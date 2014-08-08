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
public class IndexData implements Map<String, Object>
{
    private Map<String, Object> internalMap;

    public IndexData()
    {
        internalMap = new LinkedHashMap<String, Object>();
    }

    public String getFirstName()
    {
        return (String)internalMap.get("FIELD1");
    }

    public void putFirstName(Object firstName)
    {
        internalMap.put("FIELD1", firstName);
    }

    public String getLastName()
    {
        return (String)internalMap.get("FIELD2");
    }

    public void putLastName(Object lastName)
    {
        internalMap.put("FIELD2", lastName);
    }

    public String getAddressLine1()
    {
        return (String)internalMap.get("FIELD3");
    }

    public void putAddressLine1(Object addressLine1)
    {
        internalMap.put("FIELD3", addressLine1);
    }

    public String getAddressLine2()
    {
        return (String)internalMap.get("FIELD4");
    }

    public void putAddressLine2(Object addressLine2)
    {
        if(addressLine2 == null) internalMap.put("FIELD4", "NULLDATA");
        else internalMap.put("FIELD4", addressLine2);
    }

    public String getCity()
    {
        return (String)internalMap.get("FIELD5");
    }

    public void putCity(Object city)
    {
        internalMap.put("FIELD5", city);
    }

    public String getState()
    {
        return (String)internalMap.get("FIELD6");
    }

    public void putState(Object state)
    {
        internalMap.put("FIELD6", state);
    }

    public String getZipCode()
    {
        return (String)internalMap.get("FIELD7");
    }

    public void putZipCode(Object zipCode)
    {
        internalMap.put("FIELD7", zipCode);
    }

    public String getStandardizedFirstName()
    {
        return (String)internalMap.get("FIELD8");
    }

    public void putStandardizedFirstName(Object standardizedFirstName)
    {
        internalMap.put("FIELD8", standardizedFirstName);
    }

    public String getPartyId()
    {
        return (String)internalMap.get("FIELD10");
    }

    public void putPartyId(Object partyId)
    {
        internalMap.put("FIELD10", partyId);
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

    @Override
    public boolean equals(Object objectToCompare)
    {
        if(objectToCompare == this) return true;
        if(objectToCompare == null || !(objectToCompare instanceof IndexData)) return false;

        IndexData indexDataToCompare = (IndexData) objectToCompare;
        for(Entry<String, Object> entry : internalMap.entrySet())
        {
            String key = entry.getKey();
            if(key.equals("FIELD10")) continue;
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
            if(entry.getKey().equals("FIELD10")) continue;
            Object value = entry.getValue();
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
