package org.cru.service;

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
        deleteService = new DeleteService();

        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        deleteService.setOafProperties(oafProperties);
    }

    @Test
    public void testDeletePerson() throws Exception
    {
        deleteService.deletePerson("6");
        deleteService.deletePerson("7");
        deleteService.deletePerson("8");
    }
}