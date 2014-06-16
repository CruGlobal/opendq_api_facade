package org.cru.model;

import org.cru.webservices.MatchingResource;

/**
 * An object to hold data our {@link MatchingResource} endpoint will send back to the client
 *
 * Created by William.Randall on 6/11/14.
 */
public class MatchResponse
{
    private String matchId;
    private double confidenceLevel;
    private String message;

    public String getMatchId()
    {
        return matchId;
    }

    public void setMatchId(String matchId)
    {
        this.matchId = matchId;
    }

    public double getConfidenceLevel()
    {
        return confidenceLevel;
    }

    public void setConfidenceLevel(double confidenceLevel)
    {
        this.confidenceLevel = confidenceLevel;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
