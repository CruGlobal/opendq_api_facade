package org.cru.webservices;

import org.cru.service.DeleteService;

import javax.inject.Inject;
import javax.jws.WebParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Created by William.Randall on 6/10/14.
 */
@Path("/")
public class DeleteResource
{
    @Inject
    private DeleteService deleteService;

    @Path("/delete/{id}")
    @DELETE
    public Response deletePerson(@WebParam(name = "id") String globalRegistryId)
    {
        try
        {
            deleteService.deletePerson(globalRegistryId, "test5");
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
