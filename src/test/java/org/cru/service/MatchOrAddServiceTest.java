package org.cru.service;

import org.cru.model.Address;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Test for {@link MatchOrAddService} which can currently only be run once successfully
 * before changing the data because once the {@link Person} is added to the index, it
 * will be found.
 *
 * Created by William.Randall on 6/10/14.
 */
@Test
public class MatchOrAddServiceTest
{
    private MatchOrAddService matchOrAddService;
    private AddressNormalizationService addressNormalizationService;

    private void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        DeleteService deleteService = new DeleteService(deletedIndexesFileIO);

        MatchingService matchingService = new MatchingService(openDQProperties, deleteService);
        addressNormalizationService = mock(AddressNormalizationService.class);
        AddService addService = new AddService(openDQProperties, addressNormalizationService);
        matchOrAddService = new MatchOrAddService(matchingService, addService);
    }

    //NOTE: this will only work once, then the first part of the test will fail
    @Test
    public void testMatchOrAdd() throws Exception
    {
        setup();
        Person testPerson = createTestPerson();

        for(Address address : testPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }

        OafResponse matchOrAddResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNull(matchOrAddResponse); //it should add it first

        matchOrAddResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchOrAddResponse);
        assertEquals(matchOrAddResponse.getMatchId(), testPerson.getId());  //now it should find it
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();

        Address address = new Address();
        address.setAddressLine1("AddOrMatch Line 1");
        address.setCity("Indianapolis");

        testPerson.setFirstName("AddOrMatch");
        testPerson.setLastName("AddOrMatchLastName");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        testPerson.setAddresses(addresses);
        testPerson.setId("5");

        return testPerson;
    }
}
