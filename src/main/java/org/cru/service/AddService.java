package org.cru.service;

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
        this.stepName = "RtIndex";

        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        RealTimeObjectActionDTO addedPerson = addPersonToMdm(person);
        RuntimeMatchWS runtimeMatchWS = callRuntimeMatchService();
        addSlot(runtimeMatchWS, person, addedPerson);
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

    void addSlot(RuntimeMatchWS runtimeMatchWS, Person person, RealTimeObjectActionDTO mdmPerson)
    {
        //while updateSlot sounds like an update, it is actually inserting an entry into the index
        Map<String, String> fieldNamesAndValues = generateFieldNamesAndValues(person, mdmPerson);

        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(fieldNamesAndValues.keySet());

        List<String> fieldValues = new ArrayList<String>();
        fieldValues.addAll(fieldNamesAndValues.values());

        ServiceResult addResponse = runtimeMatchWS.updateSlot(slotName, fieldNames, fieldValues);

        if(addResponse.isError())
        {
            throw new WebApplicationException(addResponse.getMessage());
        }
    }

    //TODO: Handle multiple addresses, emails, phones
    Map<String, String> generateFieldNamesAndValues(Person person, RealTimeObjectActionDTO mdmPerson)
    {
        Map<String, String> fieldNamesAndValues = new LinkedHashMap<String, String>();

        //NOTE: Only 10 fields can be set
        fieldNamesAndValues.put("FIELD1", person.getName().getFirstName());
        fieldNamesAndValues.put("FIELD2", person.getName().getLastName());
        fieldNamesAndValues.put("FIELD3", person.getAddresses().get(0).getAddressLine1());
        fieldNamesAndValues.put("FIELD4", person.getAddresses().get(0).getCity());
        fieldNamesAndValues.put("FIELD5", person.getGlobalRegistryId());
        fieldNamesAndValues.put("FIELD6", mdmPerson.getObjectEntity().getPartyId());

        return fieldNamesAndValues;
    }
}
