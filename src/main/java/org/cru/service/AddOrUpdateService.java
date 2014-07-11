package org.cru.service;

import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;
import org.cru.qualifiers.Match;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Created by William.Randall on 7/11/2014.
 */
public class AddOrUpdateService
{
    private AddService addService;
    private MatchOrUpdateService matchOrUpdateService;

    @SuppressWarnings("unused") //Used by CDI
    public AddOrUpdateService() {}

    @Inject
    public AddOrUpdateService(@Add AddService addService, MatchOrUpdateService matchOrUpdateService)
    {
        this.addService = addService;
        this.matchOrUpdateService = matchOrUpdateService;
    }

    public MatchResponse addOrUpdate(Person person) throws ConnectException
    {
        String slotName = "AddOrUpdate";

        MatchResponse matchResponse = matchOrUpdateService.matchOrUpdatePerson(person);

        //No match found, so add the person
        if(matchResponse == null)
        {
            addService.addPerson(person, slotName);
            return null;
        }
        else
        {
            return matchResponse;
        }
    }
}
