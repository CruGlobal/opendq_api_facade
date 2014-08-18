package org.cru.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by William.Randall on 6/27/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkedIdentity
{
    private String systemId;
    private String clientIntegrationId;  // Primary key for Person from the system in question (e.g. ContactId from Siebel)
    private String employeeNumber;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public String getClientIntegrationId()
    {
        return clientIntegrationId;
    }

    public void setClientIntegrationId(String clientIntegrationId)
    {
        this.clientIntegrationId = clientIntegrationId;
    }

    public String getEmployeeNumber()
    {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }
}
