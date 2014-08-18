package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.data.TestPeople;
import org.cru.model.EmailAddress;
import org.cru.model.OafResponse;
import org.cru.model.Person;
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
import static org.testng.Assert.fail;

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

        deleteService = new DeleteService(deletedIndexesFileIO, openDQProperties);
        matchingService = new MatchingService(openDQProperties, deleteService);
        NicknameService nicknameService = new NicknameService(openDQProperties);
        AddService addService = new AddService(openDQProperties, addressNormalizationService, nicknameService);
        matchOrAddService = new MatchOrAddService(matchingService, addService);
        UpdateService updateService = new UpdateService(openDQProperties, addressNormalizationService, nicknameService);
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
        Person testPerson = TestPeople.createPersonForEndToEndTest();

        //The person should not exist yet
        checkPersonNotExists(testPerson);

        //This will add the person and return null
        checkAddPersonWorked(testPerson);

        //Now that the person is added, we should find the person
        checkFindPersonWithMatchOrAdd(testPerson);

        //The matching service by itself should also find the person
        checkFindPersonWithMatch(testPerson);

        //We should be able to find the person by Global Registry ID now
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(testPerson.getId());
        assertNotNull(foundPerson);
        String partyId = foundPerson.getObjectEntity().getPartyId();

        //Make an update to the person
        //TODO: For now, the update will not work properly because it will find the same row and think it is a conflict
        checkUpdatePerson(testPerson, partyId);

        //Now we delete the person from the index
        deleteService.deletePerson(testPerson.getId(), foundPerson);

        //The person should not be found anymore
        checkPersonNotExists(testPerson);

        //The record in MDM should have been "deleted"
        checkPersonDeletedInMdm(testPerson, partyId);
    }

    private void checkPersonNotExists(Person testPerson) throws Exception
    {
        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, "Match");
        if(matchResponseList != null && !matchResponseList.isEmpty())
        {
            for(OafResponse matchResponse : matchResponseList)
            {
                if(matchResponse.getConfidenceLevel() >= 1.0D) fail();
            }
        }
    }

    private void checkAddPersonWorked(Person testPerson) throws Exception
    {
        assertNull(matchOrAddService.matchOrAddPerson(testPerson));
    }

    private void checkFindPersonWithMatchOrAdd(Person testPerson) throws Exception
    {
        List<OafResponse> matchOrAddResponseList = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchOrAddResponseList);
        assertEquals(matchOrAddResponseList.get(0).getMatchId(), testPerson.getId());
    }

    private void checkFindPersonWithMatch(Person testPerson) throws Exception
    {
        List<OafResponse> matchResponseList = matchingService.findMatches(testPerson, "Match");
        assertNotNull(matchResponseList);
        assertEquals(matchResponseList.get(0).getMatchId(), testPerson.getId());
    }

    private void checkUpdatePerson(Person testPerson, String partyId) throws Exception
    {
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("e2e@blah.com");
        emailAddress.setId("443432");
        emailAddresses.add(emailAddress);
        testPerson.setEmailAddresses(emailAddresses);

        List<OafResponse> updateResponseList = matchOrUpdateService.matchOrUpdatePerson(testPerson);
        assertNull(updateResponseList);  //will be null if updated, not null if it found a conflicting match

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
}
