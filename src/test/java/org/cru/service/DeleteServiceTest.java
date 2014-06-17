package org.cru.service;

import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the {@link DeleteService} class
 *
 * Created by William.Randall on 6/11/14.
 */
@Test
public class DeleteServiceTest
{
    private DeleteService deleteService;

    @BeforeClass
    public void setup()
    {
        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        deleteService = new DeleteService(deletedIndexesFileIO);
    }

    @Test
    public void testDeletePerson() throws Exception
    {
        deleteService.deletePerson("6");
        deleteService.deletePerson("7");
        deleteService.deletePerson("8");
    }
}
