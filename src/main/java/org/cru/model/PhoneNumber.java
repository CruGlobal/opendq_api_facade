package org.cru.model;

/**
 * Created by William.Randall on 6/20/14.
 */
public class PhoneNumber
{
    private String id;
    private String number;
    private String location; // ex: mobile

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
