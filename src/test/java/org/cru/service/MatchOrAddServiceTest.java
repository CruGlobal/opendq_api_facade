package org.cru.service;

import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
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

    private void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        MatchingService matchingService = new MatchingService();
        matchingService.setOpenDQProperties(openDQProperties);

        AddService addService = new AddService();
        addService.setOpenDQProperties(openDQProperties);

        matchOrAddService = new MatchOrAddService();
        matchOrAddService.setMatchingService(matchingService);
        matchOrAddService.setAddService(addService);
    }

    //NOTE: this will only work once, then the first part of the test will fail
    @Test
    public void testMatchOrAdd() throws Exception
    {
        setup();
        Person testPerson = createTestPerson();
        String matchId = matchOrAddService.matchOrAddPerson(testPerson);
        assertNull(matchId); //it should add it first
        matchId = matchOrAddService.matchOrAddPerson(testPerson);
        assertEquals(matchId, testPerson.getRowId());  //now it should find it
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();
        testPerson.setRowId("5");
        testPerson.setFirstName("AddOrMatch");
        testPerson.setLastName("AddOrMatchLastName");

        Address address = new Address();
        address.setAddressLine1("AddOrMatch Line 1");
        address.setCity("Indianapolis");
        testPerson.setAddress(address);

        return testPerson;
    }
}