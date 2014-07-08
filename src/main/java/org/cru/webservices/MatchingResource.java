package org.cru.webservices;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.cru.model.Address;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Match;
import org.cru.service.AddressNormalizationService;
import org.cru.service.MatchingService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.ConnectException;

/**
 * Endpoint for clients to find a matching {@link Person} in the index
 *
 * Created by William.Randall on 6/6/14.
 */
@Path("/")
public class MatchingResource
{
    @Inject
    private AddressNormalizationService addressNormalizationService;
    @Inject @Match
    private MatchingService matchingService;

    @SuppressWarnings("unused")  //used by Clients
    @POST
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMatchingPerson(String json)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Person person;

        try
        {
            person = objectMapper.readValue(json, Person.class);
        }
        catch(JsonParseException jpe)
        {
            throw new WebApplicationException(
                Response.serverError().entity(jpe.getMessage()).build()
            );
        }
        catch(JsonMappingException jme)
        {
            throw new WebApplicationException(
                Response.serverError().entity(jme.getMessage()).build()
            );
        }
        catch(IOException ioe)
        {
            throw new WebApplicationException(
                Response.serverError().entity(ioe.getMessage()).build()
            );
        }

        if(person == null) throw new WebApplicationException("Failed to create Person object.");

        for(Address personAddress : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(personAddress);
        }

        try
        {
            //We have a clean address, person's address is already updated
            MatchResponse matchResponse = matchingService.findMatch(person, "Match");

            if(matchResponse != null)
            {
                //Send the match back to the client
                return Response.ok().entity(matchResponse).build();
            }
            else
            {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch(ConnectException ce)
        {
            throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ce.getMessage())
                .build());
        }
    }
}
