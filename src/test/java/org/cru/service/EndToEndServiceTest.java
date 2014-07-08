package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.cru.util.PostalsoftServiceProperties;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Test Match, Add, and Delete services together.
 * This only works if the index is rebuilt between runs.
 *
 * Created by William.Randall on 6/12/14.
 */
@Test
public class EndToEndServiceTest
{
    private String filename;
    private MatchingService matchingService;
    private MatchOrAddService matchOrAddService;
    private DeleteService deleteService;
    private MatchOrUpdateService matchOrUpdateService;

    @BeforeMethod
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        PostalsoftServiceProperties postalsoftServiceProperties = new PostalsoftServiceProperties();
        PostalsoftServiceWrapperProducer postalsoftServiceWrapperProducer = new PostalsoftServiceWrapperProducer();
        postalsoftServiceWrapperProducer.setPostalsoftServiceProperties(postalsoftServiceProperties);
        postalsoftServiceWrapperProducer.init();
        PostalsoftServiceWrapper postalsoftServiceWrapper = postalsoftServiceWrapperProducer.getPostalsoftServiceWrapper();

        AddressNormalizationService addressNormalizationService = new AddressNormalizationService(postalsoftServiceWrapper);

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);

        deleteService = new DeleteService(deletedIndexesFileIO);
        AddService addService = new AddService(openDQProperties, addressNormalizationService);
        matchingService = new MatchingService(openDQProperties, deleteService);
        matchOrAddService = new MatchOrAddService(matchingService, addService);
        UpdateService updateService = new UpdateService(openDQProperties, addressNormalizationService);
        matchOrUpdateService = new MatchOrUpdateService(matchingService, updateService);

        filename = oafProperties.getProperty("deletedIndexFile");
    }

    @AfterClass
    public void tearDown()
    {
        File deletedIndexFile = new File(filename);
        if(!deletedIndexFile.delete())
        {
            System.out.println("Did not delete the file!");
        }
    }

    @Test
    public void endToEndServiceTest() throws Exception
    {
        Person testPerson = createTestPerson();
        MatchResponse matchResponse = null;

        //The person should not exist yet
        checkPersonNotExists(testPerson);

        //This will add the person and return null
        checkAddPersonWorked(testPerson);

        //Now that the person is added, we should find the person
        checkFindPersonWithMatchOrAdd(testPerson);

        //The matching service by itself should also find the person
        checkFindPersonWithMatch(testPerson);

        //We should be able to find the index data by Global Registry ID now
        SearchResponse foundIndex = checkFindById(testPerson.getId());
        String partyId = (String) foundIndex.getResultValues().get("partyId");

        //Make an update to the person
        //TODO: For now, the update will not work properly because it will find the same row and think it is a conflict
//        checkUpdatePerson(testPerson, partyId);

        //Now we delete the person from the index
        deleteService.deletePerson(testPerson.getId(), foundIndex);

        //The person should not be found anymore
        checkPersonNotExists(testPerson);

        //The record in MDM should have been "deleted"
        checkPersonDeletedInMdm(testPerson, partyId);
    }

    private void checkPersonNotExists(Person testPerson) throws Exception
    {
        MatchResponse matchResponse = matchingService.findMatch(testPerson, "Match");
        assertNull(matchResponse);
    }

    private void checkAddPersonWorked(Person testPerson) throws Exception
    {
        assertNull(matchOrAddService.matchOrAddPerson(testPerson));
    }

    private void checkFindPersonWithMatchOrAdd(Person testPerson) throws Exception
    {
        MatchResponse matchResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), testPerson.getId());
    }

    private void checkFindPersonWithMatch(Person testPerson) throws Exception
    {
        MatchResponse matchResponse = matchingService.findMatch(testPerson, "Match");
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), testPerson.getId());
    }

    private SearchResponse checkFindById(String id) throws Exception
    {
        SearchResponse foundIndex = matchingService.findMatchById(id, "MatchId");
        assertNotNull(foundIndex);
        assertNotNull(foundIndex.getResultValues().get("partyId"));
        assertEquals(foundIndex.getId(), id);
        return foundIndex;
    }

    private void checkUpdatePerson(Person testPerson, String partyId) throws Exception
    {
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("e2e@blah.com");
        emailAddress.setId("443432");
        emailAddresses.add(emailAddress);
        testPerson.setEmailAddresses(emailAddresses);

        MatchResponse updateResponse = matchOrUpdateService.matchOrUpdatePerson(testPerson);
        assertNull(updateResponse);  //will be null if updated, not null if it found a conflicting match

        //We should now find the updates
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdm(partyId);
        assertNotNull(foundPerson);
        assertNotNull(foundPerson.getObjectCommunications());
        assertEquals(foundPerson.getObjectCommunications().getObjectCommunication().size(), 1);
    }

    private void checkPersonDeletedInMdm(Person testPerson, String partyId) throws Exception
    {
        RealTimeObjectActionDTO deletedPerson = matchingService.findMatchInMdm(partyId);
        assertNotNull(deletedPerson);
        //The record in MDM should have an Action value of "D"
        assertEquals(deletedPerson.getObjectEntity().getAction(), "D");
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setAddressLine1("E2E4");
        testAddress.setAddressLine2("E2E5");
        testAddress.setAddressLine3("E2E6");
        testAddress.setCity("Dallas");
        testAddress.setState("TX");
        testAddress.setZipCode("38437");
        testAddress.setCountry("USA");

        testPerson.setTitle("Mr.");
        testPerson.setFirstName("EE3");
        testPerson.setLastName("EE4");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);
        testPerson.setAddresses(addresses);
        testPerson.setId("afd65af4-hj546fg-xn51rg-5asdf4");

        return testPerson;
    }
}
