package org.cru.webservices;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Delete;
import org.cru.qualifiers.Match;
import org.cru.service.AuthService;
import org.cru.service.DeleteService;
import org.cru.service.MatchingService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Endpoint to delete a {@link Person} from the index
 *
 * Created by William.Randall on 6/10/14.
 */
@Path("/")
public class DeleteResource
{
    @Inject @Delete
    private DeleteService deleteService;
    @Inject @Match
    private MatchingService matchingService;
    @Inject
    private PersonDeserializer personDeserializer;
    @Inject
    private AuthService authService;

    @SuppressWarnings("unused")  //used by Clients
    @Path("/delete/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonByGlobalRegistryId(@PathParam("id") String globalRegistryId, @Context HttpHeaders httpHeaders)
    {
        if(!authService.hasAccess(httpHeaders)) return authService.notAuthorized(httpHeaders);
        if(Strings.isNullOrEmpty(globalRegistryId))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Global Registry ID is required for delete").build();
        }
        deleteService.deletePerson(globalRegistryId, matchingService.findMatchInMdmByGlobalRegistryId(globalRegistryId));
        return Response.ok().entity(buildResponseEntity(globalRegistryId)).build();
    }

    private List<OafResponse> buildResponseEntity(String id)
    {
        OafResponse deleteResponse = new OafResponse();
        deleteResponse.setConfidenceLevel(1.0D);
        deleteResponse.setMatchId(id);
        deleteResponse.setAction(Action.DELETE);
        return Lists.newArrayList(deleteResponse);
    }
}
