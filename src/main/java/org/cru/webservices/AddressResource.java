package org.cru.webservices;

import org.cru.model.Address;
import org.cru.service.AddressNormalizationService;

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
public class AddressResource
{
    @Inject
    private AddressNormalizationService addressNormalizationService;

    @GET
    @Path("/address")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findMatchingAddress(Address address)
    {

        return Response.ok().build();
    }
}
