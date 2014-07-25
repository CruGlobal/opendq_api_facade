package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.mdm.MdmConstants;
import org.cru.mdm.PersonToMdmConverter;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.qualifiers.Add;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle complexity of adding a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
@Add
public class AddService extends IndexingService
{
    AddressNormalizationService addressNormalizationService;
    private static final String ACTION = "A";  // A = Add

    @SuppressWarnings("unused")  //used by CDI
    public AddService() {}

    @Inject
    public AddService(OpenDQProperties openDQProperties, AddressNormalizationService addressNormalizationService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
    }

    public void addPerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";

        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        RealTimeObjectActionDTO addedPerson = addPersonToMdm(person);
        addSlot(person, addedPerson);
    }

    private RealTimeObjectActionDTO addPersonToMdm(Person person)
    {
        DataManagementWSImpl mdmService = configureMdmService();
        PersonToMdmConverter personToMdmConverter = new PersonToMdmConverter(ACTION);
        RealTimeObjectActionDTO returnedObject =
            mdmService.addObject(personToMdmConverter.createRealTimeObjectFromPerson(person));

        if(MdmConstants.JUNK_ID.equals(returnedObject.getObjectEntity().getPartyId()))
        {
            throw new WebApplicationException("Failed to add person to mdm.");
        }

        return returnedObject;
    }

    void addSlot(Person person, RealTimeObjectActionDTO mdmPerson) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = callRuntimeMatchService();

        //Handle cases where no address was passed in
        if(person.getAddresses() == null || person.getAddresses().isEmpty())
        {
            addSlot(runtimeMatchWS, person, mdmPerson, null);
        }

        //If more than one address was passed in, add them all to the index
        for(Address personAddress : person.getAddresses())
        {
            addSlot(runtimeMatchWS, person, mdmPerson, personAddress);
        }
    }

    private void addSlot(RuntimeMatchWS runtimeMatchWS, Person person, RealTimeObjectActionDTO mdmPerson, Address addressToUse)
    {
        Map<String, String> fieldNamesAndValues = generateFieldNamesAndValues(person, mdmPerson, addressToUse);

        List<String> fieldNames = Lists.newArrayList();
        fieldNames.addAll(fieldNamesAndValues.keySet());

        List<String> fieldValues = Lists.newArrayList();
        fieldValues.addAll(fieldNamesAndValues.values());

        //while updateSlot sounds like an update, it is actually inserting an entry into the index
        ServiceResult addResponse = runtimeMatchWS.updateSlot(slotName, fieldNames, fieldValues);

        if(addResponse.isError())
        {
            throw new WebApplicationException(addResponse.getMessage());
        }
    }

    Map<String, String> generateFieldNamesAndValues(Person person, RealTimeObjectActionDTO mdmPerson, Address addressToUse)
    {
        Map<String, String> fieldNamesAndValues = new LinkedHashMap<String, String>();

        //NOTE: Only 10 fields can be set
        fieldNamesAndValues.put("FIELD1", person.getFirstName());
        fieldNamesAndValues.put("FIELD2", person.getLastName());

        if(addressToUse != null)
        {
            fieldNamesAndValues.put("FIELD3", addressToUse.getAddressLine1());

            if(addressToUse.getAddressLine2() == null) fieldNamesAndValues.put("FIELD4", "NULLDATA");
            else fieldNamesAndValues.put("FIELD4", addressToUse.getAddressLine2());

            fieldNamesAndValues.put("FIELD5", addressToUse.getCity());
            fieldNamesAndValues.put("FIELD6", addressToUse.getState());
            fieldNamesAndValues.put("FIELD7", addressToUse.getZipCode());
        }

        fieldNamesAndValues.put("FIELD8", person.getId());
        //FIELD10 is not currently in use
        fieldNamesAndValues.put("FIELD10", mdmPerson.getObjectEntity().getPartyId());

        return fieldNamesAndValues;
    }
}
