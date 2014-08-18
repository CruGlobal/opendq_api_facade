package org.cru.service;

import org.cru.data.TestPeople;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.util.OafProperties;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

/**
 * Test the {@link AddService} class.
 *
 * Created by William.Randall on 6/9/14.
 */
@Test
public class AddServiceTest
{
    private AddService addService;
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();
        OafProperties oafProperties = new OafProperties();
        oafProperties.init();

        addressNormalizationService = mock(AddressNormalizationService.class);
        NicknameService nicknameService = new NicknameService(openDQProperties);
        addService = new AddService(openDQProperties, addressNormalizationService, nicknameService);
    }

    @Test
    public void testAddPerson()
    {
        try
        {
            Person testPerson = TestPeople.generatePersonWithLotsOfData();

            for(Address address : testPerson.getAddresses())
            {
                when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
            }

            addService.addPerson(testPerson, "Add");
        }
        catch(ConnectException ce)
        {
            fail(ce.getMessage());
        }
        catch(RuntimeException re)
        {
            fail(re.getMessage());
        }
    }
}
