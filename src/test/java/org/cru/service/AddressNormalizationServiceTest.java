package org.cru.service;

import org.cru.cdi.PostalsoftServiceWrapperProducer;
import org.cru.model.Address;
import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.PostalsoftServiceProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the {@link AddressNormalizationService} class
 *
 * Created by William.Randall on 6/6/14.
 */

@Test
public class AddressNormalizationServiceTest
{
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup()
    {
        PostalsoftServiceProperties postalsoftServiceProperties = new PostalsoftServiceProperties();
        postalsoftServiceProperties.init();

        PostalsoftServiceWrapperProducer postalsoftServiceWrapperProducer = new PostalsoftServiceWrapperProducer();
        postalsoftServiceWrapperProducer.setPostalsoftServiceProperties(postalsoftServiceProperties);
        postalsoftServiceWrapperProducer.init();
        PostalsoftServiceWrapper postalsoftServiceWrapper = postalsoftServiceWrapperProducer.getPostalsoftServiceWrapper();

        postalsoftServiceWrapper.setPostalsoftServiceProperties(postalsoftServiceProperties);
        addressNormalizationService = new AddressNormalizationService();
        addressNormalizationService.setPostalsoftServiceWrapper(postalsoftServiceWrapper);
    }

    @Test
    public void testNormalizeAddress()
    {
        Address preNormalizedAddress = createNormalizedWorkingAddress();
        Address addressToNormalize = createWorkingAddress();
        boolean normalized = addressNormalizationService.normalizeAddress(addressToNormalize);
        assertTrue(normalized);

        assertEquals(addressToNormalize.getAddressLine1(), preNormalizedAddress.getAddressLine1());
        assertEquals(addressToNormalize.getCity(), preNormalizedAddress.getCity());
        assertEquals(addressToNormalize.getState(), preNormalizedAddress.getState());
        assertEquals(addressToNormalize.getZipCode(), preNormalizedAddress.getZipCode());
        assertEquals(addressToNormalize.getCountry(), preNormalizedAddress.getCountry());
        assertTrue(addressToNormalize.isNormalized());
    }

    private Address createWorkingAddress()
    {
        Address workingAddress = new Address();

        workingAddress.setAddressLine1("100 lake hart drive");
        workingAddress.setCity("orlando");
        workingAddress.setState("Florida");
        workingAddress.setZipCode("32832");
        workingAddress.setCountry("USA");

        return workingAddress;
    }

    private Address createNormalizedWorkingAddress()
    {
        Address normalizedAddress = new Address();

        normalizedAddress.setAddressLine1("100 Lake Hart Dr");
        normalizedAddress.setCity("Orlando");
        normalizedAddress.setState("FL");
        normalizedAddress.setZipCode("32832-0100");
        normalizedAddress.setCountry("USA");

        return normalizedAddress;
    }
}
