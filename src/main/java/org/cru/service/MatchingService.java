package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle the complexity of matching person fields
 *
 * Created by William.Randall on 6/9/14.
 */
public class MatchingService
{
    @Inject
    private OpenDQProperties openDQProperties;

    private String matchId;     // Global Registry ID that matches data given
    private String slotName;
    private String transformationFileLocation;
    private String step;


    public String findMatch(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        callRuntimeMatchService(person);
        //TODO: only set the match Id if the confidence level > ?? else null
        matchId = "TEST_ID";
        return matchId;
    }

    private void callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        ServiceResult searchResponse = searchSlot(runtimeMatchWS, person);

        List<Object> searchResults = searchResponse.getValues();
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, step);

        if(configurationResponse.isError())
        {
            throw new RuntimeException(configurationResponse.getMessage());
        }
    }

    private ServiceResult searchSlot(RuntimeMatchWS runtimeMatchWS, Person person)
    {
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, createSearchValuesFromPerson(person));

        if(searchResponse.isError())
        {
            throw new RuntimeException(searchResponse.getMessage());
        }

        return searchResponse;
    }

    private List<String> createSearchValuesFromPerson(Person person)
    {
        // Order must match the transformation file
        List<String> searchValues = new ArrayList<String>();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());
        searchValues.add(person.getAddress().getAddressLine1());
        searchValues.add(person.getAddress().getCity());
        searchValues.add(person.getRowId());

        return searchValues;
    }

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");
        step = "RtMatch";

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    public String getMatchId()
    {
        return matchId;
    }

    void setOpenDQProperties(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }
}
