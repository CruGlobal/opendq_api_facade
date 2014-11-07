package org.cru.webservices;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Match;
import org.cru.service.AuthService;
import org.cru.service.MatchingService;
import org.cru.service.PersonDeserializationService;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.List;

/**
 * Endpoint for clients to find a matching {@link Person} in the index
 *
 * Created by William.Randall on 6/6/14.
 */
@Path("/")
public class MatchingResource
{
    @Inject @Match
    private MatchingService matchingService;
    @Inject
    private PersonDeserializationService personDeserializationService;
    @Inject
    private AuthService authService;

    private static Logger log = Logger.getLogger(MatchingResource.class);

    @SuppressWarnings("unused")  //used by Clients
    @POST
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMatchingPerson(String json, @Context HttpHeaders httpHeaders)
    {
        if(!authService.hasAccess(httpHeaders)) return authService.notAuthorized(httpHeaders);

        Person person = personDeserializationService.deserializePerson(json);

        try
        {
            //We have a clean address, person's address is already updated
            List<OafResponse> matchResponseList = matchingService.findMatches(person, "Match");

            if(matchResponseList != null && !matchResponseList.isEmpty())
            {
                //Send the match back to the client
                return Response.ok().entity(matchResponseList).build();
            }
            else
            {
                log.info("No match found for person: " + person.getFirstName() + " " + person.getLastName());
                return Response.status(Response.Status.NOT_FOUND).entity(buildResponseEntity()).build();
            }
        }
        catch(ConnectException ce)
        {
            log.error("Connection problem while trying to match", ce);
            throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ce.getMessage())
                .build());
        }
    }

    private List<OafResponse> buildResponseEntity()
    {
        OafResponse matchResponse = new OafResponse();
        matchResponse.setConfidenceLevel(0.0D);
        matchResponse.setMatchId("Not Found");
        matchResponse.setAction(Action.MATCH);
        return Lists.newArrayList(matchResponse);
    }
}
