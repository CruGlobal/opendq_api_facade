package org.cru.service;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.cru.model.ClientData;
import org.cru.model.Person;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Helper class to deserialize a JSON string into a Person object.
 *
 * Created by William.Randall on 7/8/2014.
 */
public class PersonDeserializationService
{
    private static Logger log = Logger.getLogger(PersonDeserializationService.class);

    public Person deserializePerson(String json)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Person person;

        try
        {
            ClientData data = objectMapper.readValue(json, ClientData.class);
            person = data.getPerson();
        }
        catch(JsonParseException jpe)
        {
            log.error("Failed to parse JSON: " + json, jpe);
            throw new WebApplicationException(
                Response.serverError().entity(jpe.getMessage()).build()
            );
        }
        catch(JsonMappingException jme)
        {
            log.error("Failed to map JSON: " + json, jme);
            throw new WebApplicationException(
                Response.serverError().entity(jme.getMessage()).build()
            );
        }
        catch(IOException ioe)
        {
            log.error("Failed to read JSON", ioe);
            throw new WebApplicationException(
                Response.serverError().entity(ioe.getMessage()).build()
            );
        }

        if(person == null)
        {
            log.error("Failed to create Person object from JSON: " + json);
            throw new WebApplicationException("Failed to create Person object.");
        }

        return person;
    }
}
