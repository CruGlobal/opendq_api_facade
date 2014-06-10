package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SearchResponse searchResponse = callRuntimeMatchService(person);

        //TODO: determine this logic
        if(searchResponse.getScore() >= 0.95D)
        {
            matchId = searchResponse.getRowId();
        }
        return matchId;
    }

    private SearchResponse callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        return searchSlot(runtimeMatchWS, person);
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, step);

        if(configurationResponse.isError())
        {
            throw new RuntimeException(configurationResponse.getMessage());
        }
    }

    private SearchResponse searchSlot(RuntimeMatchWS runtimeMatchWS, Person person)
    {
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, createSearchValuesFromPerson(person));

        if(searchResponse.isError())
        {
            throw new RuntimeException(searchResponse.getMessage());
        }

        return buildSearchResponse(searchResponse);
    }

    private List<String> createSearchValuesFromPerson(Person person)
    {
        // Order must match the transformation file
        List<String> searchValues = new ArrayList<String>();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());
        searchValues.add(person.getAddress().getAddressLine1());
        searchValues.add(person.getAddress().getCity());

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

    SearchResponse buildSearchResponse(ServiceResult searchResult)
    {
        SearchResponse searchResponse = new SearchResponse();
        List<Object> searchResultValues = searchResult.getValues();

        searchResponse.setScore(searchResult.getScore());
        searchResponse.setRowId((String)searchResultValues.get(4));
        searchResponse.setResultValues(buildResultValues(searchResultValues));

        return searchResponse;
    }

    Map<String, Object> buildResultValues(List<Object> searchResultValues)
    {
        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put("firstName", searchResultValues.get(0));
        valueMap.put("lastName", searchResultValues.get(1));
        valueMap.put("address1", searchResultValues.get(2));
        valueMap.put("city", searchResultValues.get(3));

        return valueMap;
    }
}
