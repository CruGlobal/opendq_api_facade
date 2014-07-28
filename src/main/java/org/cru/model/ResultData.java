package org.cru.model;

import com.sun.istack.internal.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Convenience class to reduce "magic strings" throughout the code when
 * referencing the values herein.
 *
 * Created by William.Randall on 7/28/2014.
 */
public class ResultData implements Map<String, Object>
{
    private Map<String, Object> internalMap;

    public ResultData()
    {
        internalMap = new HashMap<String, Object>();
    }

    public String getFirstName()
    {
        return (String)internalMap.get("firstName");
    }

    public void putFirstName(Object firstName)
    {
        internalMap.put("firstName", firstName);
    }

    public String getLastName()
    {
        return (String)internalMap.get("lastName");
    }

    public void putLastName(Object lastName)
    {
        internalMap.put("lastName", lastName);
    }

    public String getAddressLine1()
    {
        return (String)internalMap.get("address1");
    }

    public void putAddressLine1(Object addressLine1)
    {
        internalMap.put("address1", addressLine1);
    }

    public String getAddressLine2()
    {
        return (String)internalMap.get("address2");
    }

    public void putAddressLine2(Object addressLine2)
    {
        internalMap.put("address2", addressLine2);
    }

    public String getCity()
    {
        return (String)internalMap.get("city");
    }

    public void putCity(Object city)
    {
        internalMap.put("city", city);
    }

    public String getState()
    {
        return (String)internalMap.get("state");
    }

    public void putState(Object state)
    {
        internalMap.put("state", state);
    }

    public String getZip()
    {
        return (String)internalMap.get("zip");
    }

    public void putZip(Object zip)
    {
        internalMap.put("zip", zip);
    }

    public String getStandardizedFirstName()
    {
        return (String)internalMap.get("standardizedFirstName");
    }

    public void putStandardizedFirstName(Object standardizedFirstName)
    {
        internalMap.put("standardizedFirstName", standardizedFirstName);
    }

    public String getPartyId()
    {
        return (String)internalMap.get("partyId");
    }

    public void putPartyId(Object partyId)
    {
        internalMap.put("partyId", partyId);
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
    public Object get(Object key)
    {
        return internalMap.get(key);
    }

    @Override
    public Object put(String key, Object value)
    {
        return internalMap.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        return internalMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m)
    {
        internalMap.putAll(m);
    }

    @Override
    public void clear()
    {
        internalMap.clear();
    }

    @Override
    @NotNull
    public Set<String> keySet()
    {
        return internalMap.keySet();
    }

    @Override
    @NotNull
    public Collection<Object> values()
    {
        return internalMap.values();
    }

    @Override
    @NotNull
    public Set<Entry<String, Object>> entrySet()
    {
        return internalMap.entrySet();
    }
}
