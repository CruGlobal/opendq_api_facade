package org.cru.webservices;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.service.MatchOrAddService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

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
 * Endpoint to first try to find a matching {@link Person}.  If found, return the found person.
 * Otherwise, add the {@link Person} to the index.
 *
 * Created by William.Randall on 6/9/14.
 */
@Path("/")
public class MatchOrAddResource
{
    @Inject
    private MatchOrAddService matchOrAddService;
    @Inject
    private PersonDeserializer personDeserializer;

    @SuppressWarnings("unused")  //used by Clients
    @POST
    @Path("/match-or-add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchOrAddPerson(String json)
    {
        Person person = personDeserializer.deserializePerson(json);
        try
        {
            MatchResponse matchResponse = matchOrAddService.matchOrAddPerson(person);

            if(matchResponse != null)
            {
                //Send the match back to the client
                return Response.ok().entity(matchResponse).build();
            }
            else
            {
                return Response.ok().entity(buildResponseEntity(person.getId())).build();
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

    private MatchResponse buildResponseEntity(String id)
    {
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setConfidenceLevel(1.0D);
        matchResponse.setMatchId(id);
        matchResponse.setAction(Action.ADD);
        return matchResponse;
    }
}
