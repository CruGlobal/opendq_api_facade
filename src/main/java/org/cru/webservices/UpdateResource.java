package org.cru.webservices;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.service.MatchOrUpdateService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
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
    private MatchOrUpdateService matchOrUpdateService;
    @Inject
    private PersonDeserializer personDeserializer;

    @SuppressWarnings("unused")  //used by Clients
    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateIndex(String json)
    {
        Person person = personDeserializer.deserializePerson(json);
        try
        {
            MatchResponse matchOrUpdateResponse = matchOrUpdateService.matchOrUpdatePerson(person);

            if(matchOrUpdateResponse == null)
            {
                throw new WebApplicationException(
                    Response.serverError()
                    .entity("Could not find Person with global registry id: " + person.getId())
                    .build());
            }
            else if(matchOrUpdateResponse.getAction().equals(Action.CONFLICT.toString()))
            {
                matchOrUpdateResponse.setAction(Action.UPDATE);
                return Response.status(Response.Status.CONFLICT).entity(matchOrUpdateResponse).build();
            }

            return Response.ok().entity(matchOrUpdateResponse).build();
        }
        catch(ConnectException ce)
        {
            throw new WebApplicationException(
                Response.serverError()
                .entity(ce.getMessage())
                .build());
        }
    }
}
