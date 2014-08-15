package org.cru.model.collections;

import com.google.common.collect.Lists;
import org.cru.model.SearchResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Contains logic for filtering and sorting a {@link List} of {@link SearchResponse} objects
 *
 * Created by William.Randall on 7/28/2014.
 */
public class SearchResponseList implements List<SearchResponse>
{
    private List<SearchResponse> internalList;
    private static final double FUZZY_LOWER_THRESHOLD = 0.8D;
    private static final double BROAD_LOWER_THRESHOLD = 12.0D;

    public SearchResponseList()
    {
        this.internalList = Lists.newArrayList();
    }

    public void sortListByScore()
    {
        Collections.sort(internalList, scoreComparator());
    }

    public void removeDuplicateResults()
    {
        if(internalList == null || internalList.isEmpty() || internalList.size() == 1) return;
        Set<SearchResponse> responsesWithoutDuplicates = new LinkedHashSet<SearchResponse>(internalList);
        internalList = Lists.newArrayList(responsesWithoutDuplicates);
    }

    public void filterLowConfidenceMatches()
    {
        if(internalList == null || internalList.isEmpty()) return;
        String type = internalList.get(0).getType(); //All rows will have the same type

        if(type == null) filterLowConfidenceForFuzzyMatch();
        else if("B".equalsIgnoreCase(type)) filterLowConfidenceForBroadMatch();
    }

    public boolean hasAStrongMatch()
    {
        if(internalList == null || internalList.isEmpty()) return false;
        filterLowConfidenceMatches();
        return !internalList.isEmpty();
    }

    private void filterLowConfidenceForFuzzyMatch()
    {
        if(internalList == null || internalList.isEmpty()) return;

        List<SearchResponse> filteredList = Lists.newArrayList();
        for(SearchResponse response : internalList)
        {
            if(response.getScore() > FUZZY_LOWER_THRESHOLD) filteredList.add(response);
        }
        internalList = filteredList;
    }

    private void filterLowConfidenceForBroadMatch()
    {
        if(internalList == null || internalList.isEmpty()) return;

        List<SearchResponse> filteredList = Lists.newArrayList();
        for(SearchResponse response : internalList)
        {
            if(response.getScore() > BROAD_LOWER_THRESHOLD) filteredList.add(response);
        }
        internalList = filteredList;
    }
    
    /**
     * Sorts SearchResponses by score in descending order
     */
    private static Comparator<SearchResponse> scoreComparator()
    {
        return new Comparator<SearchResponse>()
        {
            public int compare(SearchResponse searchResponse1, SearchResponse searchResponse2)
            {
                if(searchResponse1.getScore() == searchResponse2.getScore()) return 0;
                if(searchResponse1.getScore() > searchResponse2.getScore()) return -1;
                return 1;
            }
        };
    }

    @Override
    public int size()
    {
        return internalList.size();
    }

    @Override
    public boolean isEmpty()
    {
        return internalList.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return internalList.contains(o);
    }

    @Override
    public Iterator<SearchResponse> iterator()
    {
        return internalList.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return internalList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return internalList.toArray(a);
    }

    @Override
    public boolean add(SearchResponse searchResponse)
    {
        return internalList.add(searchResponse);
    }

    @Override
    public boolean remove(Object o)
    {
        return internalList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return internalList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SearchResponse> c)
    {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends SearchResponse> c)
    {
        return internalList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return internalList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return internalList.retainAll(c);
    }

    @Override
    public void clear()
    {
        internalList.clear();
    }

    @Override
    public SearchResponse get(int index)
    {
        return internalList.get(index);
    }

    @Override
    public SearchResponse set(int index, SearchResponse element)
    {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, SearchResponse element)
    {
        internalList.add(index, element);
    }

    @Override
    public SearchResponse remove(int index)
    {
        return internalList.remove(index);
    }

    @Override
    public int indexOf(Object o)
    {
        return internalList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return internalList.lastIndexOf(o);
    }

    @Override
    public ListIterator<SearchResponse> listIterator()
    {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<SearchResponse> listIterator(int index)
    {
        return internalList.listIterator(index);
    }

    @Override
    public List<SearchResponse> subList(int fromIndex, int toIndex)
    {
        return internalList.subList(fromIndex, toIndex);
    }
}
