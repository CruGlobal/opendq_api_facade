package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.MatchOrAddService;

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
 * Created by William.Randall on 6/9/14.
 */
@Path("/")
public class MatchOrAddResource
{
    @Inject
    private MatchOrAddService matchOrAddService;

    @POST
    @Path("/match-or-add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchOrAddPerson(Person person)
    {
        try
        {
            matchOrAddService.matchOrAddPerson(person);
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
