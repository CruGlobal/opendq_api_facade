package org.cru.service;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.cru.model.Person;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Helper class to deserialize a JSON string into a Person object.
 *
 * Created by William.Randall on 7/8/2014.
 */
public class PersonDeserializer
{
    public Person deserializePerson(String json)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        Person person;
        json = removeSurroundingObjectIfNecessary(json);

        try
        {
            person = objectMapper.readValue(json, Person.class);
        }
        catch(JsonParseException jpe)
        {
            throw new WebApplicationException(
                Response.serverError().entity(jpe.getMessage()).build()
            );
        }
        catch(JsonMappingException jme)
        {
            throw new WebApplicationException(
                Response.serverError().entity(jme.getMessage()).build()
            );
        }
        catch(IOException ioe)
        {
            throw new WebApplicationException(
                Response.serverError().entity(ioe.getMessage()).build()
            );
        }

        if(person == null) throw new WebApplicationException("Failed to create Person object.");

        return person;
    }

    private String removeSurroundingObjectIfNecessary(String json)
    {
        //The parsing will not work if the json is in the format {"person": {...}} so let's remove it
        if(json.contains("{\"person\":"))
        {
            json = json.replace("{\"person\":", "");
            json = json.substring(0, json.lastIndexOf("}"));
        }
        return json;
    }
}
