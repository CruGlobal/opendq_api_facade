package org.cru.webservices;

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
    public Response findMatchingPerson(Person person)
    {
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
