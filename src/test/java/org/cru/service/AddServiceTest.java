package org.cru.service;

import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentities;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

/**
 * Test the {@link AddService} class.
 *
 * Created by William.Randall on 6/9/14.
 */
@Test
public class AddServiceTest
{
    private AddService addService;
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        addressNormalizationService = mock(AddressNormalizationService.class);
        addService = new AddService(openDQProperties, addressNormalizationService);
    }

    @Test
    public void testAddPerson()
    {
        try
        {
            Person testPerson = generatePersonWithLotsOfData();

            for(Address address : testPerson.getAddresses())
            {
                when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
            }

            addService.addPerson(testPerson, "Add");
        }
        catch(ConnectException ce)
        {
            fail(ce.getMessage());
        }
        catch(RuntimeException re)
        {
            fail(re.getMessage());
        }
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setAddressLine1("100 Lake Hart Dr");
        testAddress.setCity("Orlando");
        testAddress.setState("FL");
        testAddress.setZipCode("32832");
        testAddress.setCountry("USA");
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);

        testPerson.setTitle("Mr.");
        testPerson.setFirstName("Test");
        testPerson.setLastName("LastNameTest");

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("test.lastNameTest@crutest.org");
        emailAddress.setId("1394218");
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        emailAddresses.add(emailAddress);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("5555555555");
        phoneNumber.setLocation("mobile");
        phoneNumber.setId("1394218");
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(phoneNumber);

        LinkedIdentities linkedIdentities = new LinkedIdentities();
        linkedIdentities.setSiebelContactId("1-43BK9");

        testPerson.setId("TEST_ID1");
        testPerson.setClientIntegrationId("1394218");
        testPerson.setLinkedIdentities(linkedIdentities);

        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("M");

        return testPerson;
    }

    private Person generatePersonWithLotsOfData()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setId("kses34223-dk43-9493-394nfa2348d1");
        testAddress.setAddressLine1("1125 Blvd Way");
        testAddress.setCity("Las Vegas");
        testAddress.setState("NV");
        testAddress.setZipCode("84253");
        testAddress.setCountry("USA");
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);

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

        LinkedIdentities linkedIdentities = new LinkedIdentities();
        linkedIdentities.setSiebelContactId("1-6T4D4");

        testPerson.setId("3ikfj32-8rt4-9493-394nfa2348da");
        testPerson.setClientIntegrationId("221568");
        testPerson.setLinkedIdentities(linkedIdentities);

        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("F");

        return testPerson;
    }
}
