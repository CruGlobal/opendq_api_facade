package org.cru.model;

/**
 * Created by William.Randall on 8/1/2014.
 */
public class PersonAttributeDataId
{
    private String attributeDataType;  //Maps to MultDetTypeLev2
    private String secondaryIdentifier;  //this will be different for each type

    public String getAttributeDataType()
    {
        return attributeDataType;
    }

    public void setAttributeDataType(String attributeDataType)
    {
        this.attributeDataType = attributeDataType;
    }

    public String getSecondaryIdentifier()
    {
        return secondaryIdentifier;
    }

    public void setSecondaryIdentifier(String secondaryIdentifier)
    {
        this.secondaryIdentifier = secondaryIdentifier;
    }
}
