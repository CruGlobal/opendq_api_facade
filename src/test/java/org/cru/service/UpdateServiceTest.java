package org.cru.service;

import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests the {@link UpdateService} class
*
* Created by William.Randall on 6/25/14.
*/
@Test
public class UpdateServiceTest
{
    private UpdateService updateService;
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        addressNormalizationService = mock(AddressNormalizationService.class);

        updateService = new UpdateService(openDQProperties, addressNormalizationService);
    }

    @Test
    public void testUpdatePerson() throws Exception
    {
        Person testPerson = TestPeople.createTestPersonGotMarried();

        for(Address address : testPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }
        updateService.updatePerson(testPerson, TestPeople.createMockMdmPerson(), "Update");
    }
}
