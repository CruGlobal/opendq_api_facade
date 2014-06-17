package org.cru.service;

import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.model.Address;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.cru.util.PostalsoftServiceProperties;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

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

        MatchingService matchingService = new MatchingService(openDQProperties, oafProperties);
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
