package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.OafResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.model.collections.SearchResponseList;
import org.cru.qualifiers.Match;
import org.cru.util.Action;

import javax.inject.Inject;
import java.net.ConnectException;
import java.util.List;

/**
 * Handle the logic of either matching or updating the {@link Person} given
 *
 * Created by William.Randall on 6/16/14.
 */
public class MatchOrUpdateService
{
    private MatchingService matchingService;
    private UpdateService updateService;

    @SuppressWarnings("unused")  //used by CDI
    public MatchOrUpdateService() {}

    @SuppressWarnings("unused")  //used by CDI
    @Inject
    public MatchOrUpdateService(@Match MatchingService matchingService, UpdateService updateService)
    {
        this.matchingService = matchingService;
        this.updateService = updateService;
    }

    /**
     * Attempt to update the person passed in.
     * If no matches are found, do not update and return null.
     * Otherwise, update the person and return an update response.
     *
     * @throws ConnectException if it fails to connect to the real time matching service
     */
    public List<OafResponse> matchOrUpdatePerson(Person person) throws ConnectException
    {
        //Find the person in the Mdm to get the correct party id
        RealTimeObjectActionDTO foundPerson = matchingService.findMatchInMdmByGlobalRegistryId(person.getId());

        if(foundPerson == null) return null;

        updateService.updatePerson(person, foundPerson, "MatchOrUpdate");
        return buildResponseList(person);
    }

    private List<OafResponse> buildResponseList(Person person)
    {
        OafResponse matchResponse = new OafResponse();
        matchResponse.setMatchId(person.getId());
        matchResponse.setConfidenceLevel(1.0D);
        matchResponse.setAction(Action.UPDATE);

        return Lists.newArrayList(matchResponse);
    }
}
