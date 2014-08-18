package org.cru.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by William.Randall on 7/8/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientData
{
    private Person person;

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }
}
