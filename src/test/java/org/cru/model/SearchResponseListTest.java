package org.cru.model;

import org.cru.model.collections.SearchResponseList;
import org.cru.model.map.IndexData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    @Test
    public void testEquals()
    {
        SearchResponseList searchResponseList = createSearchResponseListWithDuplicates();
        assertEquals(searchResponseList.get(0), searchResponseList.get(1));
        assertEquals(searchResponseList.get(1), searchResponseList.get(2));
    }

    private SearchResponseList createSearchResponseListForScoreSort()
    {
        SearchResponseList searchResponseList = new SearchResponseList();

        SearchResponse perfectMatch = new SearchResponse();
        perfectMatch.setScore(1.0D);
        perfectMatch.setId("1");

        SearchResponse overMatch = new SearchResponse();
        overMatch.setScore(2.34D);
        overMatch.setId("2");

        SearchResponse underMatch = new SearchResponse();
        underMatch.setScore(0.18D);
        underMatch.setId("3");

        SearchResponse overMatch2 = new SearchResponse();
        overMatch2.setScore(1.75D);
        overMatch2.setId("4");

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

        IndexData firstMapValues = new IndexData();
        firstMapValues.putFirstName("Test");
        firstMapValues.putLastName("Tester");
        firstMapValues.putAddressLine1("1125 Way Blvd");
        firstMapValues.putAddressLine2("NULLDATA");
        firstMapValues.putCity("Orlando");
        firstMapValues.putState("FL");
        firstMapValues.putZipCode("32832");
        firstMapValues.putStandardizedFirstName("TEST");
        firstMapValues.putPartyId("1");

        firstMatch.setResultValues(firstMapValues);
        firstMatch.setId("1");

        SearchResponse secondMatch = new SearchResponse();
        secondMatch.setScore(1.0D);

        IndexData secondMapValues = new IndexData();
        secondMapValues.putFirstName("Test");
        secondMapValues.putLastName("Tester");
        secondMapValues.putAddressLine1("1125 Way Blvd");
        secondMapValues.putAddressLine2("NULLDATA");
        secondMapValues.putCity("Orlando");
        secondMapValues.putState("FL");
        secondMapValues.putZipCode("32832");
        secondMapValues.putStandardizedFirstName("TEST");
        secondMapValues.putPartyId("2");

        secondMatch.setResultValues(secondMapValues);
        secondMatch.setId("1");

        SearchResponse thirdMatch = new SearchResponse();
        thirdMatch.setScore(1.0D);

        IndexData thirdMatchValues = new IndexData();
        thirdMatchValues.putFirstName("Test");
        thirdMatchValues.putLastName("Tester");
        thirdMatchValues.putAddressLine1("1125 Way Blvd");
        thirdMatchValues.putAddressLine2("NULLDATA");
        thirdMatchValues.putCity("Orlando");
        thirdMatchValues.putState("FL");
        thirdMatchValues.putZipCode("32832");
        thirdMatchValues.putStandardizedFirstName("TEST");
        thirdMatchValues.putPartyId("3");

        thirdMatch.setResultValues(thirdMatchValues);
        thirdMatch.setId("1");

        searchResponseList.add(firstMatch);
        searchResponseList.add(secondMatch);
        searchResponseList.add(thirdMatch);

        return searchResponseList;
    }
}
