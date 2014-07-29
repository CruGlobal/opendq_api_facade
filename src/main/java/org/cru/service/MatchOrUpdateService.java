package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
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

    public List<OafResponse> matchOrUpdatePerson(Person person) throws ConnectException
    {
        List<OafResponse> matchResponseList = matchingService.findMatches(person, "Match");

        if(matchResponseList == null || matchResponseList.isEmpty())
        {
            return updatePersonIfPossible(person);
        }
        else
        {
            for(OafResponse matchResponse : matchResponseList)
            {
                if(matchResponse.getMatchId().equalsIgnoreCase(person.getId()))
                {
                    updatePersonIfPossible(person);
                }
            }

            // There are matches, but none of them have a matching Global Registry ID, so return the highest scoring conflict
            matchResponseList.get(0).setAction(Action.CONFLICT);
            return Lists.newArrayList(matchResponseList.get(0));
        }
    }

    private List<OafResponse> updatePersonIfPossible(Person person) throws ConnectException
    {
       SearchResponse searchResponse = matchingService.searchForPerson(person, "MatchId");

        if(searchResponse == null)
        {
            return null;
        }

        String partyId = searchResponse.getResultValues().getPartyId();
        RealTimeObjectActionDTO foundMdmPerson = matchingService.findMatchInMdm(partyId);

        if(foundMdmPerson == null)
        {
            return null;
        }

        updateService.updatePerson(person, foundMdmPerson, "MatchOrUpdate");

        OafResponse matchResponse = new OafResponse();
        matchResponse.setMatchId(person.getId());

        //We didn't find the match based on the normal index search values, so let's indicate that in some way
        matchResponse.setConfidenceLevel(0.0D);
        matchResponse.setAction(Action.UPDATE);

        return Lists.newArrayList(matchResponse);
    }
}
