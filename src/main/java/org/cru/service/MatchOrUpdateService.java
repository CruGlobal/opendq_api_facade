package org.cru.service;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.util.ResponseMessage;

import javax.inject.Inject;
import java.net.ConnectException;

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

    @Inject
    public MatchOrUpdateService(MatchingService matchingService, UpdateService updateService)
    {
        this.matchingService = matchingService;
        this.updateService = updateService;
    }

    public MatchResponse matchOrUpdatePerson(Person person) throws ConnectException
    {
        MatchResponse matchResponse = matchingService.findMatch(person, "Match");

        if(matchResponse == null)
        {
            updateService.updatePerson(person, "MatchOrUpdate");
        }
        else   // This means there is a match found for the updated information
        {
            // If it is the same global registry ID, then go ahead and do the add (do we even need to if we found it?)
            if(matchResponse.getMatchId().equals(person.getGlobalRegistryId()))
            {
                updateService.updatePerson(person, "MatchOrUpdate");
            }
            else
            {
                matchResponse.setMessage(ResponseMessage.CONFLICT.getMessage());
                return matchResponse;
            }
        }

        return null;
    }
}
