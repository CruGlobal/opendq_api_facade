package org.cru.model.collections;

import com.google.common.collect.Lists;
import org.cru.model.ResultData;
import org.cru.model.SearchResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains logic for filtering and sorting a {@link List} of {@link SearchResponse} objects
 *
 * Created by William.Randall on 7/28/2014.
 */
public class SearchResponseList implements List<SearchResponse>
{
    private List<SearchResponse> internalList;

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

        SearchResponseList newList = new SearchResponseList();
        for(int i = 0; i < internalList.size(); i++)
        {
            if(i == internalList.size() - 1) //last in the list
            {
                if(areResponsesEffectivelyEqual(internalList.get(i), internalList.get(i - 1)))
                {
                    break;
                }
                else newList.add(internalList.get(i));
            }
            else
            {
                if(areResponsesEffectivelyEqual(internalList.get(i), internalList.get(i + 1)))
                {
                    newList.add(internalList.get(i));
                    i++;
                }
                else
                {
                    newList.add(internalList.get(i));
                }
            }
        }

        internalList = newList;
    }

    //TODO: May need to change it so that closer to 1.0 is closer to first for over
    /**
     * Sorts SearchResponses in the following way:
     *   - 1.0 score(s) will be first
     *   - anything above 1.0 comes next in descending order (e.g. 2.34, 2.14, 1.75, etc)
     *   - anything below 1.0 comes afterward in descending order (e.g. 0.84, 0.55, 0.18, etc)
     */
    private static Comparator<SearchResponse> scoreComparator()
    {
        return new Comparator<SearchResponse>()
        {
            public int compare(SearchResponse searchResponse1, SearchResponse searchResponse2)
            {
                if(searchResponse1.getScore() == 1.0D)
                {
                    if(searchResponse2.getScore() == 1.0D) return 0;
                    return -1;
                }
                if(searchResponse1.getScore() > searchResponse2.getScore())
                {
                    return -1;
                }
                else if(searchResponse1.getScore() < searchResponse2.getScore())
                {
                    return 1;
                }
                else return 0;
            }
        };
    }

    private boolean areResponsesEffectivelyEqual(SearchResponse searchResponse1, SearchResponse searchResponse2)
    {
        ResultData resultValues1 = searchResponse1.getResultValues();
        ResultData resultValues2 = searchResponse2.getResultValues();
        return
            (resultValues1.getFirstName()).equalsIgnoreCase(resultValues2.getFirstName()) &&
            (resultValues1.getLastName()).equalsIgnoreCase(resultValues2.getLastName()) &&
            (resultValues1.getAddressLine1()).equalsIgnoreCase(resultValues2.getAddressLine1()) &&
            (resultValues1.getAddressLine2()).equalsIgnoreCase(resultValues2.getAddressLine2()) &&
            (resultValues1.getCity()).equalsIgnoreCase(resultValues2.getCity()) &&
            (resultValues1.getState()).equalsIgnoreCase(resultValues2.getState()) &&
            (resultValues1.getZip()).equalsIgnoreCase(resultValues2.getZip()) &&
            (resultValues1.getStandardizedFirstName()).equalsIgnoreCase(resultValues2.getStandardizedFirstName());
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
