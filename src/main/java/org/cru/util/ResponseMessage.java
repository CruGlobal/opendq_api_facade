package org.cru.util;

/**
 * Message to send back to the client depending on the action taken.
 *
 * Created by William.Randall on 6/16/14.
 */
public enum ResponseMessage
{
    CONFLICT("The data you passed in matches another person."),
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
