package org.cru.webservices;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.service.AddOrUpdateService;
import org.cru.service.AuthService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
    @Inject
    private AuthService authService;

    private static Logger log = Logger.getLogger(AddOrUpdateResource.class);

    @SuppressWarnings("unused")  //used by Clients
    @Path("/add-or-update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdatePerson(String json, @Context HttpHeaders httpHeaders)
    {
        if(!authService.hasAccess(httpHeaders)) return authService.notAuthorized(httpHeaders);

        Person person = personDeserializer.deserializePerson(json);

        try
        {
            List<OafResponse> addOrUpdateResponseList = addOrUpdateService.addOrUpdate(person);

            if(addOrUpdateResponseList == null)
            {
                return Response.ok().entity(buildResponseEntity(person.getId())).build();
            }
            //A conflict will only have 1 in the list
            else if(addOrUpdateResponseList.get(0).getAction().equals(Action.CONFLICT.toString()))
            {
                addOrUpdateResponseList.get(0).setAction(Action.UPDATE);
                log.debug("Conflict occurred while updating person with GR ID: " + person.getId());
                return Response.status(Response.Status.CONFLICT).entity(addOrUpdateResponseList).build();
            }
            else
            {
                return Response.ok().entity(addOrUpdateResponseList).build();
            }
        }
        catch(ConnectException ce)
        {
            log.error("Connection problem while trying to add or update", ce);
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
