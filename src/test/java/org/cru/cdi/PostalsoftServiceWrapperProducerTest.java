package org.cru.cdi;

import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.PostalsoftServiceProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Test the {@link PostalsoftServiceWrapperProducer} class
 *
 * Created by William.Randall on 6/6/14.
 */
@Test
public class PostalsoftServiceWrapperProducerTest
{
    private PostalsoftServiceWrapperProducer producer = new PostalsoftServiceWrapperProducer();
    private PostalsoftServiceProperties properties;

    @BeforeClass
    public void setup()
    {
        properties = new PostalsoftServiceProperties();
    }

    @Test
    public void testInitialize()
    {
        producer.setPostalsoftServiceProperties(properties);
        producer.init();
        PostalsoftServiceWrapper wrapper = producer.getPostalsoftServiceWrapper();

        assertNotNull(wrapper);
    }
}
