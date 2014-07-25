package org.cru.webservices;

import com.google.common.collect.Lists;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.qualifiers.Delete;
import org.cru.qualifiers.Match;
import org.cru.service.DeleteService;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.List;

import org.cru.service.MatchingService;
import org.cru.service.PersonDeserializer;
import org.cru.util.Action;

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

    @SuppressWarnings("unused")  //used by Clients
    @Path("/delete")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePerson(String json)
    {
        Person person = personDeserializer.deserializePerson(json);
        try
        {
            SearchResponse foundIndex = matchingService.searchForPerson(person, "findPersonForDelete");
            deleteService.deletePerson(foundIndex.getId(), foundIndex);
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

    private List<OafResponse> buildResponseEntity(String id)
    {
        OafResponse deleteResponse = new OafResponse();
        deleteResponse.setConfidenceLevel(1.0D);
        deleteResponse.setMatchId(id);
        deleteResponse.setAction(Action.DELETE);
        return Lists.newArrayList(deleteResponse);
    }
}
