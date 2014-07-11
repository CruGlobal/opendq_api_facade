package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.MatchResponse;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.qualifiers.Match;
import org.cru.util.ResponseMessage;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

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

    public MatchResponse matchOrUpdatePerson(Person person) throws ConnectException
    {
        MatchResponse matchResponse = matchingService.findMatch(person, "Match");

        if(matchResponse == null || matchResponse.getMatchId().equalsIgnoreCase(person.getId()))
        {
            SearchResponse searchResponse = matchingService.findMatchById(person.getId(), "MatchId");

            if(searchResponse == null)
            {
                return null;
            }

            String partyId = (String) searchResponse.getResultValues().get("partyId");
            RealTimeObjectActionDTO foundMdmPerson = matchingService.findMatchInMdm(partyId);

            if(foundMdmPerson == null)
            {
                return null;
            }

            updateService.updatePerson(person, foundMdmPerson, "MatchOrUpdate");

            if(matchResponse == null)
            {
                matchResponse = new MatchResponse();
                matchResponse.setMatchId(person.getId());
                matchResponse.setConfidenceLevel(0.0D);
            }
            matchResponse.setMessage(ResponseMessage.UPDATED.getMessage());

            return matchResponse;
        }
        else   // This means there is a match found for the updated information
        {
            matchResponse.setMessage(ResponseMessage.CONFLICT.getMessage());
            return matchResponse;
        }
    }
}
