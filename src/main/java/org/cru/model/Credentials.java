package org.cru.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Holds data for authorization of client systems.
 *
 * Created by William.Randall on 8/11/2014.
 */
@Entity
@Table(name = "credentials")
public class Credentials implements Serializable
{
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CredentialsPK credentialsPrimaryKey;

    public CredentialsPK getCredentialsPrimaryKey()
    {
        return credentialsPrimaryKey;
    }

    public void setCredentialsPrimaryKey(CredentialsPK credentialsPrimaryKey)
    {
        this.credentialsPrimaryKey = credentialsPrimaryKey;
    }
}
