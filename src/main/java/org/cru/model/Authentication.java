package org.cru.model;

/**
 * Created by William.Randall on 6/27/14.
 */
public class Authentication
{
    private String relayGuid;
    private String employeeRelayGuid;
    private String googleAppsUid;
    private String facebookUid;
    private String keyGuid;

    public String getRelayGuid()
    {
        return relayGuid;
    }

    public void setRelayGuid(String relayGuid)
    {
        this.relayGuid = relayGuid;
    }

    public String getEmployeeRelayGuid()
    {
        return employeeRelayGuid;
    }

    public void setEmployeeRelayGuid(String employeeRelayGuid)
    {
        this.employeeRelayGuid = employeeRelayGuid;
    }

    public String getGoogleAppsUid()
    {
        return googleAppsUid;
    }

    public void setGoogleAppsUid(String googleAppsUid)
    {
        this.googleAppsUid = googleAppsUid;
    }

    public String getFacebookUid()
    {
        return facebookUid;
    }

    public void setFacebookUid(String facebookUid)
    {
        this.facebookUid = facebookUid;
    }

    public String getKeyGuid()
    {
        return keyGuid;
    }

    public void setKeyGuid(String keyGuid)
    {
        this.keyGuid = keyGuid;
    }
}
