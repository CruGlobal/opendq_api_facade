package org.cru.service;

import org.cru.model.Person;

/**
 * Service to handle the complexity of matching person fields
 *
 * Created by William.Randall on 6/9/14.
 */
public class MatchingService
{
    private String matchId;     // Global Registry ID that matches data given


    public String findMatch(Person person)
    {
        //TODO: call OpenDQ matching stuff (for each field separately?)
        //TODO: only set the match Id if the confidence level > ?? else null
        matchId = "TEST_ID";
        return matchId;
    }

    public String getMatchId()
    {
        return matchId;
    }
}
