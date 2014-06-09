package org.cru.webservices;

import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.service.AddressNormalizationService;
import org.cru.service.MatchingService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
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
    public Response findMatchingPerson(Person person)
    {
        if(addressNormalizationService.normalizeAddress(person.getAddress()))
        {
            //We have a clean address, person's address is already updated
            String matchId = matchingService.findMatch(person);

            if(matchId != null)
            {
                //Send the matching ID back to the client
                return Response.ok().entity(matchId).build();
            }
            else
            {
                //TODO: Return no match to the client
            }
        }
        else
        {
            //TODO: Do we want to fail here or just use the address given to us?
        }
        return Response.ok().build();
    }
}
