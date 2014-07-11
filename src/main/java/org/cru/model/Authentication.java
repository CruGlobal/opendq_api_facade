package org.cru.model;

/**
 * Created by William.Randall on 6/27/14.
 */
public class Authentication
{
    private String relayGuid;
    private String employeeRelayGuid;

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
}
