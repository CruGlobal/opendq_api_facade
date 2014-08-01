package org.cru.service;

import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.util.Action;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by William.Randall on 7/31/2014.
 */
@Test
public class MatchOrUpdateServiceTest
{
    private MatchOrUpdateService matchOrUpdateService;
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup() throws Exception
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        addressNormalizationService = mock(AddressNormalizationService.class);

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        DeleteService deleteService = new DeleteService(deletedIndexesFileIO, openDQProperties);
        UpdateService updateService = new UpdateService(openDQProperties, addressNormalizationService);
        MatchingService matchingService = new MatchingService(openDQProperties, deleteService);
        matchOrUpdateService = new MatchOrUpdateService(matchingService, updateService);
    }

    @Test
    public void testMatchOrUpdatePerson() throws ConnectException
    {
        Person testPerson = TestPeople.createPersonForUpdate();

        for(Address address : testPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }

        List<OafResponse> oafResponseList = matchOrUpdateService.matchOrUpdatePerson(testPerson);

        assertNotNull(oafResponseList);
        assertEquals(oafResponseList.size(), 1);
        assertEquals(oafResponseList.get(0).getMatchId(), testPerson.getId());
        assertEquals(oafResponseList.get(0).getConfidenceLevel(), 1.0D);
        assertEquals(oafResponseList.get(0).getAction(), Action.UPDATE.toString());
    }
}
