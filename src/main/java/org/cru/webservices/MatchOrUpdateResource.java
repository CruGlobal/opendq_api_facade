package org.cru.webservices;

import org.cru.model.Person;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint for doing either a match or update of an existing {@link Person}
 * 
 * Created by William.Randall on 6/16/14.
 */
@Path("/")
public class MatchOrUpdateResource
{
    @Path("/match-or-update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchOrUpdatePerson(Person person)
    {
        return Response.ok().build();
    }
}
