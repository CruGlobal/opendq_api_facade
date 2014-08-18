package org.cru.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Model to hold source data.  It should look like this:
 * "source": {"system_id": "String", "client_integration_id": "String"}
 *
 * Created by William.Randall on 7/24/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source
{
    String systemId;
    String clientIntegrationId;

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
}
