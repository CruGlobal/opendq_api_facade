package org.cru.service;

import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.model.PersonName;
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
            Person testPerson = createTestPerson();

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

        PersonName personName = new PersonName();
        personName.setFirstName("Test");
        personName.setLastName("LastNameTest");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);
        testPerson.setAddresses(addresses);
        testPerson.setName(personName);
        testPerson.setId("TEST_ROW_ID1");

        return testPerson;
    }
}
