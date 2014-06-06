package org.cru.service;

import org.cru.model.Address;
import org.cru.postalsoft.PostalsoftServiceWrapper;

import javax.inject.Inject;

/**
 * Created by William.Randall on 6/6/14.
 */
public class AddressNormalizationService
{
    @Inject
    private PostalsoftServiceWrapper postalsoftServiceWrapper;

    public boolean normalizeAddress(Address address)
    {
        return postalsoftServiceWrapper.normalizeAddress(address);
    }

    public void setPostalsoftServiceWrapper(PostalsoftServiceWrapper postalsoftServiceWrapper)
    {
        this.postalsoftServiceWrapper = postalsoftServiceWrapper;
    }
}
