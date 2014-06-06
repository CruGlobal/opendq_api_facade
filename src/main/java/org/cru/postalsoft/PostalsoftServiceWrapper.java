package org.cru.postalsoft;

import com.google.common.base.Throwables;
import org.ccci.postalsoft.ObjectFactory;
import org.ccci.postalsoft.PostalAddress;
import org.ccci.postalsoft.PostalsoftService;
import org.ccci.webservices.services.postalsoft.CorrectionResult;
import org.cru.model.Address;
import org.cru.util.PostalsoftServiceProperties;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Created by William.Randall on 6/6/14.
 */
public class PostalsoftServiceWrapper
{
    final PostalsoftService postalsoftService;
    final String namespaceURI;

    @Inject
    private PostalsoftServiceProperties postalsoftServiceProperties;

    public PostalsoftServiceWrapper(PostalsoftService postalsoftService, String namespaceURI)
    {
        this.postalsoftService = postalsoftService;
        this.namespaceURI = namespaceURI;
    }

    public boolean normalizeAddress(Address address)
    {
        try
        {
            PostalAddress pa = new ObjectFactory().createPostalAddress();
            pa.setAddressLine1(createXMLValue("addressLine1", address.getAddressLine1()));
            pa.setAddressLine2(createXMLValue("addressLine2", address.getAddressLine2()));
            pa.setAddressLine3(createXMLValue("addressLine3", address.getAddressLine3()));
            pa.setAddressLine4(createXMLValue("addressLine4", address.getAddressLine4()));
            pa.setCity(createXMLValue("city", address.getCity()));
            pa.setState(createXMLValue("state", address.getState()));
            pa.setZip(createXMLValue("zip", address.getZipCode()));

            String postalsoftUsername = postalsoftServiceProperties.getProperty("postalsoftLoginName");
            String postalsoftPassword = postalsoftServiceProperties.getProperty("postalsoftLoginPassword");
            CorrectionResult correctionResult = postalsoftService.correctAddress(postalsoftUsername, postalsoftPassword, pa);

			/* if we successfully found a match and cleansed the address, update the mailing address in 
			 * the cart with the result back from PostalSoft
			 */
            if("SUCCESS".equals(correctionResult.getResultStatus().getValue()))
            {
                PostalAddress correctedAddressValue = correctionResult.getAddress().getValue();

                address.setAddressLine1(correctedAddressValue.getAddressLine1().getValue());
                address.setAddressLine2(correctedAddressValue.getAddressLine2().getValue());
                address.setAddressLine3(correctedAddressValue.getAddressLine3().getValue());
                address.setAddressLine4(correctedAddressValue.getAddressLine4().getValue());
                address.setCity(correctedAddressValue.getCity().getValue());
                address.setState(correctedAddressValue.getState().getValue());
                address.setZipCode(correctedAddressValue.getZip().getValue());

                address.setNormalized(true);

                return true;
            }
            else
            {
                address.setNormalized(false);
            }
        }
        catch (Throwable e)
        {
            address.setNormalized(false);
            Throwables.propagate(e);
        }

        return false;
    }

    JAXBElement<String> createXMLValue(String fieldName, String value)
    {
        QName qName = new QName(namespaceURI, fieldName);
        return new JAXBElement<String>(qName, String.class, value);
    }

    public void setPostalsoftServiceProperties(PostalsoftServiceProperties postalsoftServiceProperties)
    {
        this.postalsoftServiceProperties = postalsoftServiceProperties;
    }
}
