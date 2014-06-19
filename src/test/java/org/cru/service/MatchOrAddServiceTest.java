package org.cru.service;

import org.cru.model.Address;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.model.PersonName;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.Test;

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
        when(addressNormalizationService.normalizeAddress(testPerson.getAddress())).thenReturn(false);


        MatchResponse matchResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNull(matchResponse); //it should add it first

        matchResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), testPerson.getRowId());  //now it should find it
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();

        Address address = new Address();
        address.setAddressLine1("AddOrMatch Line 1");
        address.setCity("Indianapolis");

        PersonName personName = new PersonName();
        personName.setFirstName("AddOrMatch");
        personName.setLastName("AddOrMatchLastName");

        testPerson.setAddress(address);
        testPerson.setName(personName);
        testPerson.setRowId("5");

        return testPerson;
    }
}
