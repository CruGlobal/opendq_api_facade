package org.cru.service;

import org.cru.model.MatchResponse;
import org.cru.model.Person;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Handle the logic of either matching or adding the {@link Person} given
 *
 * Created by William.Randall on 6/9/14.
 */
public class MatchOrAddService
{
    @Inject
    private AddService addService;
    @Inject
    private MatchingService matchingService;

    public MatchResponse matchOrAddPerson(Person person) throws ConnectException
    {
        String slotName = "AddOrMatch";
        MatchResponse matchResponse = matchingService.findMatch(person, slotName);

        if(matchResponse != null) return matchResponse;

        addService.addPerson(person, slotName);
        return null;
    }

    void setAddService(AddService addService)
    {
        this.addService = addService;
    }

    void setMatchingService(MatchingService matchingService)
    {
        this.matchingService = matchingService;
    }
}
