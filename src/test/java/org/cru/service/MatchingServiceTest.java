package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentities;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.model.PersonName;
import org.cru.model.PhoneNumber;
import org.cru.model.SearchResponse;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
        return new Object[][] {
            { generatePersonWithLotsOfData(), "3ikfj32-8rt4-9493-394nfa2348da"}
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

    @Test
    public void testMatchHasBeenDeleted() throws Exception
    {
        Person deletedPerson = new Person();

        Address address = new Address();
        address.setAddressLine1("4 Quarter Ln");
        address.setCity("Austin");

        PersonName personName = new PersonName();
        personName.setFirstName("Pandemic");
        personName.setLastName("Handy");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        deletedPerson.setAddresses(addresses);
        deletedPerson.setName(personName);
        deletedPerson.setGlobalRegistryId("6");

        MatchResponse matchResponse = matchingService.findMatch(deletedPerson, "Match");
        assertNull(matchResponse);
    }

    @Test
    public void testFindMatchById() throws Exception
    {
        SearchResponse searchResponse = matchingService.findMatchById("3ikfj32-8rt4-9493-394nfa2348da", "MatchId");
        assertNotNull(searchResponse);
        assertEquals(searchResponse.getId(), "3ikfj32-8rt4-9493-394nfa2348da");
    }

    @Test
    public void testFindMatchInMdm() throws Exception
    {
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdm("886");

        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectEntity());
        assertNotNull(foundPerson.getObjectAddresses());
        assertNotNull(foundPerson.getObjectCommunications());
        assertNotNull(foundPerson.getObjectAttributeDatas());
    }

    @Test
    public void testFindDeletedInMdm() throws Exception
    {
        RealTimeObjectActionDTO deletedPerson = matchingService.findMatchInMdm("1");
        assertEquals(deletedPerson.getObjectEntity().getAction(), "D");
    }

    private Person generatePersonWithDataExactMatchFromSoapUI()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setAddressLine1("1211 Wee Dr");
        testAddress.setCity("Orlando");

        PersonName personName = new PersonName();
        personName.setFirstName("Test");
        personName.setLastName("LastNameTest");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);
        testPerson.setAddresses(addresses);
        testPerson.setName(personName);

        return testPerson;
    }

    private Person generatePersonWithDataSimilarMatchFromSoapUI()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setAddressLine1("1211 Wee Dr");
        testAddress.setCity("Orlando");

        PersonName personName = new PersonName();
        personName.setFirstName("Testy");
        personName.setLastName("LastNameTest");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);
        testPerson.setAddresses(addresses);
        testPerson.setName(personName);

        return testPerson;
    }

    private Person generatePersonWithDataExactMatchFromJavaTest()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setAddressLine1("100 Lake Hart Dr");
        testAddress.setCity("Orlando");

        PersonName personName = new PersonName();
        personName.setFirstName("Bill");
        personName.setLastName("Randall");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);
        testPerson.setAddresses(addresses);
        testPerson.setName(personName);

        return testPerson;
    }

    private Person generatePersonWithLotsOfData()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setId("kses34223-dk43-9493-394nfa2348d1");
        testAddress.setAddressLine1("1125 Blvd Way");
        testAddress.setCity("Las Vegas");
        testAddress.setState("NV");
        testAddress.setZipCode("84253");
        testAddress.setCountry("USA");
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);

        PersonName personName = new PersonName();
        personName.setTitle("Ms.");
        personName.setFirstName("Nom");
        personName.setLastName("Nom");

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("nom.nom@crutest.org");
        emailAddress.setId("kses34223-dk43-9493-394nfa2348d2");
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        emailAddresses.add(emailAddress);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("5555555553");
        phoneNumber.setLocation("work");
        phoneNumber.setId("kses34223-dk43-9493-394nfa2348d3");
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(phoneNumber);

        LinkedIdentities linkedIdentities = new LinkedIdentities();
        linkedIdentities.setSiebelContactId("1-6T4D4");

        testPerson.setGlobalRegistryId("3ikfj32-8rt4-9493-394nfa2348da");
        testPerson.setClientIntegrationId("221568");
        testPerson.setLinkedIdentities(linkedIdentities);

        testPerson.setName(personName);
        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("F");

        return testPerson;
    }
}
