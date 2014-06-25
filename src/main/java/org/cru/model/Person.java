package org.cru.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.cru.deserialization.PersonDeserializer;

import java.util.List;

/**
 * This object holds information that is contained within the index about a person
 *
 * Created by William.Randall on 6/6/14.
 */
@JsonDeserialize(using = PersonDeserializer.class)
public class Person
{
    private String globalRegistryId;
    private List<EmailAddress> emailAddresses;
    private PersonName name;
    private List<Address> addresses;
    private String gender;
    private String employeeNumber;
    private String accountNumber;
    private String employeeRelayId;
    private String relayId;
    private List<PhoneNumber> phoneNumbers;
    private String clientIntegrationId;
    private String siebelContactId;

    public String getGlobalRegistryId()
    {
        return globalRegistryId;
    }

    public void setGlobalRegistryId(String globalRegistryId)
    {
        this.globalRegistryId = globalRegistryId;
    }

    public PersonName getName()
    {
        return name;
    }

    public void setName(PersonName name)
    {
        this.name = name;
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

    public String getEmployeeNumber()
    {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getEmployeeRelayId()
    {
        return employeeRelayId;
    }

    public void setEmployeeRelayId(String employeeRelayId)
    {
        this.employeeRelayId = employeeRelayId;
    }

    public String getRelayId()
    {
        return relayId;
    }

    public void setRelayId(String relayId)
    {
        this.relayId = relayId;
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

    public String getSiebelContactId()
    {
        return siebelContactId;
    }

    public void setSiebelContactId(String siebelContactId)
    {
        this.siebelContactId = siebelContactId;
    }
}
