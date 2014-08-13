package org.cru.model.collections;

import com.google.common.collect.Lists;
import org.cru.model.NicknameResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Contains logic for filtering and sorting a {@link List} of {@link NicknameResponse} objects
 *
 * Created by William.Randall on 8/13/2014.
 */
public class NicknameResponseList implements List<NicknameResponse>
{
    private List<NicknameResponse> internalList;
    
    public NicknameResponseList()
    {
        this.internalList = Lists.newArrayList();
    }

    public void sortListByScore()
    {
        Collections.sort(internalList, scoreComparator());
    }

    /**
     * Sorts SearchResponses by score in descending order
     */
    private static Comparator<NicknameResponse> scoreComparator()
    {
        return new Comparator<NicknameResponse>()
        {
            public int compare(NicknameResponse response1, NicknameResponse response2)
            {
                if(response1.getScore() == response2.getScore()) return 0;
                if(response1.getScore() > response2.getScore()) return -1;
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
    public Iterator<NicknameResponse> iterator()
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
    public boolean add(NicknameResponse NicknameResponse)
    {
        return internalList.add(NicknameResponse);
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
    public boolean addAll(Collection<? extends NicknameResponse> c)
    {
        return internalList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NicknameResponse> c)
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
    public NicknameResponse get(int index)
    {
        return internalList.get(index);
    }

    @Override
    public NicknameResponse set(int index, NicknameResponse element)
    {
        return internalList.set(index, element);
    }

    @Override
    public void add(int index, NicknameResponse element)
    {
        internalList.add(index, element);
    }

    @Override
    public NicknameResponse remove(int index)
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
    public ListIterator<NicknameResponse> listIterator()
    {
        return internalList.listIterator();
    }

    @Override
    public ListIterator<NicknameResponse> listIterator(int index)
    {
        return internalList.listIterator(index);
    }

    @Override
    public List<NicknameResponse> subList(int fromIndex, int toIndex)
    {
        return internalList.subList(fromIndex, toIndex);
    }
}
