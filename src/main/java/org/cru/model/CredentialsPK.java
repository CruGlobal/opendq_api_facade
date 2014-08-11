package org.cru.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by William.Randall on 8/11/2014.
 */
@Embeddable
public class CredentialsPK implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "system_name")
    private String systemName;

    @Column(name = "system_key")
    private String systemKey;

    public String getSystemName()
    {
        return systemName;
    }

    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }

    public String getSystemKey()
    {
        return systemKey;
    }

    public void setSystemKey(String systemKey)
    {
        this.systemKey = systemKey;
    }
}
