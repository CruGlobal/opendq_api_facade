package org.cru.service;

import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.model.Address;
import org.cru.model.MatchResponse;
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
    private AddService addService;
    private MatchingService matchingService;
    private MatchOrAddService matchOrAddService;
    private DeleteService deleteService;

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
        addService = new AddService(openDQProperties, addressNormalizationService);
        matchingService = new MatchingService(openDQProperties, deleteService);
        matchOrAddService = new MatchOrAddService(matchingService, addService);

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

        //The person should not exist yet
        MatchResponse matchResponse = matchingService.findMatch(testPerson, "Match");
        assertNull(matchResponse);

        //This will add the person and return null
        matchResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNull(matchResponse);

        //Now that the person is added, we should find the person
        matchResponse = matchOrAddService.matchOrAddPerson(testPerson);
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), testPerson.getRowId());

        //The matching service by itself should also find the person
        matchResponse = matchingService.findMatch(testPerson, "Match");
        assertNotNull(matchResponse);
        assertEquals(matchResponse.getMatchId(), testPerson.getRowId());

        //Now we delete the person from the index
        deleteService.deletePerson(testPerson.getRowId());

        //The person should not be found anymore
        matchResponse = matchingService.findMatch(testPerson, "Match");
        assertNull(matchResponse);

        //Add the person back with a new row id
        testPerson.setRowId("9");
        addService.addPerson(testPerson, "Add");

        //TODO: Make this work (currently it will still find the original match, and will see it deleted
        //We should now find the person again
//        matchResponse = matchingService.findMatch(testPerson, "Match");
//        assertEquals(matchResponse.getMatchId(), testPerson.getRowId());
//        assertTrue(matchResponse.getConfidenceLevel() >= 0.95D);
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("End To End Test Address");
        testAddress.setCity("Greenbay");

        testPerson.setFirstName("E2E");
        testPerson.setLastName("Test");
        testPerson.setRowId("8");
        testPerson.setAddress(testAddress);

        return testPerson;
    }
}
