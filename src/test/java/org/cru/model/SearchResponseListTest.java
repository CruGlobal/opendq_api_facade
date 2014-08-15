package org.cru.model;

import org.cru.model.collections.SearchResponseList;
import org.cru.model.map.IndexData;
import org.cru.model.map.NameAndAddressIndexData;
import org.testng.annotations.Test;

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

        assertEquals(searchResponseList.get(0).getScore(), 0.94D);
        assertEquals(searchResponseList.get(1).getScore(), 0.75D);
        assertEquals(searchResponseList.get(2).getScore(), 0.34D);
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

        SearchResponse match1 = new SearchResponse();
        match1.setScore(0.94D);
        match1.setId("1");

        SearchResponse match2 = new SearchResponse();
        match2.setScore(0.34D);
        match2.setId("2");

        SearchResponse match3 = new SearchResponse();
        match3.setScore(0.18D);
        match3.setId("3");

        SearchResponse match4 = new SearchResponse();
        match4.setScore(0.75D);
        match4.setId("4");

        searchResponseList.add(match4);
        searchResponseList.add(match3);
        searchResponseList.add(match2);
        searchResponseList.add(match1);

        return searchResponseList;
    }

    private SearchResponseList createSearchResponseListWithDuplicates()
    {
        SearchResponseList searchResponseList = new SearchResponseList();

        SearchResponse firstMatch = new SearchResponse();
        firstMatch.setScore(1.0D);

        NameAndAddressIndexData firstMapValues = new NameAndAddressIndexData();
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

        NameAndAddressIndexData secondMapValues = new NameAndAddressIndexData();
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

        NameAndAddressIndexData thirdMatchValues = new NameAndAddressIndexData();
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
