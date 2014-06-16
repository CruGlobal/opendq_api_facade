package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.MatchOrUpdateService;

import javax.inject.Inject;
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

    @Path("/match-or-update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchOrUpdatePerson(Person person)
    {
        try
        {
            matchOrUpdateService.matchOrUpdatePerson(person);
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
