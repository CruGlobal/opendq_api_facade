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
 * Service to handle the complexity of matching person fields
 *
 * Created by William.Randall on 6/9/14.
 */
public class MatchingService
{
    @Inject
    private OpenDQProperties openDQProperties;

    private String matchId;     // Global Registry ID that matches data given


    public String findMatch(Person person) throws ConnectException
    {
        callRuntimeMatchService(person);
        //TODO: only set the match Id if the confidence level > ?? else null
        matchId = "TEST_ID";
        return matchId;
    }

    private void callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        RuntimeMatchWS runtimeMatchWS = runtimeMatchWSService.getRuntimeMatchWSPort();

        String slotName = "test1";
        String transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");
        String step = "RtMatch";

        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, step);

        if(configurationResponse.isError())
        {
            throw new RuntimeException(configurationResponse.getMessage());
        }

        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, createSearchValuesFromPerson(person));

        if(searchResponse.isError())
        {
            //throw exception with searchResponse.getMessage()
        }

        List<Object> searchResults = searchResponse.getValues();
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

    public String getMatchId()
    {
        return matchId;
    }

    void setOpenDQProperties(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }
}
