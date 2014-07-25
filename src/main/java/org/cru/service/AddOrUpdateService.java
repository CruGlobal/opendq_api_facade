package org.cru.service;

import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Handle logic for either updating or adding a {@link Person}.
 *
 * If the person is found and the ID is the same, update the person.
 * If the person is found and the ID is different, return a conflict response.
 * If the person is not found, add the person.
 *
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

    public OafResponse addOrUpdate(Person person) throws ConnectException
    {
        String slotName = "AddOrUpdate";

        OafResponse matchOrUpdateResponse = matchOrUpdateService.matchOrUpdatePerson(person);

        //No match found, so add the person
        if(matchOrUpdateResponse == null)
        {
            addService.addPerson(person, slotName);
            return null;
        }
        else
        {
            return matchOrUpdateResponse;
        }
    }
}
