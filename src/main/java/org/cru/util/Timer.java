package org.cru.util;

import org.apache.log4j.Logger;

/**
 * Created by William.Randall on 8/19/2014.
 */
public class Timer
{
    private static Logger timerLog = Logger.getLogger("TIMER");

    public static void logTime(long startTime, long endTime, String codeLabel)
    {
        long duration = (endTime - startTime) / 1000000;
        timerLog.debug(codeLabel + " took " + duration + " milliseconds to complete");
    }
}
