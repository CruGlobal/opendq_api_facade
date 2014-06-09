package org.cru.service;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by William.Randall on 6/9/14.
 */
@Test
public class MatchingServiceTest
{
    MatchingService matchingService = new MatchingService();

    @Test
    public void testFindMatch()
    {
        assertEquals(matchingService.findMatch(null), "TEST_ID");
    }
}
