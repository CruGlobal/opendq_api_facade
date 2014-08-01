package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.model.collections.SearchResponseList;
import org.cru.qualifiers.Match;
import org.cru.util.Action;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.List;

/**
 * Handle the logic of either matching or updating the {@link Person} given
 *
 * Created by William.Randall on 6/16/14.
 */
public class MatchOrUpdateService
{
    private MatchingService matchingService;
    private UpdateService updateService;

    @SuppressWarnings("unused")  //used by CDI
    public MatchOrUpdateService() {}

    @SuppressWarnings("unused")  //used by CDI
    @Inject
    public MatchOrUpdateService(@Match MatchingService matchingService, UpdateService updateService)
    {
        this.matchingService = matchingService;
        this.updateService = updateService;
    }

    /**
     * Attempt to update the person passed in.
     * If no matches are found, do not update and return null.
     * If matches are found and none of the matches are the same person as the one passed in, return a conflict response.
     * Otherwise, update the person and return an update response.
     *
     * @throws ConnectException if it fails to connect to the real time matching service
     */
    public List<OafResponse> matchOrUpdatePerson(Person person) throws ConnectException
    {
        //Find all possible matches in the index
        SearchResponseList searchResponseList = matchingService.findMatchesAllData(person, "Match");
        boolean updated = false;

        //If there are no matches using the updated data, attempt to update
        if(searchResponseList == null || searchResponseList.isEmpty())
        {
            return updatePersonIfPossible(person);
        }
        else //matches were found using the updated data
        {
            //Find the person in the Mdm to get the correct party id
            RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(person.getId());
            if(foundPerson == null) return null;

            for(SearchResponse searchResponse : searchResponseList)
            {
                //If the match has the same Party Id as the Person found in MDM, update
                if(searchResponse.getResultValues().getPartyId().equalsIgnoreCase(foundPerson.getObjectEntity().getPartyId()))
                {
                    updateService.updatePerson(person, foundPerson, "MatchOrUpdate");
                    updated = true;
                }
            }
            if(updated)
            {
                return buildResponseList(person);
            }

            // There are matches, but none of them have a matching Party ID, so return the highest scoring conflict
            List<OafResponse> matchResponseList = matchingService.buildOafResponseList(searchResponseList);
            matchResponseList.get(0).setAction(Action.CONFLICT);
            return Lists.newArrayList(matchResponseList.get(0));
        }
    }

    private List<OafResponse> updatePersonIfPossible(Person person) throws ConnectException
    {
        //Find the person in the MDM by global registry id
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(person.getId());

        //If nobody was found, we can't update, so just return null
        if(foundPerson == null) return null;

        updateService.updatePerson(person, foundPerson, "MatchOrUpdate");

        OafResponse matchResponse = new OafResponse();
        matchResponse.setMatchId(person.getId());
        //We didn't find the match based on the normal index search values, so let's indicate that in some way
        matchResponse.setConfidenceLevel(0.0D);
        matchResponse.setAction(Action.UPDATE);

        return Lists.newArrayList(matchResponse);
    }

    private List<OafResponse> buildResponseList(Person person)
    {
        OafResponse matchResponse = new OafResponse();
        matchResponse.setMatchId(person.getId());
        matchResponse.setConfidenceLevel(1.0D);
        matchResponse.setAction(Action.UPDATE);

        return Lists.newArrayList(matchResponse);
    }
}
