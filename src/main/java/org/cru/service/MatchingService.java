package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolve.openmdm.webservices.provider.impl.UniqueIdNameDTO;
import com.infosolve.openmdm.webservices.provider.impl.UniqueIdNameDTOList;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import net.java.dev.jaxb.array.AnyTypeArray;
import org.apache.log4j.Logger;
import org.cru.model.Address;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.map.ResultData;
import org.cru.model.SearchResponse;
import org.cru.model.collections.SearchResponseList;
import org.cru.qualifiers.Delete;
import org.cru.qualifiers.Match;
import org.cru.util.Action;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle the complexity of matching {@link Person} fields
 *
 * Created by William.Randall on 6/9/14.
 */
@Match
public class MatchingService extends IndexingService
{
    private DeleteService deleteService;
    private static Logger log = Logger.getLogger(MatchingService.class);

    @SuppressWarnings("unused")  //used by CDI
    public MatchingService() {}

    @Inject
    public MatchingService(OpenDQProperties openDQProperties, @Delete DeleteService deleteService)
    {
        this.openDQProperties = openDQProperties;
        this.deleteService = deleteService;
    }

    /**
     * Returns a {@link List<OafResponse>} of all the non-duplicate people found
     * in the index with different party IDs (using the given {@link Person} for matching)
     */
    public List<OafResponse> findMatches(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";

        SearchResponseList searchResponseList = findPersonInIndex(person);
        if(searchResponseList == null || searchResponseList.isEmpty()) return null;

        return buildOafResponseList(filterDuplicatePartyIds(buildFilteredSearchResponseList(searchResponseList)));
    }

    SearchResponseList buildFilteredSearchResponseList(SearchResponseList searchResponseList)
    {
        SearchResponseList filteredResults = new SearchResponseList();

        for(SearchResponse response : searchResponseList)
        {
            String partyId = response.getResultValues().getPartyId();
            if(!matchHasBeenDeleted(getGlobalRegistryIdFromMdm(partyId))) filteredResults.add(response);
        }

        filteredResults.removeDuplicateResults();
        filteredResults.sortListByScore();
        filteredResults.filterLowConfidenceMatches();

        return filteredResults;
    }

    SearchResponseList filterDuplicatePartyIds(SearchResponseList searchResponseList)
    {
        SearchResponseList filteredSearchResponseList = new SearchResponseList();
        List<String> usedPartyIds = Lists.newArrayList();

        for(SearchResponse response : searchResponseList)
        {
            if(!usedPartyIds.contains(response.getResultValues().getPartyId()))
            {
                usedPartyIds.add(response.getResultValues().getPartyId());
                filteredSearchResponseList.add(response);
            }
        }

        return filteredSearchResponseList;
    }

    public List<OafResponse> buildOafResponseList(SearchResponseList filteredSearchResponseList)
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

    public RealTimeObjectActionDTO findMatchInMdm(String partyId)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation();
        try
        {
            return mdmService.findObject(partyId);
        }
        catch(Throwable t)
        {
            log.error("Failed to find match in MDM", t);
            throw new WebApplicationException("Failed to find match in MDM: " + t.getMessage());
        }
    }

    /**
     * Search MDM database for a person using global registry id.
     *
     * @return null if no person is found, otherwise a {@link RealTimeObjectActionDTO} person
     * @throws SOAPFaultException if more than one result is found
     */
    public RealTimeObjectActionDTO findMatchInMdmByGlobalRegistryId(String globalRegistryId)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation();
        UniqueIdNameDTOList uniqueNameList = new UniqueIdNameDTOList();
        List<UniqueIdNameDTO> uniqueIdNameDTOs = uniqueNameList.getUniqueIdNames();

        UniqueIdNameDTO uniqueIdName = new UniqueIdNameDTO();
        uniqueIdName.setUniqueId("GlobalRegistryId");
        uniqueIdName.setUniqueIdName(globalRegistryId);
        uniqueIdNameDTOs.add(uniqueIdName);

        try
        {
            return mdmService.findObjectMulti(uniqueNameList);
        }
        catch(Throwable t)
        {
            if(t.getMessage().contains("No Data found with these input set."))
            {
                log.info("No data found for GR ID: " + globalRegistryId);
                return null;
            }
            if(t.getMessage().contains("query did not return a unique result"))
            {
                log.error("More than one instance of GR ID: " + globalRegistryId + " in MDM");
                throw new WebApplicationException("More than one result returned for global registry id: " + globalRegistryId);
            }
            log.error("Error searching MDM with GR ID: " + globalRegistryId, t);
            throw new WebApplicationException(t.getMessage());
        }
    }

    SearchResponseList findPersonInIndex(Person person) throws ConnectException
    {
        SearchResponseList searchResponseList = new SearchResponseList();

        //Handle cases where no address was passed in
        if(person.getAddresses() == null || person.getAddresses().isEmpty())
        {
            SearchResponseList responses = queryIndex(createSearchValuesFromPerson(person, null));
            if(responses != null) searchResponseList.addAll(responses);
        }

        //If given more than one address, we need to make sure we search on all of them
        for(Address personAddress : person.getAddresses())
        {
            SearchResponseList responses = queryIndex(createSearchValuesFromPerson(person, personAddress));
            if(responses!= null) searchResponseList.addAll(responses);
        }

        return searchResponseList;
    }

    private SearchResponseList queryIndex(List<String> searchValues) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService();
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, searchValues);

        if(searchResponse.isError())
        {
            log.error("Error searching index: " + searchResponse.getMessage());
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

    SearchResponse buildSearchResponse(Float score, ResultData values, String type)
    {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setScore(score);
        searchResponse.setResultValues(values);
        searchResponse.setId(getGlobalRegistryIdFromMdm(values.getPartyId()));
        searchResponse.setType(type);

        return searchResponse;
    }

    String getGlobalRegistryIdFromMdm(String partyId)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation();

        RealTimeObjectActionDTO foundPerson;
        try
        {
            foundPerson = mdmService.findObject(partyId);
        }
        catch(Throwable t)
        {
            log.debug("Could not find a person with party id: " + partyId + "in MDM", t);
            throw new WebApplicationException("Failed to find global registry id using party id: " + partyId);
        }

        if(foundPerson == null) return null;

        for(ObjAttributeDataDTO attributeData : foundPerson.getObjectAttributeDatas().getObjectAttributeData())
        {
            if(attributeData.getMultDetTypeLev1().equalsIgnoreCase("PERSONATTRIBUTES") &&
                attributeData.getMultDetTypeLev2().equalsIgnoreCase("GLOBALREGISTRYID"))
            {
                return attributeData.getField2();
            }
        }
        return null;
    }

    List<ResultData> buildListOfValueMaps(List<AnyTypeArray> searchResultValues)
    {
        List<ResultData> valueMapList = Lists.newArrayList();

        for(AnyTypeArray valueSet : searchResultValues)
        {
            valueMapList.add(buildResultValues(valueSet.getItem()));
        }
        return valueMapList;
    }

    ResultData buildResultValues(List<Object> searchResultValues)
    {
        ResultData valueMap = new ResultData();

        valueMap.putFirstName(searchResultValues.get(0));
        valueMap.putLastName(searchResultValues.get(1));
        valueMap.putAddressLine1(searchResultValues.get(2));
        valueMap.putAddressLine2(searchResultValues.get(3));
        valueMap.putCity(searchResultValues.get(4));
        valueMap.putState(searchResultValues.get(5));
        valueMap.putZip(searchResultValues.get(6));
        valueMap.putStandardizedFirstName(searchResultValues.get(7));
        // Value 8 is not mapped to anything yet
        valueMap.putPartyId(searchResultValues.get(9));

        return valueMap;
    }

    SearchResponseList buildSearchResponses(ServiceResult searchResult)
    {
        SearchResponseList searchResponseList = new SearchResponseList();
        List<AnyTypeArray> searchResultValues = searchResult.getRows();

        if(searchResultValues == null || searchResultValues.isEmpty())
        {
            return null;
        }

        List<ResultData> valueMapList = buildListOfValueMaps(searchResultValues);
        List<Float> scoreList = searchResult.getScores();

        for(int i = 0; i < scoreList.size(); i++)
        {
            searchResponseList.add(buildSearchResponse(scoreList.get(i), valueMapList.get(i), searchResult.getType()));
        }

        return searchResponseList;
    }
}
