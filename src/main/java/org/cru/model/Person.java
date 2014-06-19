package org.cru.model;

/**
 * This object holds information that is contained within the index about a person
 *
 * Created by William.Randall on 6/6/14.
 */
public class Person
{
    private PersonName name;
    private Address address;
    private String rowId;

    public PersonName getName()
    {
        return name;
    }

    public void setName(PersonName name)
    {
        this.name = name;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public String getRowId()
    {
        return rowId;
    }

    public void setRowId(String rowId)
    {
        this.rowId = rowId;
    }
}
