package org.cru.webservices;

import org.cru.model.Person;
import org.cru.service.AddressNormalizationService;
import org.cru.service.MatchingService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Endpoint for clients to find a matching {@link Person} in the index
 *
 * Created by William.Randall on 6/6/14.
 */
@Path("/")
public class MatchingResource
{
    @Inject
    private AddressNormalizationService addressNormalizationService;
    @Inject
    private MatchingService matchingService;

    @GET
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMatchingPerson(Person person)
    {
        if(addressNormalizationService.normalizeAddress(person.getAddress()))
        {
            try
            {
                //We have a clean address, person's address is already updated
                String matchId = matchingService.findMatch(person, "Match");

                if(matchId != null)
                {
                    //Send the matching ID back to the client
                    return Response.ok().entity(matchId).build();
                }
                else
                {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
            }
            catch(ConnectException ce)
            {
                //TODO: log error
                throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ce.getMessage())
                    .build());
            }
        }
        else
        {
            //TODO: Do we want to fail here or just use the address given to us?
        }
        return Response.ok().build();
    }
}
