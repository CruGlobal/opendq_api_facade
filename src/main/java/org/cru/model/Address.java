package org.cru.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.cru.mdm.MdmConstants;

/**
 * This object holds different address information that we capture for normalization and matching purposes
 *
 * Created by William.Randall on 6/6/14.
 */
public class Address
{
    private String mdmAddressId;

    private String id;
    @JsonProperty("address_1")
    private String addressLine1;
    @JsonProperty("address_2")
    private String addressLine2;
    @JsonProperty("address_3")
    private String addressLine3;
    @JsonProperty("address_4")
    private String addressLine4;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private boolean normalized;

    public String getMdmAddressId()
    {
        if(mdmAddressId == null) return MdmConstants.JUNK_ID;
        return mdmAddressId;
    }

    public void setMdmAddressId(String mdmAddressId)
    {
        this.mdmAddressId = mdmAddressId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAddressLine1()
    {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2()
    {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3()
    {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3)
    {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine4()
    {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4)
    {
        this.addressLine4 = addressLine4;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public boolean isNormalized()
    {
        return normalized;
    }

    public void setNormalized(boolean normalized)
    {
        this.normalized = normalized;
    }
}
