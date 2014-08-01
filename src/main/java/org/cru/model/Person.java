package org.cru.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.cru.deserialization.DateTimeDeserializer;
import org.cru.mdm.MdmConstants;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

/**
 * This object holds information that is contained within the index about a person
 *
 * Created by William.Randall on 6/6/14.
 */
public class Person
{
    private String mdmPartyId;
    private String mdmPersonId;
    private Map<String, String> mdmPersonAttributesIdMap;

    private String id;  // Global Registry ID
    @JsonProperty("email_address")
    private List<EmailAddress> emailAddresses;
    @JsonProperty("address")
    private List<Address> addresses;
    private String gender;
    private String accountNumber;
    @JsonProperty("phone_number")
    private List<PhoneNumber> phoneNumbers;
    private String clientIntegrationId;
    private Authentication authentication;
    @JsonProperty("linked_identities")
    private List<LinkedIdentity> linkedIdentities;
    private DateTime clientUpdatedAt;
    private Source source;

    //Name values
    private String firstName;
    private String middleName;
    private String lastName;
    private String title;
    private String suffix;
    private String preferredName;

    public String getMdmPartyId()
    {
        if(mdmPartyId == null) return MdmConstants.JUNK_ID;
        return mdmPartyId;
    }

    public void setMdmPartyId(String mdmPartyId)
    {
        this.mdmPartyId = mdmPartyId;
    }

    public String getMdmPersonId()
    {
        if(mdmPersonId == null) return MdmConstants.JUNK_ID;
        return mdmPersonId;
    }

    public void setMdmPersonId(String mdmPersonId)
    {
        this.mdmPersonId = mdmPersonId;
    }

    public Map<String, String> getMdmPersonAttributesIdMap()
    {
        return mdmPersonAttributesIdMap;
    }

    public void setMdmPersonAttributesIdMap(Map<String, String> mdmPersonAttributesIdMap)
    {
        this.mdmPersonAttributesIdMap = mdmPersonAttributesIdMap;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<Address> getAddresses()
    {
        return addresses;
    }

    public void setAddresses(List<Address> addresses)
    {
        this.addresses = addresses;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public List<EmailAddress> getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EmailAddress> emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

    public List<PhoneNumber> getPhoneNumbers()
    {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers)
    {
        this.phoneNumbers = phoneNumbers;
    }

    public String getClientIntegrationId()
    {
        return clientIntegrationId;
    }

    public void setClientIntegrationId(String clientIntegrationId)
    {
        this.clientIntegrationId = clientIntegrationId;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    @JsonDeserialize(contentAs = Authentication.class)
    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public List<LinkedIdentity> getLinkedIdentities()
    {
        return linkedIdentities;
    }

    public void setLinkedIdentities(List<LinkedIdentity> linkedIdentities)
    {
        this.linkedIdentities = linkedIdentities;
    }

    public DateTime getClientUpdatedAt()
    {
        return clientUpdatedAt;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setClientUpdatedAt(DateTime clientUpdatedAt)
    {
        this.clientUpdatedAt = clientUpdatedAt;
    }

    public Source getSource()
    {
        return source;
    }

    @JsonDeserialize(contentAs = Source.class)
    public void setSource(Source source)
    {
        this.source = source;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public String getPreferredName()
    {
        return preferredName;
    }

    public void setPreferredName(String preferredName)
    {
        this.preferredName = preferredName;
    }
}
