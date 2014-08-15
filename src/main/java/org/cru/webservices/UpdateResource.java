package org.cru.webservices;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.service.AuthService;
import org.cru.service.MatchOrUpdateService;
import org.cru.service.PersonDeserializationService;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.List;

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
    private PersonDeserializationService personDeserializationService;
    @Inject
    private AuthService authService;

    @SuppressWarnings("unused")  //used by Clients
    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateIndex(String json, @Context HttpHeaders httpHeaders)
    {
        if(!authService.hasAccess(httpHeaders)) return authService.notAuthorized(httpHeaders);
        
        Person person = personDeserializationService.deserializePerson(json);

        if(Strings.isNullOrEmpty(person.getId()))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Global Registry ID is required for update").build();
        }

        try
        {
            List<OafResponse> matchOrUpdateResponseList = matchOrUpdateService.matchOrUpdatePerson(person);

            if(matchOrUpdateResponseList == null || matchOrUpdateResponseList.isEmpty())
            {
                throw new WebApplicationException(
                    Response.serverError()
                    .entity("Could not find Person with global registry id: " + person.getId())
                    .build());
            }
            //A conflict will only have 1 in the list
            else if(matchOrUpdateResponseList.get(0).getAction().equals(Action.CONFLICT.toString()))
            {
                matchOrUpdateResponseList.get(0).setAction(Action.UPDATE);
                return Response.status(Response.Status.CONFLICT).entity(matchOrUpdateResponseList).build();
            }

            return Response.ok().entity(matchOrUpdateResponseList).build();
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
