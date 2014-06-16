package org.cru.util;

/**
 * Created by William.Randall on 6/16/14.
 */
public enum ResponseMessage
{
    CONFLICT("The data you passed in matches another person.  Please merge these two records."),
    ADDED("Successfully added person"),
    FOUND("Found a match"),
    DELETED("Successfully deleted person"),
    UPDATED("Successfully updated person");

    String message;

    private ResponseMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }
}
