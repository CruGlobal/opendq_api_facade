package org.cru.model;

/**
 * Object to hold information that comes back from OpenDQ about the match found
 * in a more easily maintainable manner
 *
 * Created by William.Randall on 6/10/14.
 */
public class SearchResponse
{
    private double score;
    private String id;
    private ResultData resultValues;

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public ResultData getResultValues()
    {
        return resultValues;
    }

    public void setResultValues(ResultData resultValues)
    {
        this.resultValues = resultValues;
    }
}
