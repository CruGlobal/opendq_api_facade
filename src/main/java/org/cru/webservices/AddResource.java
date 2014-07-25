package org.cru.webservices;

import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;
import org.cru.service.AddService;
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
 * Endpoint to add a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
@Path("/")
public class AddResource
{
    @Inject @Add
    private AddService addService;
    @Inject
    private PersonDeserializer personDeserializer;

    @SuppressWarnings("unused")  //used by Clients
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(String json)
    {
        Person person = personDeserializer.deserializePerson(json);

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

        return Response.ok().entity(buildResponseEntity(person.getId())).build();
    }

    private OafResponse buildResponseEntity(String id)
    {
        OafResponse addResponse = new OafResponse();
        addResponse.setConfidenceLevel(1.0D);
        addResponse.setMatchId(id);
        addResponse.setAction(Action.ADD);
        return addResponse;
    }
}
