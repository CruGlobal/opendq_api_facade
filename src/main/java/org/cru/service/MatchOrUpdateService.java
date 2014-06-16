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
        else
        {
            // This means there is a match for the updated information already in the index
            //TODO: implement some logic here
        }
    }
}
