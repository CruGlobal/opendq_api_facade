package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

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
        ServiceResult addResponse = runtimeMatchWS.updateSlot(slotName, generateFieldNames(), generateFieldValues(person));

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

    List<String> generateFieldNames()
    {
        List<String> fieldNames = new ArrayList<String>();

        fieldNames.add("FIELD1");
        fieldNames.add("FIELD2");
        fieldNames.add("FIELD3");
        fieldNames.add("FIELD4");
        fieldNames.add("FIELD5");

        return fieldNames;
    }

    //TODO: I don't like how tightly coupled these two methods (^ v) are...find a different way

    List<String> generateFieldValues(Person person)
    {
        List<String> fieldValues = new ArrayList<String>();

        fieldValues.add(person.getFirstName());
        fieldValues.add(person.getLastName());
        fieldValues.add(person.getAddress().getAddressLine1());
        fieldValues.add(person.getAddress().getCity());
        fieldValues.add(person.getRowId());

        return fieldValues;
    }

    void setOpenDQProperties(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }
}
