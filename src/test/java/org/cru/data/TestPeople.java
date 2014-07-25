package org.cru.data;

import com.google.common.collect.Lists;
import org.cru.model.Address;
import org.cru.model.Authentication;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentity;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.model.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class to create {@link Person} objects that are shared among
 * the different tests.
 *
 * Created by William.Randall on 7/17/2014.
 */
public class TestPeople
{
    public static Person generatePersonWithLotsOfData()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setId("kses34223-dk43-9493-394nfa2348d1");
        testAddress.setAddressLine1("1125 Blvd Way");
        testAddress.setCity("Las Vegas");
        testAddress.setState("NV");
        testAddress.setZipCode("84253");
        testAddress.setCountry("USA");

        Address testAddress2 = new Address();
        testAddress2.setId("65a4sdf4-dk43-9493-394nfa2348d1");
        testAddress2.setAddressLine1("5499 Lake Dr");
        testAddress2.setCity("Juneau");
        testAddress2.setState("AK");
        testAddress2.setZipCode("99954");
        testAddress2.setCountry("USA");

        List<Address> addresses = Lists.newArrayList();
        addresses.add(testAddress);
        addresses.add(testAddress2);

        testPerson.setTitle("Ms.");
        testPerson.setFirstName("Nom");
        testPerson.setLastName("Nom");

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("nom.nom@crutest.org");
        emailAddress.setId("kses34223-dk43-9493-394nfa2348d2");
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        emailAddresses.add(emailAddress);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("5555555553");
        phoneNumber.setLocation("work");
        phoneNumber.setId("kses34223-dk43-9493-394nfa2348d3");
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(phoneNumber);

        LinkedIdentity linkedIdentity = new LinkedIdentity();
        linkedIdentity.setClientIntegrationId("1-6T4D4");

        Authentication authentication = new Authentication();
        authentication.setRelayGuid("Re1ay-6u1d");

        testPerson.setId("3ikfj32-8rt4-9493-394nfa2348da");
        testPerson.setClientIntegrationId("1-6T4D4");
        testPerson.setLinkedIdentities(Lists.newArrayList(linkedIdentity));
        testPerson.setAuthentication(authentication);

        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("F");

        Source testSource = new Source();
        testSource.setClientIntegrationId("1-6T4D4");
        testSource.setSystemId("OAF");

        testPerson.setSource(testSource);

        return testPerson;
    }

    public static Person createPersonFromSoapUITestData()
    {
        Person testPerson = new Person();

        testPerson.setFirstName("Susan");
        testPerson.setLastName("Snowa");
        testPerson.setMdmPartyId("100");
        testPerson.setId("SUSAN"); //TODO: Replace with real global registry id when available

        Address testAddress = new Address();
        testAddress.setAddressLine1("2824 McManaway Dr");
        testAddress.setCity("Midlothian");
        testAddress.setState("VA");

        testPerson.setAddresses(Lists.newArrayList(testAddress));

        return testPerson;
    }
}
