package org.cru.service;

import com.beust.jcommander.internal.Lists;
import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentity;
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
            Person testPerson = TestPeople.generatePersonWithLotsOfData();

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

        LinkedIdentity linkedIdentity = new LinkedIdentity();
        linkedIdentity.setClientIntegrationId("1-43BK9");

        testPerson.setId("TEST_ID1");
        testPerson.setClientIntegrationId("1-43BK9");

        List<LinkedIdentity> identitiesList = Lists.newArrayList();
        identitiesList.add(linkedIdentity);
        testPerson.setLinkedIdentities(identitiesList);

        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("M");

        return testPerson;
    }
}
