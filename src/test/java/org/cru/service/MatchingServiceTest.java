package org.cru.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.data.TestPeople;
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
        matchingService = new MatchingService(openDQProperties, deleteService, nicknameService);
    }

    @DataProvider
    private Object[][] validMatches()
    {
        return new Object[][] {
            { TestPeople.createPersonForGrInIndex(), 1 },
            { TestPeople.createPersonFromSoapUITestData(), 2 },
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

    @DataProvider
    private Object[][] globalRegistryIdsInMdm()
    {
        return new Object[][] {
            { "0004a598-e0de-11e3-82af-12768b82bfd5" },
            { "0004A598-E0DE-11E3-82AF-12768B82BFD5" },
            { "74e97ae1-18f3-11e4-8c21-0800200c9a67" },
            { "74e97ae1-18f3-11e4-8c21-0800200c9a67".toUpperCase() },
            { "3ikfj32-8rt4-9493-394nfa2348da" }
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
        Person testPerson = TestPeople.createPersonWithoutAddress();
        testPerson.setPhoneNumbers(null);
        SearchResponseList searchResponseList = matchingService.findPersonInIndexUsingEmail(testPerson);

        assertNotNull(searchResponseList);
        assertEquals(searchResponseList.get(0).getId(), testPerson.getId());
    }

    @Test
    public void testFindPersonInIndexUsingPhoneNumber() throws ConnectException
    {
        //Person's phone number is: (706) 968-8967
        //Person has 2 records with different GR IDs: 1-1E6-4616 and 002a294e-e057-11e3-af9a-12768b82bfd5
        List<String> possibleIds = ImmutableList.of("1-1E6-4616", "002a294e-e057-11e3-af9a-12768b82bfd5");

        //The first format of phone number is 7069688967
        Person testPerson = TestPeople.createPersonWithoutAddress();
        testPerson.setEmailAddresses(null);
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
        format2.setNumber("706-968-8967");
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
        format3.setNumber("(706) 968-8967");
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
        format4.setNumber("706.968.8967");
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
        format5.setNumber("706/968-8967");
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
        Person testPersonWithAllData = TestPeople.createPersonForSearchTypeTesting();

        Person testPersonWithNoAddress = TestPeople.createPersonForSearchTypeTesting();
        testPersonWithNoAddress.setAddresses(null);

        Person testPersonWithNoAddressOrEmail = TestPeople.createPersonForSearchTypeTesting();
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

        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, slotName);
        assertNotNull(matchResponseList);
        assertEquals(matchResponseList.get(0).getMatchId(), testPerson.getId());
        assertEquals(matchingService.stepName, expectedStepName);
    }
}
