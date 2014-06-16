package org.cru.service;

import org.cru.model.MatchResponse;
import org.cru.model.Person;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Handle the logic of either matching or updating the {@link Person} given
 *
 * Created by William.Randall on 6/16/14.
 */
public class MatchOrUpdateService
{
    @Inject
    private MatchingService matchingService;
    @Inject
    private AddService addService;

    public void matchOrUpdatePerson(Person person) throws ConnectException
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
                //TODO: implement some logic here (some sort of merge perhaps?  Or do we send this back to the client instead?)
            }
        }
    }
}
