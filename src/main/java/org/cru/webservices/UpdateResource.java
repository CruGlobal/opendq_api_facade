package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.AddService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Endpoint for updating the index of a {@link Person}
 *
 * Created by William.Randall on 6/12/14.
 */
@Path("/")
public class UpdateResource
{
    @Inject
    private AddService addService;

    @Path("/update")
    @POST
    public Response updateIndex(Person person)
    {
        try
        {
            //Since there is currently no way to update an existing one, just add a new one
            addService.addPerson(person, "UpdateIndex");
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
