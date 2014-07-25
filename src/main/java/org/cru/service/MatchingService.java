package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Address;
import org.cru.model.OafResponse;
import net.java.dev.jaxb.array.AnyTypeArray;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.qualifiers.Delete;
import org.cru.qualifiers.Match;
import org.cru.util.Action;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle the complexity of matching {@link Person} fields
 *
 * Created by William.Randall on 6/9/14.
 */
@Match
public class MatchingService extends IndexingService
{
    private DeleteService deleteService;

    @SuppressWarnings("unused")  //used by CDI
    public MatchingService() {}

    @Inject
    public MatchingService(OpenDQProperties openDQProperties, @Delete DeleteService deleteService)
    {
        this.openDQProperties = openDQProperties;
        this.deleteService = deleteService;
    }

    public List<OafResponse> findMatch(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";
        List<SearchResponse> searchResponseList = Lists.newArrayList();

        if(person.getAddresses() == null || person.getAddresses().isEmpty())
        {
            searchResponseList.addAll(searchSlot(createSearchValuesFromPerson(person, null)));
        }
        for(Address personAddress : person.getAddresses())
        {
            searchResponseList.addAll(searchSlot(createSearchValuesFromPerson(person, personAddress)));
        }

        if(searchResponseList == null || searchResponseList.isEmpty()) return null;

        return buildOafResponseList(buildFilteredSearchResponseList(searchResponseList));
    }

    public SearchResponse searchForPerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";
        List<SearchResponse> searchResponseList = Lists.newArrayList();

        //Handle cases where no address was passed in
        if(person.getAddresses() == null || person.getAddresses().isEmpty())
        {
            searchResponseList.addAll(searchSlot(createSearchValuesFromPerson(person, null)));
        }

        //If given more than one address, we need to make sure we search on all of them
        for(Address personAddress : person.getAddresses())
        {
            searchResponseList.addAll(searchSlot(createSearchValuesFromPerson(person, personAddress)));
        }

        if(searchResponseList == null || searchResponseList.isEmpty()) return null;

        return findSinglePersonFromList(buildFilteredSearchResponseList(searchResponseList), person.getId());
    }

    List<SearchResponse> buildFilteredSearchResponseList(List<SearchResponse> searchResponseList)
    {
        List<SearchResponse> filteredResults = Lists.newArrayList();

        for(SearchResponse response : searchResponseList)
        {
            String partyId = (String)response.getResultValues().get("partyId");
            if(!matchHasBeenDeleted(partyId)) filteredResults.add(response);
        }
        return filteredResults;
    }

    List<OafResponse> buildOafResponseList(List<SearchResponse> filteredSearchResponseList)
    {
        List<OafResponse> oafResponseList = Lists.newArrayList();

        for(SearchResponse searchResponse : filteredSearchResponseList)
        {
            OafResponse matchResponse = new OafResponse();
            matchResponse.setConfidenceLevel(searchResponse.getScore());
            matchResponse.setMatchId(searchResponse.getId());
            matchResponse.setAction(Action.MATCH);

            oafResponseList.add(matchResponse);
        }

        return oafResponseList;
    }

    SearchResponse findSinglePersonFromList(List<SearchResponse> filteredSearchResponseList, String globalRegistryId)
    {
        for(SearchResponse searchResponse : filteredSearchResponseList)
        {
            if(searchResponse.getId().equalsIgnoreCase(globalRegistryId)) return searchResponse;
        }

        return null;
    }

    public RealTimeObjectActionDTO findMatchInMdm(String partyId)
    {
        DataManagementWSImpl mdmService = configureMdmService();
        return mdmService.findObject(partyId);
    }

    private List<SearchResponse> searchSlot(List<String> searchValues) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = callRuntimeMatchService();
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, searchValues);

        if(searchResponse.isError())
        {
            throw new WebApplicationException(searchResponse.getMessage());
        }

        return buildSearchResponses(searchResponse);
    }

    private List<String> createSearchValuesFromPerson(Person person, Address addressToSearchOn)
    {
        // Order must match the transformation file
        List<String> searchValues = new ArrayList<String>();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());

        if(addressToSearchOn != null)
        {
            searchValues.add(addressToSearchOn.getAddressLine1());

            if(addressToSearchOn.getAddressLine2() == null) searchValues.add("NULLDATA");
            else searchValues.add(addressToSearchOn.getAddressLine2());

            searchValues.add(addressToSearchOn.getCity());
            searchValues.add(addressToSearchOn.getState());
        }

        return searchValues;
    }

    private boolean matchHasBeenDeleted(String matchId)
    {
        return deleteService.personIsDeleted(matchId);
    }

    SearchResponse buildSearchResponse(Float score, Map<String, Object> values)
    {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setScore(score);
        searchResponse.setResultValues(values);
        searchResponse.setId((String)values.get("standardizedFirstName"));

        return searchResponse;
    }

    List<Map<String, Object>> buildListOfValueMaps(List<AnyTypeArray> searchResultValues)
    {
        List<Map<String, Object>> valueMapList = new ArrayList<Map<String, Object>>();

        for(AnyTypeArray valueSet : searchResultValues)
        {
            valueMapList.add(buildResultValues(valueSet.getItem()));
        }
        return valueMapList;
    }

    Map<String, Object> buildResultValues(List<Object> searchResultValues)
    {
        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put("firstName", searchResultValues.get(0));
        valueMap.put("lastName", searchResultValues.get(1));
        valueMap.put("address1", searchResultValues.get(2));
        valueMap.put("address2", searchResultValues.get(3));
        valueMap.put("city", searchResultValues.get(4));
        valueMap.put("state", searchResultValues.get(5));
        valueMap.put("zip", searchResultValues.get(6));
        valueMap.put("standardizedFirstName", searchResultValues.get(7));
        // Value 8 is not mapped to anything yet
        valueMap.put("partyId", searchResultValues.get(9));

        return valueMap;
    }

    List<SearchResponse> buildSearchResponses(ServiceResult searchResult)
    {
        List<SearchResponse> searchResponseList = Lists.newArrayList();
        List<AnyTypeArray> searchResultValues = searchResult.getRows();

        if(searchResultValues == null || searchResultValues.isEmpty())
        {
            return null;
        }

        List<Map<String, Object>> valueMapList = buildListOfValueMaps(searchResultValues);
        List<Float> scoreList = searchResult.getScores();

        for(int i = 0; i < scoreList.size(); i++)
        {
            searchResponseList.add(buildSearchResponse(scoreList.get(i), valueMapList.get(i)));
        }

        return searchResponseList;
    }
}
