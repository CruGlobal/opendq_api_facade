package org.cru.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.model.collections.SearchResponseList;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Test the {@link MatchingService} class with exact and near matches
 *
 * Created by William.Randall on 6/9/14.
 */
@Test
public class MatchingServiceTest
{
    private MatchingService matchingService;

    @BeforeMethod
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        DeleteService deleteService = new DeleteService(deletedIndexesFileIO, openDQProperties);
        NicknameService nicknameService = new NicknameService(openDQProperties);
        AddressNormalizationService addressNormalizationService = mock(AddressNormalizationService.class);
        when(addressNormalizationService.normalizeAddress(any(Address.class))).thenReturn(false);
        matchingService = new MatchingService(openDQProperties, deleteService, nicknameService, addressNormalizationService);
    }

    @DataProvider
    private Object[][] validMatches()
    {
        return new Object[][] {
            { TestPeople.createPersonForGrInIndex(), 1 },
            { TestPeople.createPersonFromSoapUITestData(), 4 },
            { TestPeople.generatePersonWithLotsOfData(), 2 }
        };
    }

    @Test(dataProvider = "validMatches")
    public void testFindMatches(Person testPerson, int numMatches) throws ConnectException
    {
        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, "contactMatch");
        assertNotNull(matchResponseList);
        assertEquals(matchResponseList.size(), numMatches);
        assertEquals(matchResponseList.get(0).getMatchId(), testPerson.getId());
    }

    @Test
    public void testMatchHasBeenDeleted() throws Exception
    {
        Person deletedPerson = TestPeople.createTestPersonHasBeenDeleted();

        List<OafResponse> matchResponseList = matchingService.findMatches(deletedPerson, "Match");
        assertNull(matchResponseList);
    }

    @Test
    public void testFindMatchInMdm() throws Exception
    {
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdm("19754423");

        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertNotNull(foundPerson.getObjectCommunications());
        assertNotNull(foundPerson.getObjectAttributeDatas());

        // Person with multiple communications and attribute data rows
        foundPerson = matchingService.findMatchInMdm("19754423");
        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertNotNull(foundPerson.getObjectCommunications());
        assertEquals(foundPerson.getObjectCommunications().getObjectCommunication().size(), 4);
        assertNotNull(foundPerson.getObjectAttributeDatas());
        assertEquals(foundPerson.getObjectAttributeDatas().getObjectAttributeData().size(), 4);

        // Person with multiple addresses
        foundPerson = matchingService.findMatchInMdm("19754423");
        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertEquals(foundPerson.getObjectAddresses().getObjectAddress().size(), 7);
        assertNotNull(foundPerson.getObjectCommunications());
        assertNotNull(foundPerson.getObjectAttributeDatas());
    }

    @DataProvider
    private Object[][] globalRegistryIdsInMdm()
    {
        return new Object[][] {
            { "3958d652-1fa9-11e4-b22e-12543788cf06" }
        };
    }

    @Test(dataProvider = "globalRegistryIdsInMdm")
    public void testFindMatchInMdmByGlobalRegistryId(String globalRegistryId) throws Exception
    {
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(globalRegistryId);

        assertNotNull(foundPerson);
        System.out.println("Party Id for " + globalRegistryId + ": " + foundPerson.getObjectEntity().getPartyId());
    }

    @Test
    public void testFindPersonInIndex() throws ConnectException
    {
        matchingService.slotName = "contactMatch";
        matchingService.stepName = "RtMatchAddr";
        Person testPerson = TestPeople.createPersonForGrInIndex();
        SearchResponseList searchResponseList = matchingService.findPersonInIndex(testPerson);

        assertNotNull(searchResponseList);
        assertEquals(searchResponseList.size(), 1);
        assertEquals(searchResponseList.get(0).getId(), testPerson.getId());
    }

    @Test
    public void testFindPersonInIndexUsingEmail() throws ConnectException
    {
        Person testPerson = TestPeople.createPersonForSearchTypeTesting2();
        testPerson.setPhoneNumbers(null);

        List<String> possibleIds = ImmutableList.of("1-2XF-1994", testPerson.getId());

        SearchResponseList searchResponseList = matchingService.findPersonInIndexUsingEmail(testPerson);

        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
    }

    @Test
    public void testFindPersonInIndexUsingPhoneNumber() throws ConnectException
    {
        //Person's phone number is: (765) 532-1510

        //The first format of phone number is 7069688967
        Person testPerson = TestPeople.createPersonForSearchTypeTesting2();
        testPerson.setAddresses(null);
        testPerson.setEmailAddresses(null);

        //Person has 2 records with different GR IDs
        List<String> possibleIds = ImmutableList.of("1-2XF-1994", testPerson.getId());

        SearchResponseList searchResponseList = matchingService.findPersonInIndexUsingPhoneNumber(testPerson);

        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
        assertTrue(searchResponseList.get(0).getScore() > 0.95D);
        System.out.println(
            "Phone Number: " + testPerson.getPhoneNumbers().get(0).getNumber() +
            "; ID: " + searchResponseList.get(0).getId() +
            "; Score: " + searchResponseList.get(0).getScore()
        );

        PhoneNumber format2 = new PhoneNumber();
        format2.setNumber("765-532-1510");
        format2.setLocation("home");
        testPerson.setPhoneNumbers(Lists.newArrayList(format2));

        searchResponseList = matchingService.findPersonInIndexUsingPhoneNumber(testPerson);
        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
        assertTrue(searchResponseList.get(0).getScore() > 0.95D);
        System.out.println(
            "Phone Number: " + testPerson.getPhoneNumbers().get(0).getNumber() +
                "; ID: " + searchResponseList.get(0).getId() +
                "; Score: " + searchResponseList.get(0).getScore()
        );

        PhoneNumber format3 = new PhoneNumber();
        format3.setNumber("(765) 532-1510");
        format3.setLocation("home");
        testPerson.setPhoneNumbers(Lists.newArrayList(format3));

        searchResponseList = matchingService.findPersonInIndexUsingPhoneNumber(testPerson);
        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
        assertTrue(searchResponseList.get(0).getScore() > 0.95D);
        System.out.println(
            "Phone Number: " + testPerson.getPhoneNumbers().get(0).getNumber() +
                "; ID: " + searchResponseList.get(0).getId() +
                "; Score: " + searchResponseList.get(0).getScore()
        );

        PhoneNumber format4 = new PhoneNumber();
        format4.setNumber("765.532.1510");
        format4.setLocation("home");
        testPerson.setPhoneNumbers(Lists.newArrayList(format4));

        searchResponseList = matchingService.findPersonInIndexUsingPhoneNumber(testPerson);
        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
        assertTrue(searchResponseList.get(0).getScore() > 0.95D);
        System.out.println(
            "Phone Number: " + testPerson.getPhoneNumbers().get(0).getNumber() +
                "; ID: " + searchResponseList.get(0).getId() +
                "; Score: " + searchResponseList.get(0).getScore()
        );

        PhoneNumber format5 = new PhoneNumber();
        format5.setNumber("765/532-1510");
        format5.setLocation("home");
        testPerson.setPhoneNumbers(Lists.newArrayList(format5));

        searchResponseList = matchingService.findPersonInIndexUsingPhoneNumber(testPerson);
        assertNotNull(searchResponseList);
        assertTrue(possibleIds.contains(searchResponseList.get(0).getId()));
        assertTrue(searchResponseList.get(0).getScore() > 0.95D);
        System.out.println(
            "Phone Number: " + testPerson.getPhoneNumbers().get(0).getNumber() +
                "; ID: " + searchResponseList.get(0).getId() +
                "; Score: " + searchResponseList.get(0).getScore()
        );
    }

    @DataProvider
    public Object[][] matchTypeData()
    {
        Person testPersonWithAllData = TestPeople.createPersonForSearchTypeTesting2();

        Person testPersonWithNoAddress = TestPeople.createPersonForSearchTypeTesting2();
        testPersonWithNoAddress.setAddresses(null);

        Person testPersonWithNoAddressOrEmail = TestPeople.createPersonForSearchTypeTesting2();
        testPersonWithNoAddressOrEmail.setAddresses(null);
        testPersonWithNoAddressOrEmail.setEmailAddresses(null);

        return new Object[][] {
            { testPersonWithAllData, "RtMatchAddr" },
            { testPersonWithNoAddress, "RtMatchComm" },
            { testPersonWithNoAddressOrEmail, "RtMatchComm" }
        };
    }

    @Test(dataProvider = "matchTypeData")
    public void testFindMatchesDifferentTypes(Person testPerson, String expectedStepName) throws ConnectException
    {
        String slotName = "Match";

        List<String> possibleIds = ImmutableList.of("1-2XF-1994", testPerson.getId());

        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, slotName);
        assertNotNull(matchResponseList);
        assertTrue(possibleIds.contains(matchResponseList.get(0).getMatchId()));
        assertEquals(matchingService.stepName, expectedStepName);
    }
}
