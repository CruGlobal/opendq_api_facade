package org.cru.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.cru.deserialization.PersonDeserializer;
import org.cru.mdm.MdmConstants;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This object holds information that is contained within the index about a person
 *
 * Created by William.Randall on 6/6/14.
 */
@JsonDeserialize(using = PersonDeserializer.class)
public class Person
{
    private String mdmPartyId;
    private String mdmPersonId;
    private String mdmPersonAttributesId;

    private String globalRegistryId;
    private List<EmailAddress> emailAddresses;
    private List<Address> addresses;
    private String gender;
    private String accountNumber;
    private List<PhoneNumber> phoneNumbers;
    private String clientIntegrationId;
    private Authentication authentication;
    private LinkedIdentities linkedIdentities;
    private DateTime clientUpdatedAt;

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

    public String getMdmPersonAttributesId()
    {
        if(mdmPersonAttributesId == null) return MdmConstants.JUNK_ID;
        return mdmPersonAttributesId;
    }

    public void setMdmPersonAttributesId(String mdmPersonAttributesId)
    {
        this.mdmPersonAttributesId = mdmPersonAttributesId;
    }

    public String getGlobalRegistryId()
    {
        return globalRegistryId;
    }

    public void setGlobalRegistryId(String globalRegistryId)
    {
        this.globalRegistryId = globalRegistryId;
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

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public LinkedIdentities getLinkedIdentities()
    {
        return linkedIdentities;
    }

    public void setLinkedIdentities(LinkedIdentities linkedIdentities)
    {
        this.linkedIdentities = linkedIdentities;
    }

    public DateTime getClientUpdatedAt()
    {
        return clientUpdatedAt;
    }

    public void setClientUpdatedAt(DateTime clientUpdatedAt)
    {
        this.clientUpdatedAt = clientUpdatedAt;
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
