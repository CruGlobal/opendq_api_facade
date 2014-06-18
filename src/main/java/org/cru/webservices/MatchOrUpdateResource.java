package org.cru.webservices;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.service.MatchOrUpdateService;
import org.cru.util.ResponseMessage;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Endpoint for doing either a match or update of an existing {@link Person}
 *
 * Created by William.Randall on 6/16/14.
 */
@Path("/")
public class MatchOrUpdateResource
{
    @Inject
    private MatchOrUpdateService matchOrUpdateService;

    @POST
    @Path("/match-or-update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchOrUpdatePerson(Person person)
    {
        try
        {
            MatchResponse matchOrUpdateResponse = matchOrUpdateService.matchOrUpdatePerson(person);

            if(matchOrUpdateResponse == null)
            {
                return Response.ok().entity(ResponseMessage.UPDATED.getMessage()).build();
            }
            else if(matchOrUpdateResponse.getMessage().equals(ResponseMessage.CONFLICT.getMessage()))
            {
                return Response.status(Response.Status.CONFLICT).entity(matchOrUpdateResponse).build();
            }
        }
        catch(ConnectException ce)
        {
            throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ce.getMessage())
                .build());
        }
        return Response.ok().build();
    }
}
