package org.cru.model;

import org.cru.mdm.MdmConstants;

/**
 * Created by William.Randall on 6/20/14.
 */
public class EmailAddress
{
    private String mdmCommunicationId;
    private String id;
    private String email;

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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
