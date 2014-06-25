package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.DeleteService;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import org.cru.util.ResponseMessage;

/**
 * Endpoint to delete a {@link Person} from the index
 *
 * Created by William.Randall on 6/10/14.
 */
@Path("/")
public class DeleteResource
{
    @Inject
    private DeleteService deleteService;

    @SuppressWarnings("unused")  //used by Clients
    @Path("/delete/{id}")
    @DELETE
    public Response deletePerson(@PathParam("id") String globalRegistryId)
    {
        try
        {
            deleteService.deletePerson(globalRegistryId);
        }
        catch(ConnectException ce)
        {
            throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ce.getMessage())
                .build());
        }

        return Response.ok().entity(ResponseMessage.DELETED.getMessage()).build();
    }
}
