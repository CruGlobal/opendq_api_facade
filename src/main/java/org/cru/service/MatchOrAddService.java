package org.cru.service;

import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;
import org.cru.qualifiers.Match;

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
    public MatchOrAddService(@Match MatchingService matchingService, @Add AddService addService)
    {
        this.matchingService = matchingService;
        this.addService = addService;
    }

    public OafResponse matchOrAddPerson(Person person) throws ConnectException
    {
        String slotName = "MatchOrAdd";
        OafResponse matchResponse = matchingService.findMatch(person, slotName);

        if(matchResponse != null) return matchResponse;

        addService.addPerson(person, slotName);
        return null;
    }
}
