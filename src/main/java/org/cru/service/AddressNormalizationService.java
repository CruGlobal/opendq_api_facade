package org.cru.service;

import org.cru.model.Address;
import org.cru.postalsoft.PostalsoftServiceWrapper;

import javax.inject.Inject;

/**
 * This service handles the logic of normalizing the given {@link Address}
 *
 * Created by William.Randall on 6/6/14.
 */
public class AddressNormalizationService
{
    private PostalsoftServiceWrapper postalsoftServiceWrapper;

    public AddressNormalizationService() {}

    @Inject
    public AddressNormalizationService(PostalsoftServiceWrapper postalsoftServiceWrapper)
    {
        this.postalsoftServiceWrapper = postalsoftServiceWrapper;
    }

    public boolean normalizeAddress(Address address)
    {
        return postalsoftServiceWrapper.normalizeAddress(address);
    }

    void setPostalsoftServiceWrapper(PostalsoftServiceWrapper postalsoftServiceWrapper)
    {
        this.postalsoftServiceWrapper = postalsoftServiceWrapper;
    }
}
