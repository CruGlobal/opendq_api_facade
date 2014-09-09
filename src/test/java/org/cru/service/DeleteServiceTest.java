package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * Tests the {@link DeleteService} class
 *
 * Created by William.Randall on 6/11/14.
 */
@Test
public class DeleteServiceTest
{
    private DeleteService deleteService;
    private OafProperties oafProperties;

    @DataProvider
    private Object[][] notFoundPersons()
    {
        return new Object[][] {
            { "6", createPerson("-50") },
            { "7", createPerson("-45") },
            { "8", createPerson("-99") }
        };
    }

    @DataProvider
    private Object[][] foundPersons()
    {
        return new Object[][] {
            { "3ikfj32-8rt4-9493-394nfa2348da", createPerson("11541581") },
        };
    }

    @BeforeClass
    public void setup()
    {
        oafProperties = new OafProperties();
        oafProperties.init();

        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        deleteService = new DeleteService(deletedIndexesFileIO, openDQProperties);
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        File deletedIndexesFile = new File(oafProperties.getProperty("deletedIndexFile"));
        if(deletedIndexesFile.exists())
        {
            if(!deletedIndexesFile.delete()) throw new Exception("File was not successfully deleted!");
        }
    }

    @Test(dataProvider = "notFoundPersons")
    public void testDeletePersonNotFound(String id, RealTimeObjectActionDTO person) throws Exception
    {
        try
        {
            deleteService.deletePerson(id, person);
        }
        catch(WebApplicationException we)
        {
            assertEquals(we.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
            assertTrue(((String)we.getResponse().getEntity()).contains("not found"));
        }
    }

    @Test(dataProvider = "foundPersons")
    public void testDeletePersonFound(String id, RealTimeObjectActionDTO foundPerson)
    {
        try
        {
            deleteService.deletePerson(id, foundPerson);
        }
        catch(WebApplicationException we)
        {
            fail();
        }
    }

    private RealTimeObjectActionDTO createPerson(String partyId)
    {
        RealTimeObjectActionDTO foundPerson = new RealTimeObjectActionDTO();

        ObjEntityDTO objEntity = new ObjEntityDTO();
        objEntity.setPartyId(partyId);

        foundPerson.setObjectEntity(objEntity);
        return foundPerson;
    }
}
