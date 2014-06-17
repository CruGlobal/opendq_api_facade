package org.cru.service;

import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.OpenDQProperties;
import org.cru.util.PostalsoftServiceProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;

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

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        PostalsoftServiceProperties postalsoftServiceProperties = new PostalsoftServiceProperties();

        PostalsoftServiceWrapperProducer postalsoftServiceWrapperProducer = new PostalsoftServiceWrapperProducer();
        postalsoftServiceWrapperProducer.setPostalsoftServiceProperties(postalsoftServiceProperties);
        postalsoftServiceWrapperProducer.init();
        PostalsoftServiceWrapper postalsoftServiceWrapper = postalsoftServiceWrapperProducer.getPostalsoftServiceWrapper();

        postalsoftServiceWrapper.setPostalsoftServiceProperties(postalsoftServiceProperties);
        AddressNormalizationService addressNormalizationService = new AddressNormalizationService();
        addressNormalizationService.setPostalsoftServiceWrapper(postalsoftServiceWrapper);

        addService = new AddService(openDQProperties, addressNormalizationService);
    }

    @Test
    public void testAddPerson()
    {
        try
        {
            addService.addPerson(createTestPerson(), "Add");
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

    private Person createTestPerson()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("100 Lake Hart Dr");
        testAddress.setCity("Orlando");

        testPerson.setFirstName("Test");
        testPerson.setLastName("LastNameTest");
        testPerson.setAddress(testAddress);
        testPerson.setRowId("TEST_ROW_ID1");

        return testPerson;
    }
}
