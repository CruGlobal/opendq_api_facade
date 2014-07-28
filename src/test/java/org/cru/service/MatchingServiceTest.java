package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.data.TestPeople;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.util.Action;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test the {@link MatchingService} class with exact and near matches
 *
 * Created by William.Randall on 6/9/14.
 */
@Test
public class MatchingServiceTest
{
    private MatchingService matchingService;

    @DataProvider(name = "successfulMatches")
    private Object[][] successfulMatches()
    {
        return new Object[][] {
            { TestPeople.generatePersonWithLotsOfData(), "3ikfj32-8rt4-9493-394nfa2348da"}
        };
    }

    @BeforeMethod
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        DeleteService deleteService = new DeleteService(deletedIndexesFileIO);
        matchingService = new MatchingService(openDQProperties, deleteService);
    }

    @Test
    public void testFindMatch() throws ConnectException
    {
        Person testPerson = TestPeople.createPersonFromSoapUITestData();
        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, "contactMatch");

        assertNotNull(matchResponseList);
        assertEquals(matchResponseList.size(), 1);
        assertEquals(matchResponseList.get(0).getMatchId(), testPerson.getId());
        assertEquals(matchResponseList.get(0).getAction(), Action.MATCH.toString());

        testPerson = TestPeople.generatePersonWithLotsOfData();
        matchResponseList = matchingService.findMatches(testPerson, "contactMatch");
        assertEquals(matchResponseList.size(), 2); //Two different addresses
    }

    @Test
    public void testSearchForPerson() throws ConnectException
    {
        Person testPerson = TestPeople.createPersonFromSoapUITestData();
        SearchResponse searchResponse = matchingService.searchForPerson(testPerson, "contactMatch");

        assertNotNull(searchResponse);
        assertEquals(searchResponse.getId(), testPerson.getId());
        assertEquals(searchResponse.getResultValues().get("partyId"), testPerson.getMdmPartyId());
        assertEquals(searchResponse.getResultValues().get("address1"), testPerson.getAddresses().get(0).getAddressLine1());

        testPerson = TestPeople.generatePersonWithLotsOfData();
        searchResponse = matchingService.searchForPerson(testPerson, "contactMatch");

        assertNotNull(searchResponse);
        assertEquals(searchResponse.getId(), testPerson.getId());
    }

    @Test
    public void testMatchHasBeenDeleted() throws Exception
    {
        Person deletedPerson = TestPeople.createTestPersonHasBeenDeleted();

        List<OafResponse> matchResponseList = matchingService.findMatches(deletedPerson, "Match");
        assertEquals(matchResponseList.size(), 0);
    }

    @Test
    public void testFindMatchInMdm() throws Exception
    {
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdm("37539");

        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertNotNull(foundPerson.getObjectCommunications());
        assertNotNull(foundPerson.getObjectAttributeDatas());

        // Person with multiple communications and attribute data rows
        foundPerson = matchingService.findMatchInMdm("11239883");
        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertNotNull(foundPerson.getObjectCommunications());
        assertEquals(foundPerson.getObjectCommunications().getObjectCommunication().size(), 2);
        assertNotNull(foundPerson.getObjectAttributeDatas());
        assertEquals(foundPerson.getObjectAttributeDatas().getObjectAttributeData().size(), 7);

        // Person with multiple addresses
        foundPerson = matchingService.findMatchInMdm("37539");
        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertEquals(foundPerson.getObjectAddresses().getObjectAddress().size(), 2);
        assertNotNull(foundPerson.getObjectCommunications());
        assertNotNull(foundPerson.getObjectAttributeDatas());
    }
}
