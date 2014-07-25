package org.cru.webservices;

import com.google.common.collect.Lists;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.service.AddOrUpdateService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.List;

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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdatePerson(String json)
    {
        Person person = personDeserializer.deserializePerson(json);

        try
        {
            OafResponse addOrUpdateResponse = addOrUpdateService.addOrUpdate(person);

            if(addOrUpdateResponse == null)
            {
                return Response.ok().entity(buildResponseEntity(person.getId())).build();
            }
            else if(addOrUpdateResponse.getAction().equals(Action.CONFLICT.toString()))
            {
                addOrUpdateResponse.setAction(Action.UPDATE);
                return Response.status(Response.Status.CONFLICT).entity(Lists.newArrayList(addOrUpdateResponse)).build();
            }
            else
            {
                return Response.ok().entity(Lists.newArrayList(addOrUpdateResponse)).build();
            }
        }
        catch(ConnectException ce)
        {
            return Response.serverError().entity(ce.getMessage()).build();
        }
    }

    private List<OafResponse> buildResponseEntity(String id)
    {
        OafResponse addResponse = new OafResponse();
        addResponse.setConfidenceLevel(1.0D);
        addResponse.setMatchId(id);
        addResponse.setAction(Action.ADD);
        return Lists.newArrayList(addResponse);
    }
}
