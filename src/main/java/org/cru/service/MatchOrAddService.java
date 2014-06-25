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
    private AddService addService;
    private MatchingService matchingService;

    @SuppressWarnings("unused")  //used by CDI
    public MatchOrAddService() {}

    @Inject
    public MatchOrAddService(MatchingService matchingService, AddService addService)
    {
        this.matchingService = matchingService;
        this.addService = addService;
    }

    public MatchResponse matchOrAddPerson(Person person) throws ConnectException
    {
        String slotName = "MatchOrAdd";
        MatchResponse matchResponse = matchingService.findMatch(person, slotName);

        if(matchResponse != null) return matchResponse;

        addService.addPerson(person, slotName);
        return null;
    }
}
