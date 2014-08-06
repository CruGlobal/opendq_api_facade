package org.cru.cdi;

import org.apache.log4j.Logger;
import org.ccci.postalsoft.Util_002fPostalSoft;
import org.cru.postalsoft.PostalsoftServiceWrapper;
import org.cru.util.PostalsoftServiceProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is a producer for {@link PostalsoftServiceWrapper}
 *
 * Created by William.Randall on 6/6/14.
 */
@ApplicationScoped
public class PostalsoftServiceWrapperProducer
{
    @Inject
    private PostalsoftServiceProperties postalsoftServiceProperties;

    private URL wsdlUrl;
    private QName serviceName;
    private String namespace;
    private static Logger log = Logger.getLogger(PostalsoftServiceWrapperProducer.class);

    PostalsoftServiceWrapper postalsoftServiceWrapper;

    @PostConstruct
    public void init()
    {
        initProperties();
        postalsoftServiceWrapper =
                new PostalsoftServiceWrapper(new Util_002fPostalSoft(wsdlUrl, serviceName).
                        getUtil_002fPostalSoftHttpPort(), namespace, postalsoftServiceProperties);
    }

    @Produces
    public PostalsoftServiceWrapper getPostalsoftServiceWrapper()
    {
        return postalsoftServiceWrapper;
    }

    public void setPostalsoftServiceProperties(PostalsoftServiceProperties postalsoftServiceProperties)
    {
        this.postalsoftServiceProperties = postalsoftServiceProperties;
    }

    private void initProperties()
    {
        try
        {
            wsdlUrl = new URL(postalsoftServiceProperties.getProperty("postalsoftWsdl"));
            namespace = postalsoftServiceProperties.getProperty("postalsoftNamespace");
            serviceName = new QName(namespace, postalsoftServiceProperties.getProperty("postalsoftName"));
        }
        catch(MalformedURLException me)
        {
            log.error("Problem creating URL from properties file", me);
            throw new WebApplicationException(me);
        }
    }
}
