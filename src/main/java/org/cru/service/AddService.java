package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle complexity of adding a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
public class AddService
{
    @Inject
    private OpenDQProperties openDQProperties;
    @Inject
    private AddressNormalizationService addressNormalizationService;

    private String slotName;
    private String transformationFileLocation;
    private String step;

    public void addPerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        addressNormalizationService.normalizeAddress(person.getAddress());
        callRuntimeMatchService(person);
    }

    private void callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        addSlot(runtimeMatchWS, person);
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, step);

        if(configurationResponse.isError())
        {
            throw new RuntimeException(configurationResponse.getMessage());
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
            throw new RuntimeException(addResponse.getMessage());
        }
    }

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");
        step = "RtIndex";

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    Map<String, String> generateFieldNamesAndValues(Person person)
    {
        Map<String, String> fieldNamesAndValues = new HashMap<String, String>();

        fieldNamesAndValues.put("FIELD1", person.getFirstName());
        fieldNamesAndValues.put("FIELD2", person.getLastName());
        fieldNamesAndValues.put("FIELD3", person.getAddress().getAddressLine1());
        fieldNamesAndValues.put("FIELD4", person.getAddress().getCity());
        fieldNamesAndValues.put("FIELD5", person.getRowId());

        return fieldNamesAndValues;
    }

    void setOpenDQProperties(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }

    void setAddressNormalizationService(AddressNormalizationService addressNormalizationService)
    {
        this.addressNormalizationService = addressNormalizationService;
    }
}
