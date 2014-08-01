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
import static org.testng.Assert.assertNull;

/**
 * Created by William.Randall on 8/1/2014.
 */
@Test
public class AddOrUpdateServiceTest
{
    private AddOrUpdateService addOrUpdateService;
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
        AddService addService = new AddService(openDQProperties, addressNormalizationService);

        MatchOrUpdateService matchOrUpdateService = new MatchOrUpdateService(matchingService, updateService);
        addOrUpdateService = new AddOrUpdateService(addService, matchOrUpdateService);
    }

    @Test
    public void testAddOrUpdate() throws ConnectException
    {
        Person updatePerson = TestPeople.createPersonForUpdate();

        for(Address address : updatePerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }

        List<OafResponse> responseList = addOrUpdateService.addOrUpdate(updatePerson);

        assertNotNull(responseList);
        assertEquals(responseList.size(), 1);
        assertEquals(responseList.get(0).getAction(), Action.UPDATE.toString());
        assertEquals(responseList.get(0).getConfidenceLevel(), 1.0D);
        assertEquals(responseList.get(0).getMatchId(), updatePerson.getId());

        Person addPerson = TestPeople.createPersonForAdd();

        for(Address address : addPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }

        List<OafResponse> addResponse = addOrUpdateService.addOrUpdate(addPerson);
        assertNull(addResponse);
    }
}
