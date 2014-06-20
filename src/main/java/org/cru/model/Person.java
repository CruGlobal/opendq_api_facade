package org.cru.model;

import java.util.List;

/**
 * This object holds information that is contained within the index about a person
 *
 * Created by William.Randall on 6/6/14.
 */
public class Person
{
    private String id;
    private List<EmailAddress> emailAddresses;
    private PersonName name;
    private Address address;
    private String rowId;
    private String gender;
    private String employeeNumber;
    private String accountNumber;
    private String employeeRelayId;
    private String relayId;
    private PhoneNumber phoneNumber;
    private String clientIntegrationId;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public PersonName getName()
    {
        return name;
    }

    public void setName(PersonName name)
    {
        this.name = name;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public String getRowId()
    {
        return rowId;
    }

    public void setRowId(String rowId)
    {
        this.rowId = rowId;
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

    public PhoneNumber getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber)
    {
        this.phoneNumber = phoneNumber;
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
