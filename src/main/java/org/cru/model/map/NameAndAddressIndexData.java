package org.cru.model.map;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by William.Randall on 8/15/2014.
 */
public class NameAndAddressIndexData extends IndexData
{
    private static final String ADDRESS1_KEY = "FIELD3";
    private static final String ADDRESS2_KEY = "FIELD4";
    private static final String CITY_KEY = "FIELD5";
    private static final String STATE_KEY = "FIELD6";
    private static final String ZIP_CODE_KEY = "FIELD7";

    public NameAndAddressIndexData()
    {
        FIRST_NAME_KEY = "FIELD1";
        LAST_NAME_KEY = "FIELD2";
        STANDARDIZED_FIRST_NAME_KEY = "FIELD8";
        PARTY_ID_KEY = "FIELD10";
        GR_ID_KEY = "FIELD11";
    }

    public String getAddressLine1()
    {
        return (String)internalMap.get(ADDRESS1_KEY);
    }

    public void putAddressLine1(Object addressLine1)
    {
        internalMap.put(ADDRESS1_KEY, addressLine1);
    }

    public String getAddressLine2()
    {
        return (String)internalMap.get(ADDRESS2_KEY);
    }

    public void putAddressLine2(Object addressLine2)
    {
        if(addressLine2 == null) internalMap.put(ADDRESS2_KEY, "NULLDATA");
        else internalMap.put(ADDRESS2_KEY, addressLine2);
    }

    public String getCity()
    {
        return (String)internalMap.get(CITY_KEY);
    }

    public void putCity(Object city)
    {
        internalMap.put(CITY_KEY, city);
    }

    public String getState()
    {
        return (String)internalMap.get(STATE_KEY);
    }

    public void putState(Object state)
    {
        internalMap.put(STATE_KEY, state);
    }

    public String getZipCode()
    {
        return (String)internalMap.get(ZIP_CODE_KEY);
    }

    public void putZipCode(Object zipCode)
    {
        internalMap.put(ZIP_CODE_KEY, zipCode);
    }

    public List<String> getValuesForEquality()
    {
        List<String> valuesForEquality = Lists.newArrayList();

        valuesForEquality.add(getFirstName());
        valuesForEquality.add(getLastName());
        valuesForEquality.add(getStandardizedFirstName());
        valuesForEquality.add(getAddressLine1());
        valuesForEquality.add(getAddressLine2());
        valuesForEquality.add(getCity());
        valuesForEquality.add(getState());
        valuesForEquality.add(getZipCode());

        return valuesForEquality;
    }
}
