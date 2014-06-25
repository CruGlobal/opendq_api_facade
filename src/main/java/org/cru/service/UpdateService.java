package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Service to handle complexity of updating a {@link Person} in the index
 *
 * Created by William.Randall on 6/25/14.
 */
public class UpdateService extends AddService
{
    private static final String ACTION = "U";

    @SuppressWarnings("unused")  //Used by CDI
    public UpdateService() {}

    @Inject
    public UpdateService(OpenDQProperties openDQProperties, AddressNormalizationService addressNormalizationService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
    }

    public void updatePerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;

        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        updateMdm(person);
        RuntimeMatchWS runtimeMatchWS = callRuntimeMatchService();
        addSlot(runtimeMatchWS, person);
    }

    private void updateMdm(Person person)
    {
        //TODO: implement
    }
}
