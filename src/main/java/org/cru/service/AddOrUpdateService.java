package org.cru.service;

import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.qualifiers.Add;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.List;

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

    /**
     * If the {@link Person} is found, update that person.  Otherwise add the person.
     *
     * @return null if the person was added, otherwise return an update or conflict response.
     */
    public List<OafResponse> addOrUpdate(Person person) throws ConnectException
    {
        String slotName = "AddOrUpdate";

        List<OafResponse> matchOrUpdateResponseList = matchOrUpdateService.matchOrUpdatePerson(person);

        //No match found, so add the person
        if(matchOrUpdateResponseList == null || matchOrUpdateResponseList.isEmpty())
        {
            addService.addPerson(person, slotName);
            return null;
        }
        else
        {
            return matchOrUpdateResponseList;
        }
    }
}
