package org.cru.model.map;

import com.google.common.collect.Lists;
import org.cru.model.Person;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Add a layer of abstraction between our pretty object data in {@link Person}
 * and the not-so-pretty index (FIELD1-FIELD10) field names.  This makes the
 * index less painful to work with.
 *
 * Created by William.Randall on 8/7/2014.
 */
public abstract class IndexData implements Map<String, Object>
{
    protected Map<String, Object> internalMap;

    protected String FIRST_NAME_KEY;
    protected String LAST_NAME_KEY;
    protected String PARTY_ID_KEY;
    protected String GR_ID_KEY;


    public IndexData()
    {
        internalMap = new LinkedHashMap<String, Object>();
    }

    public String getFirstName()
    {
        return (String)internalMap.get(FIRST_NAME_KEY);
    }

    public void putFirstName(Object firstName)
    {
        internalMap.put(FIRST_NAME_KEY, firstName);
    }

    public String getLastName()
    {
        return (String)internalMap.get(LAST_NAME_KEY);
    }

    public void putLastName(Object lastName)
    {
        internalMap.put(LAST_NAME_KEY, lastName);
    }

    public String getPartyId()
    {
        return (String)internalMap.get(PARTY_ID_KEY);
    }

    public void putPartyId(Object partyId)
    {
        internalMap.put(PARTY_ID_KEY, partyId);
    }

    public String getGlobalRegistryId()
    {
        return (String)internalMap.get(GR_ID_KEY);
    }

    public void putGlobalRegistryId(Object globalRegistryId)
    {
        internalMap.put(GR_ID_KEY, globalRegistryId);
    }

    /**
     * Returns a String List representation of the internal map's values.
     */
    public List<String> stringValues()
    {
        List<String> stringCollection = Lists.newArrayList();

        for(Object value : internalMap.values())
        {
            stringCollection.add((String)value);
        }

        return stringCollection;
    }

    public abstract List<String> getValuesForEquality();

    @Override
    public boolean equals(Object objectToCompare)
    {
        if(objectToCompare == this) return true;
        if(objectToCompare == null || !(objectToCompare instanceof IndexData)) return false;

        IndexData indexDataToCompare = (IndexData) objectToCompare;
        for(Entry<String, Object> entry : internalMap.entrySet())
        {
            String key = entry.getKey();
            if(key.equals(PARTY_ID_KEY)) continue;
            Object value = entry.getValue();
            if(value instanceof String)
            {
                if(!(((String) value).equalsIgnoreCase(indexDataToCompare.get(key))))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for(Entry<String, Object> entry : internalMap.entrySet())
        {
            //I want the objects to be effectively equal even if they have different party ids
            if(entry.getKey().equals(PARTY_ID_KEY)) continue;
            Object value = entry.getValue();
            //skip nulls
            if(value == null) continue;
            //Ignore case
            if(value instanceof String) result = 31 * result + ((String)value).toUpperCase().hashCode();
            else result = 31 * result + value.hashCode();
        }
        return result;
    }

    @Override
    public int size()
    {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty()
    {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return internalMap.containsValue(value);
    }

    @Override
    public String get(Object key)
    {
        return (String)internalMap.get(key);
    }

    @Override
    public String put(String key, Object value)
    {
        return (String)internalMap.put(key, value);
    }

    @Override
    public String remove(Object key)
    {
        return (String)internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m)
    {
        internalMap.putAll(m);
    }

    @Override
    public void clear()
    {
        internalMap.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return internalMap.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return internalMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return internalMap.entrySet();
    }
}
