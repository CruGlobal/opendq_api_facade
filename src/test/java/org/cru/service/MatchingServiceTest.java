package org.cru.service;

import org.cru.model.Address;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

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
        Person exactMatchFromSoapUI = generatePersonWithDataExactMatchFromSoapUI();
        Person similarMatchFromSoapUI = generatePersonWithDataSimilarMatchFromSoapUI();
        Person exactMatchFromJavaTest = generatePersonWithDataExactMatchFromJavaTest();

        return new Object[][] {
            { exactMatchFromSoapUI, "3" },
            { exactMatchFromJavaTest, "2" },
            { similarMatchFromSoapUI, "3" }
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

    @Test(dataProvider = "successfulMatches")
    public void testFindMatch(Person person, String matchId) throws Exception
    {
        MatchResponse matchResponse = matchingService.findMatch(person, "Match");
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), matchId);
    }

    public void testMatchHasBeenDeleted() throws Exception
    {
        Person deletedPerson = new Person();
        Address address = new Address();
        address.setAddressLine1("4 Quarter Ln");
        address.setCity("Austin");
        deletedPerson.setAddress(address);
        deletedPerson.setFirstName("Pandemic");
        deletedPerson.setLastName("Handy");
        deletedPerson.setRowId("6");

        MatchResponse matchResponse = matchingService.findMatch(deletedPerson, "Match");
        assertNull(matchResponse);
    }

    private Person generatePersonWithDataExactMatchFromSoapUI()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("1211 Wee Dr");
        testAddress.setCity("Orlando");

        testPerson.setFirstName("Test");
        testPerson.setLastName("LastNameTest");
        testPerson.setAddress(testAddress);

        return testPerson;
    }

    private Person generatePersonWithDataSimilarMatchFromSoapUI()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("1211 Wee Dr");
        testAddress.setCity("Orlando");

        testPerson.setFirstName("Testy");
        testPerson.setLastName("LastNameTest");
        testPerson.setAddress(testAddress);

        return testPerson;
    }

    private Person generatePersonWithDataExactMatchFromJavaTest()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("100 Lake Hart Dr");
        testAddress.setCity("Orlando");

        testPerson.setFirstName("Bill");
        testPerson.setLastName("Randall");
        testPerson.setAddress(testAddress);

        return testPerson;
    }
}
