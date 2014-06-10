package org.cru.model;

import java.util.Map;

/**
 * Object to hold information that comes back from OpenDQ about the match found
 * in a more easily maintainable manner
 *
 * Created by William.Randall on 6/10/14.
 */
public class SearchResponse
{
    private double score;
    private String rowId;
    private Map<String, Object> resultValues;

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public String getRowId()
    {
        return rowId;
    }

    public void setRowId(String rowId)
    {
        this.rowId = rowId;
    }

    public Map<String, Object> getResultValues()
    {
        return resultValues;
    }

    public void setResultValues(Map<String, Object> resultValues)
    {
        this.resultValues = resultValues;
    }
}
