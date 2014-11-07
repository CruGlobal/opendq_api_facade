package org.cru.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolve.openmdm.webservices.provider.impl.UniqueIdNameDTO;
import com.infosolve.openmdm.webservices.provider.impl.UniqueIdNameDTOList;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import net.java.dev.jaxb.array.AnyTypeArray;
import org.apache.log4j.Logger;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.model.SearchResponse;
import org.cru.model.collections.SearchResponseList;
import org.cru.model.map.IndexData;
import org.cru.model.map.NameAndAddressIndexData;
import org.cru.model.map.NameAndCommunicationIndexData;
import org.cru.qualifiers.Delete;
import org.cru.qualifiers.Match;
import org.cru.qualifiers.Nickname;
import org.cru.util.Action;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
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
    private NicknameService nicknameService;
    private AddressNormalizationService addressNormalizationService;
    private static Logger log = Logger.getLogger(MatchingService.class);

    @SuppressWarnings("unused")  //used by CDI
    public MatchingService() {}

    @Inject
    public MatchingService(
        OpenDQProperties openDQProperties,
        @Delete DeleteService deleteService,
        @Nickname NicknameService nicknameService,
        AddressNormalizationService addressNormalizationService)
    {
        this.openDQProperties = openDQProperties;
        this.deleteService = deleteService;
        this.nicknameService = nicknameService;
        this.addressNormalizationService = addressNormalizationService;
    }

    /**
     * Returns a {@link List<OafResponse>} of all the non-duplicate people found
     * in the index with different party IDs (using the given {@link Person} for matching)
     */
    public List<OafResponse> findMatches(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;

        standardizeFirstName(person);
        SearchResponseList searchResponseList = findPersonInIndex(person);
        if(searchResponseList == null || searchResponseList.isEmpty()) return null;

        //If we only have 1 result, there is no need to go through the filtering process
        if(searchResponseList.size() == 1) return buildOafResponseList(searchResponseList);

        return buildOafResponseList(filterDuplicatePartyIds(buildFilteredSearchResponseList(searchResponseList)));
    }

    void standardizeFirstName(Person person) throws ConnectException
    {
        person.setFirstName(nicknameService.getStandardizedNickName(person.getFirstName()));
    }

    SearchResponseList buildFilteredSearchResponseList(SearchResponseList searchResponseList)
    {
        SearchResponseList filteredResults = new SearchResponseList();

        for(SearchResponse response : searchResponseList)
        {
            if(!matchHasBeenDeleted(response.getId())) filteredResults.add(response);
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

    List<OafResponse> buildOafResponseList(SearchResponseList filteredSearchResponseList)
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
        DataManagementWSImpl mdmService = getMdmServiceImplementation("contact");
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
     * @throws WebApplicationException if more than one result is found
     */
    public RealTimeObjectActionDTO findMatchInMdmByGlobalRegistryId(String globalRegistryId)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation("contact");
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
        SearchResponseList searchResponseList;

        if(person.getAddresses() != null && !person.getAddresses().isEmpty())
        {
            searchResponseList = findPersonInIndexUsingAddress(person);
            if(searchResponseList.hasAStrongMatch()) return searchResponseList;
        }
        if(person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty())
        {
            searchResponseList = findPersonInIndexUsingEmail(person);
            if(searchResponseList.hasAStrongMatch()) return searchResponseList;
        }
        if(person.getPhoneNumbers() != null && !person.getPhoneNumbers().isEmpty())
        {
            searchResponseList = findPersonInIndexUsingPhoneNumber(person);
            if(searchResponseList.hasAStrongMatch()) return searchResponseList;
        }

        return null;
    }

    private SearchResponseList findPersonInIndexUsingAddress(Person person) throws ConnectException
    {
        SearchResponseList searchResponseList = new SearchResponseList();
        this.stepName = "RtMatchAddr";

        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService("contact");

        //If given more than one address, we need to make sure we search on all of them
        for(Address personAddress : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(personAddress);

            SearchResponseList responses = queryIndexByNameAndAddress(
                createNameAndAddressSearchValuesFromPerson(person, personAddress), runtimeMatchWS);

            if(responses != null)
            {
                if(hasAPerfectMatch(responses))
                {
                    return responses;
                }
                else
                {
                    searchResponseList.addAll(responses);
                }
            }
        }

        return searchResponseList;
    }

    SearchResponseList findPersonInIndexUsingEmail(Person person) throws ConnectException
    {
        this.stepName = "RtMatchComm";
        this.slotName = "contactCommMatch";

        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService("communication");

        SearchResponseList searchResponseList = new SearchResponseList();

        for(EmailAddress emailAddress : person.getEmailAddresses())
        {
            ServiceResult searchResponse =
                runtimeMatchWS.searchSlot(slotName, createNameAndEmailSearchValuesFromPerson(person, emailAddress));

            if(searchResponse.isError())
            {
                log.error("Error searching index: " + searchResponse.getMessage());
                throw new WebApplicationException(searchResponse.getMessage());
            }

            SearchResponseList responses = buildSearchResponses(searchResponse, IndexType.COMMUNICATION);
            if(responses != null)
            {
                if(hasAPerfectMatch(responses))
                {
                    return responses;
                }
                else
                {
                    searchResponseList.addAll(responses);
                }
            }
        }

        return searchResponseList;
    }

    SearchResponseList findPersonInIndexUsingPhoneNumber(Person person) throws ConnectException
    {
        this.stepName = "RtMatchComm";
        this.slotName = "contactCommMatch";

        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService("communication");

        SearchResponseList searchResponseList = new SearchResponseList();

        for(PhoneNumber phoneNumber : person.getPhoneNumbers())
        {
            ServiceResult searchResponse =
                runtimeMatchWS.searchSlot(slotName, createNameAndPhoneNumberSearchValuesFromPerson(person, phoneNumber));

            if(searchResponse.isError())
            {
                log.error("Error searching index: " + searchResponse.getMessage());
                throw new WebApplicationException(searchResponse.getMessage());
            }

            SearchResponseList responses = buildSearchResponses(searchResponse, IndexType.COMMUNICATION);
            if(responses != null)
            {
                if(hasAPerfectMatch(responses))
                {
                    return responses;
                }
                else
                {
                    searchResponseList.addAll(responses);
                }
            }
        }

        return searchResponseList;
    }

    private SearchResponseList queryIndexByNameAndAddress(List<String> searchValues, RuntimeMatchWS runtimeMatchWS) throws ConnectException
    {
        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, searchValues);

        if(searchResponse.isError())
        {
            log.error("Error searching index: " + searchResponse.getMessage());
            throw new WebApplicationException(searchResponse.getMessage());
        }

        return buildSearchResponses(searchResponse, IndexType.ADDRESS);
    }

    private List<String> createNameAndAddressSearchValuesFromPerson(Person person, Address addressToSearchOn) throws ConnectException
    {
        // Order must match the transformation file
        List<String> searchValues = new ArrayList<String>();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());

        if(addressToSearchOn != null)
        {
            searchValues.add(addressToSearchOn.getAddressLine1());

            if(Strings.isNullOrEmpty(addressToSearchOn.getAddressLine2())) searchValues.add("NULLDATA");
            else searchValues.add(addressToSearchOn.getAddressLine2());

            searchValues.add(addressToSearchOn.getCity());
            searchValues.add(addressToSearchOn.getState());
        }

        return searchValues;
    }

    private List<String> createNameAndEmailSearchValuesFromPerson(Person person, EmailAddress emailAddressToSearchOn) throws ConnectException
    {
        List<String> searchValues = Lists.newArrayList();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());
        searchValues.add(emailAddressToSearchOn.getEmail());

        return searchValues;
    }

    private List<String> createNameAndPhoneNumberSearchValuesFromPerson(Person person, PhoneNumber phoneNumberToSearchOn) throws ConnectException
    {
        List<String> searchValues = Lists.newArrayList();

        searchValues.add(person.getFirstName());
        searchValues.add(person.getLastName());
        searchValues.add(phoneNumberToSearchOn.getDigitsOnly());

        return searchValues;
    }

    private boolean matchHasBeenDeleted(String matchId)
    {
        return deleteService.personIsDeleted(matchId);
    }

    SearchResponse buildSearchResponse(Float score, IndexData values, String type)
    {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setScore(score);
        searchResponse.setResultValues(values);
        searchResponse.setId(searchResponse.getResultValues().getGlobalRegistryId());
        searchResponse.setType(type);

        return searchResponse;
    }

    List<IndexData> buildListOfValueMaps(List<AnyTypeArray> searchResultValues, IndexType indexType)
    {
        switch(indexType)
        {
            case ADDRESS:
                return buildListOfValueMapsForAddressIndex(searchResultValues);
            case COMMUNICATION:
                return buildListOfValueMapsForCommunicationIndex(searchResultValues);
            default:
                return null;
        }
    }

    List<IndexData> buildListOfValueMapsForAddressIndex(List<AnyTypeArray> searchResultValues)
    {
        List<IndexData> valueMapList = Lists.newArrayList();

        for(AnyTypeArray valueSet : searchResultValues)
        {
            valueMapList.add(buildNameAndAddressResultValues(valueSet.getItem()));
        }
        return valueMapList;
    }

    List<IndexData> buildListOfValueMapsForCommunicationIndex(List<AnyTypeArray> searchResultValues)
    {
        List<IndexData> valueMapList = Lists.newArrayList();

        for(AnyTypeArray valueSet : searchResultValues)
        {
            valueMapList.add(buildNameAndCommunicationResultValues(valueSet.getItem()));
        }
        return valueMapList;
    }

    IndexData buildNameAndAddressResultValues(List<Object> searchResultValues)
    {
        NameAndAddressIndexData valueMap = new NameAndAddressIndexData();

        valueMap.putFirstName(searchResultValues.get(0));
        valueMap.putLastName(searchResultValues.get(1));
        valueMap.putAddressLine1(searchResultValues.get(2));
        valueMap.putAddressLine2(searchResultValues.get(3));
        valueMap.putCity(searchResultValues.get(4));
        valueMap.putState(searchResultValues.get(5));
        valueMap.putZipCode(searchResultValues.get(6));
        valueMap.putStandardizedFirstName(searchResultValues.get(7));
        // Value 8 is not mapped to anything yet
        valueMap.putPartyId(searchResultValues.get(9));
        valueMap.putGlobalRegistryId(searchResultValues.get(10));

        return valueMap;
    }

    IndexData buildNameAndCommunicationResultValues(List<Object> searchResultValues)
    {
        NameAndCommunicationIndexData valueMap = new NameAndCommunicationIndexData();

        valueMap.putFirstName(searchResultValues.get(0));
        valueMap.putLastName(searchResultValues.get(1));
        valueMap.putCommunicationData(searchResultValues.get(2));
        valueMap.putPartyId(searchResultValues.get(3));
        valueMap.putGlobalRegistryId(searchResultValues.get(4));

        return valueMap;
    }

    SearchResponseList buildSearchResponses(ServiceResult searchResult, IndexType indexType)
    {
        SearchResponseList searchResponseList = new SearchResponseList();
        List<AnyTypeArray> searchResultValues = searchResult.getRows();

        if(searchResultValues == null || searchResultValues.isEmpty())
        {
            return null;
        }

        List<IndexData> valueMapList = buildListOfValueMaps(searchResultValues, indexType);
        List<Float> scoreList = searchResult.getScores();

        for(int i = 0; i < scoreList.size(); i++)
        {
            searchResponseList.add(buildSearchResponse(scoreList.get(i), valueMapList.get(i), searchResult.getType()));
        }

        return searchResponseList;
    }

    boolean hasAPerfectMatch(SearchResponseList searchResponseList)
    {
        for(SearchResponse response : searchResponseList)
        {
            if(response.getScore() == 1.0D) return true;
        }
        return false;
    }
}
