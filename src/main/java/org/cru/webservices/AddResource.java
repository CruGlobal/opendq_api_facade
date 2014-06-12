package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.AddService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Endpoint to add a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
@Path("/")
public class AddResource
{
    @Inject
    private AddService addService;

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPerson(Person person)
    {
        //TODO: Do we want to check for a match here before adding?
        try
        {
            addService.addPerson(person, "Add");
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
