package org.cru.model;

import org.cru.model.collections.SearchResponseList;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Created by William.Randall on 7/28/2014.
 */
@Test
public class SearchResponseListTest
{
    @Test
    public void testSortByScore()
    {
        SearchResponseList searchResponseList = createSearchResponseListForScoreSort();
        searchResponseList.sortListByScore();

        assertEquals(searchResponseList.get(0).getScore(), 1.0D);
        assertEquals(searchResponseList.get(1).getScore(), 2.34D);
        assertEquals(searchResponseList.get(2).getScore(), 1.75D);
        assertEquals(searchResponseList.get(3).getScore(), 0.18D);
    }

    @Test
    public void testRemoveDuplicateResults()
    {
        SearchResponseList searchResponseList = createSearchResponseListWithDuplicates();
        searchResponseList.removeDuplicateResults();

        assertEquals(searchResponseList.size(), 1);
    }

    private SearchResponseList createSearchResponseListForScoreSort()
    {
        SearchResponseList searchResponseList = new SearchResponseList();

        SearchResponse perfectMatch = new SearchResponse();
        perfectMatch.setScore(1.0D);

        SearchResponse overMatch = new SearchResponse();
        overMatch.setScore(2.34D);

        SearchResponse underMatch = new SearchResponse();
        underMatch.setScore(0.18D);

        SearchResponse overMatch2 = new SearchResponse();
        overMatch2.setScore(1.75D);

        searchResponseList.add(overMatch2);
        searchResponseList.add(underMatch);
        searchResponseList.add(overMatch);
        searchResponseList.add(perfectMatch);

        return searchResponseList;
    }

    private SearchResponseList createSearchResponseListWithDuplicates()
    {
        SearchResponseList searchResponseList = new SearchResponseList();

        SearchResponse firstMatch = new SearchResponse();
        firstMatch.setScore(1.0D);

        Map<String, Object> firstMapValues = new HashMap<String, Object>();
        firstMapValues.put("firstName", "Test");
        firstMapValues.put("lastName", "Tester");
        firstMapValues.put("address1", "1125 Way Blvd");
        firstMapValues.put("address2", "NULLDATA");
        firstMapValues.put("city", "Orlando");
        firstMapValues.put("state", "FL");
        firstMapValues.put("zip", "32832");
        firstMapValues.put("standardizedFirstName", "TEST");
        firstMapValues.put("partyId", "1");

        firstMatch.setResultValues(firstMapValues);

        SearchResponse secondMatch = new SearchResponse();
        secondMatch.setScore(1.0D);

        Map<String, Object> secondMapValues = new HashMap<String, Object>();
        secondMapValues.put("firstName", "Test");
        secondMapValues.put("lastName", "Tester");
        secondMapValues.put("address1", "1125 Way Blvd");
        secondMapValues.put("address2", "NULLDATA");
        secondMapValues.put("city", "Orlando");
        secondMapValues.put("state", "FL");
        secondMapValues.put("zip", "32832");
        secondMapValues.put("standardizedFirstName", "TEST");
        secondMapValues.put("partyId", "2");

        secondMatch.setResultValues(secondMapValues);

        searchResponseList.add(firstMatch);
        searchResponseList.add(secondMatch);

        return searchResponseList;
    }
}
