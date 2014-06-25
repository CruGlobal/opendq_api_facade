package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImplService;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.mdm.MdmConstants;
import org.cru.mdm.PersonToMdmConverter;
import org.cru.model.Address;
import org.cru.model.Person;
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
public class AddService
{
    private OpenDQProperties openDQProperties;
    private AddressNormalizationService addressNormalizationService;

    private String slotName;
    private String transformationFileLocation;

    private static final String ACTION = "A";  // A = Add

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

        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        addPersonToMdm(person);
        callRuntimeMatchService(person);
    }

    private void callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        addSlot(runtimeMatchWS, person);
    }

    private void addPersonToMdm(Person person)
    {
        DataManagementWSImpl mdmService = configureMdmService();
        PersonToMdmConverter personToMdmConverter = new PersonToMdmConverter(ACTION);
        RealTimeObjectActionDTO returnedObject =
            mdmService.addObject(personToMdmConverter.createRealTimeObjectFromPerson(person));

        if(MdmConstants.JUNK_ID.equals(returnedObject.getObjectEntity().getPartyId()))
        {
            throw new WebApplicationException("Failed to add person to mdm.");
        }
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, "RtIndex");

        if(configurationResponse.isError())
        {
            throw new WebApplicationException(configurationResponse.getMessage());
        }
    }

    private void addSlot(RuntimeMatchWS runtimeMatchWS, Person person)
    {
        //while updateSlot sounds like an update, it is actually inserting an entry into the index
        Map<String, String> fieldNamesAndValues = generateFieldNamesAndValues(person);

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

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    private DataManagementWSImpl configureMdmService()
    {
        DataManagementWSImplService mdmService = new DataManagementWSImplService();
        return mdmService.getDataManagementWSImplPort();
    }

    //TODO: Handle multiple addresses, emails, phones
    Map<String, String> generateFieldNamesAndValues(Person person)
    {
        Map<String, String> fieldNamesAndValues = new LinkedHashMap<String, String>();

        fieldNamesAndValues.put("FIELD1", person.getName().getFirstName());
        fieldNamesAndValues.put("FIELD2", person.getName().getLastName());
        fieldNamesAndValues.put("FIELD3", person.getAddresses().get(0).getAddressLine1());
        fieldNamesAndValues.put("FIELD4", person.getAddresses().get(0).getCity());
        fieldNamesAndValues.put("FIELD5", person.getGlobalRegistryId());

        return fieldNamesAndValues;
    }
}
