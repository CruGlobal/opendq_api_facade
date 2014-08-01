package org.cru.service;

import org.cru.model.ResultData;
import org.cru.model.SearchResponse;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

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

    @DataProvider(name = "notFoundPersons")
    private Object[][] notFoundPersons()
    {
        String notFoundId1 = "6";
        String notFoundId2 = "7";
        String notFoundId3 = "8";

        return new Object[][] {
            { notFoundId1, createNotFoundSearchResponse(notFoundId1) },
            { notFoundId2, createNotFoundSearchResponse(notFoundId2) },
            { notFoundId3, createNotFoundSearchResponse(notFoundId3) }
        };
    }

    @DataProvider(name = "foundPersons")
    private Object[][] foundPersons()
    {
        String foundGRId1 = "3ikfj32-8rt4-9493-394nfa2348da";
        String foundPartyId1 = "11541581";

        return new Object[][] {
            { foundGRId1, createFoundSearchResponse(foundGRId1, foundPartyId1) },
        };
    }

    @BeforeClass
    public void setup()
    {
        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        deleteService = new DeleteService(deletedIndexesFileIO);
    }

    @Test(dataProvider = "notFoundPersons")
    public void testDeletePersonNotFound(String id, SearchResponse foundIndex) throws Exception
    {
        try
        {
            deleteService.deletePerson(id, foundIndex);
        }
        catch(WebApplicationException we)
        {
            assertEquals(we.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
            assertTrue(((String)we.getResponse().getEntity()).contains("not found"));
        }
    }

    @Test(dataProvider = "foundPersons")
    public void testDeletePersonFound(String id, SearchResponse foundIndex) throws Exception
    {
        try
        {
            deleteService.deletePerson(id, foundIndex);
        }
        catch(ConnectException ce)
        {
            fail();
        }
        catch(WebApplicationException we)
        {
            fail();
        }
    }

    private SearchResponse createNotFoundSearchResponse(String globalRegistryId)
    {
        SearchResponse foundIndex = new SearchResponse();
        foundIndex.setId(globalRegistryId);
        ResultData values = new ResultData();
        values.putPartyId("-50");
        foundIndex.setResultValues(values);
        return foundIndex;
    }

    private SearchResponse createFoundSearchResponse(String globalRegistryId, String partyId)
    {
        SearchResponse foundIndex = new SearchResponse();
        foundIndex.setId(globalRegistryId);
        ResultData values = new ResultData();
        values.putPartyId(partyId);
        foundIndex.setResultValues(values);
        return foundIndex;
    }
}
