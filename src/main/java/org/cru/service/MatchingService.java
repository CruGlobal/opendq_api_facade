package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.util.OpenDQProperties;
import org.cru.util.ResponseMessage;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
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
    private OpenDQProperties openDQProperties;
    private DeleteService deleteService;
    private String slotName;
    private String transformationFileLocation;

    public MatchingService() {}

    @Inject
    public MatchingService(OpenDQProperties openDQProperties, DeleteService deleteService)
    {
        this.openDQProperties = openDQProperties;
        this.deleteService = deleteService;
    }


    public MatchResponse findMatch(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        SearchResponse searchResponse = callRuntimeMatchService(person);

        if(searchResponse == null) return null;

        String matchId = searchResponse.getId();

        if(matchHasBeenDeleted(matchId)) return null;

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setConfidenceLevel(searchResponse.getScore());
        matchResponse.setMatchId(matchId);
        matchResponse.setMessage(ResponseMessage.FOUND.getMessage());
        return matchResponse;
    }

    private SearchResponse callRuntimeMatchService(Person person) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        return searchSlot(runtimeMatchWS, person);
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, "RtMatch");

        if(configurationResponse.isError())
        {
            throw new WebApplicationException(configurationResponse.getMessage());
        }
    }

    private SearchResponse searchSlot(RuntimeMatchWS runtimeMatchWS, Person person)
    {
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, createSearchValuesFromPerson(person));

        if(searchResponse.isError())
        {
            throw new WebApplicationException(searchResponse.getMessage());
        }

        return buildSearchResponse(searchResponse);
    }

    //TODO: Handle multiple addresses, emails, phones
    private List<String> createSearchValuesFromPerson(Person person)
    {
        // Order must match the transformation file
        List<String> searchValues = new ArrayList<String>();

        searchValues.add(person.getName().getFirstName());
        searchValues.add(person.getName().getLastName());
        searchValues.add(person.getAddresses().get(0).getAddressLine1());
        searchValues.add(person.getAddresses().get(0).getCity());

        return searchValues;
    }

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    private boolean matchHasBeenDeleted(String matchId)
    {
        return deleteService.personIsDeleted(matchId);
    }

    SearchResponse buildSearchResponse(ServiceResult searchResult)
    {
        SearchResponse searchResponse = new SearchResponse();
        List<Object> searchResultValues = searchResult.getValues();

        if(searchResultValues == null || searchResultValues.isEmpty())
        {
            return null;
        }

        searchResponse.setScore(searchResult.getScore());
        searchResponse.setId((String) searchResultValues.get(4));
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
