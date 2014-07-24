package org.cru.webservices;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.service.AddOrUpdateService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Endpoint to add a {@link Person} to the index if it does not exist
 * and update it if it does exist.
 *
 * Created by William.Randall on 7/11/2014.
 */
@Path("/")
public class AddOrUpdateResource
{
    @Inject
    private AddOrUpdateService addOrUpdateService;
    @Inject
    private PersonDeserializer personDeserializer;

    @SuppressWarnings("unused")  //used by Clients
    @Path("/add-or-update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addOrUpdatePerson(String json)
    {
        Person person = personDeserializer.deserializePerson(json);

        try
        {
            MatchResponse addOrUpdateResponse = addOrUpdateService.addOrUpdate(person);

            if(addOrUpdateResponse == null)
            {
                return Response.ok().entity(Action.ADD.toString()).build();
            }
            else if(addOrUpdateResponse.getAction().equals(Action.CONFLICT.toString()))
            {
                addOrUpdateResponse.setAction(Action.UPDATE);
                return Response.status(Response.Status.CONFLICT).entity(addOrUpdateResponse).build();
            }
            else
            {
                return Response.ok().entity(addOrUpdateResponse).build();
            }
        }
        catch(ConnectException ce)
        {
            return Response.serverError().entity(ce.getMessage()).build();
        }
    }
}
