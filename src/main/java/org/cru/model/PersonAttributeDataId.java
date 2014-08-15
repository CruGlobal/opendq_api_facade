package org.cru.model;

/**
 * Used to identify a specific Person Attribute for updating the MDM database
 *
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

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        PersonAttributeDataId that = (PersonAttributeDataId) o;

        return
            attributeDataType.equals(that.attributeDataType) &&
                !(secondaryIdentifier != null ?
                    !secondaryIdentifier.equals(that.secondaryIdentifier) :
                    that.secondaryIdentifier != null);

    }

    @Override
    public int hashCode()
    {
        int result = attributeDataType.hashCode();
        result = 31 * result + (secondaryIdentifier != null ? secondaryIdentifier.hashCode() : 0);
        return result;
    }
}
