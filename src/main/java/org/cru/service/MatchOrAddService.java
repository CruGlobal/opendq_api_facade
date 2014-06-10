package org.cru.service;

import org.cru.model.Person;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Created by William.Randall on 6/9/14.
 */
public class MatchOrAddService
{
    @Inject
    private AddService addService;
    @Inject
    private MatchingService matchingService;

    public String matchOrAddPerson(Person person) throws ConnectException
    {
        String slotName = "AddOrMatch";
        String matchId = matchingService.findMatch(person, slotName);

        if(matchId != null) return matchId;

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
