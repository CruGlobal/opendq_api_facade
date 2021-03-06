package org.cru.webservices;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.apache.log4j.Logger;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;
import org.cru.qualifiers.Match;
import org.cru.service.AddService;
import org.cru.service.AuthService;
import org.cru.service.MatchingService;
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
 * Endpoint to add a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
@Path("/")
public class AddResource
{
    @Inject @Add
    private AddService addService;
    @Inject @Match
    private MatchingService matchingService;
    @Inject
    private PersonDeserializationService personDeserializationService;
    @Inject
    private AuthService authService;

    private static Logger log = Logger.getLogger(AddResource.class);

    @SuppressWarnings("unused")  //used by Clients
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(String json, @Context HttpHeaders httpHeaders)
    {
        if(!authService.hasAccess(httpHeaders)) return authService.notAuthorized(httpHeaders);

        Person person = personDeserializationService.deserializePerson(json);

        if(Strings.isNullOrEmpty(person.getId()))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Global Registry ID is required for add").build();
        }
        try
        {
            RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(person.getId());
            if(foundPerson == null) addService.addPerson(person, "Add");
            else
            {
                log.debug("Person with GR ID: " + person.getId() + " already in the system!");
                return Response.ok().entity("Person with GR ID " + person.getId() + " already in the system!").build();
            }
        }
        catch(ConnectException ce)
        {
            log.error("Connection problem while trying to add", ce);
            throw new WebApplicationException(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ce.getMessage())
                    .build());
        }

        return Response.ok().entity(buildResponseEntity(person.getId())).build();
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
