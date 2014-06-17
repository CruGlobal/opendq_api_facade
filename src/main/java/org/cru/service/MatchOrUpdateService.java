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
    private AddService addService;

    public MatchOrUpdateService() {}

    @Inject
    public MatchOrUpdateService(MatchingService matchingService, AddService addService)
    {
        this.matchingService = matchingService;
        this.addService = addService;
    }

    public MatchResponse matchOrUpdatePerson(Person person) throws ConnectException
    {
        MatchResponse matchResponse = matchingService.findMatch(person, "Match");

        if(matchResponse == null)
        {
            addService.addPerson(person, "MatchOrUpdate");
        }
        else   // This means there is a match found for the updated information
        {
            // If it is the same global registry ID, then go ahead and do the add (do we even need to if we found it?)
            if(matchResponse.getMatchId().equals(person.getRowId()))
            {
                addService.addPerson(person, "MatchOrUpdate");
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
