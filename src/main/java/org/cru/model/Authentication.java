package org.cru.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Model to hold authentication data.  JSON can contain any/all of the following as objects or arrays:
 *
 * <pre>
 * "authentication": {
 *   "relay_guid": "String",
 *   "employee_relay_guid": "String",
 *   "google_apps_uid": ["String", "String", "String"],
 *   "facebook_uid": ["String"],
 *   "key_guid": "String"
 * }
 * </pre>
 *
 * Created by William.Randall on 6/27/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication
{
    @JsonProperty("relay_guid")
    private List<String> relayGuidList;
    @JsonProperty("employee_relay_guid")
    private List<String> employeeRelayGuidList;
    @JsonProperty("google_apps_uid")
    private List<String> googleAppsUidList;
    @JsonProperty("facebook_uid")
    private List<String> facebookUidList;
    @JsonProperty("key_guid")
    private List<String> keyGuidList;

    public List<String> getRelayGuidList()
    {
        return relayGuidList;
    }

    public void setRelayGuidList(List<String> relayGuidList)
    {
        this.relayGuidList = relayGuidList;
    }

    public List<String> getEmployeeRelayGuidList()
    {
        return employeeRelayGuidList;
    }

    public void setEmployeeRelayGuidList(List<String> employeeRelayGuidList)
    {
        this.employeeRelayGuidList = employeeRelayGuidList;
    }

    public List<String> getGoogleAppsUidList()
    {
        return googleAppsUidList;
    }

    public void setGoogleAppsUidList(List<String> googleAppsUidList)
    {
        this.googleAppsUidList = googleAppsUidList;
    }

    public List<String> getFacebookUidList()
    {
        return facebookUidList;
    }

    public void setFacebookUidList(List<String> facebookUidList)
    {
        this.facebookUidList = facebookUidList;
    }

    public List<String> getKeyGuidList()
    {
        return keyGuidList;
    }

    public void setKeyGuidList(List<String> keyGuidList)
    {
        this.keyGuidList = keyGuidList;
    }
}
