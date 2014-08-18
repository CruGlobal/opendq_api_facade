package org.cru.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.cru.mdm.MdmConstants;

/**
 * Created by William.Randall on 6/20/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneNumber
{
    private String mdmCommunicationId;
    private String id;
    private String number;
    private String location; // ex: mobile

    public String getMdmCommunicationId()
    {
        if(mdmCommunicationId == null) return MdmConstants.JUNK_ID;
        return mdmCommunicationId;
    }

    public void setMdmCommunicationId(String mdmCommunicationId)
    {
        this.mdmCommunicationId = mdmCommunicationId;
    }

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
