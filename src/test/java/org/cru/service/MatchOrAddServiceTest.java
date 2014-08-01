package org.cru.service;

import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.Test;

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
        DeleteService deleteService = new DeleteService(deletedIndexesFileIO, openDQProperties);

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
        Person testPerson = TestPeople.generatePersonWithLotsOfData();

        for(Address address : testPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }

        List<OafResponse> matchOrAddResponseList = matchOrAddService.matchOrAddPerson(testPerson);
        assertNull(matchOrAddResponseList); //it should add it first

        matchOrAddResponseList = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchOrAddResponseList);
        assertEquals(matchOrAddResponseList.get(0).getMatchId(), testPerson.getId());  //now it should find it
    }
}
