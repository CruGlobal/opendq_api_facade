package org.cru.util;

import com.google.common.base.Strings;

/**
 * Message to send back to the client depending on the action taken.
 *
 * Created by William.Randall on 6/16/14.
 */
public enum Action
{
    CONFLICT,
    ADD,
    MATCH,
    DELETE,
    UPDATE;

    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
