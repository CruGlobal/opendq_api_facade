package org.cru.model.map;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by William.Randall on 8/15/2014.
 */
public class NameAndEmailIndexData extends IndexData
{
    private static final String EMAIL_ADDRESS_KEY = "FIELD3";

    public NameAndEmailIndexData()
    {
        FIRST_NAME_KEY = "FIELD1";
        LAST_NAME_KEY = "FIELD2";
        STANDARDIZED_FIRST_NAME_KEY = "FIELD4";
        PARTY_ID_KEY = "FIELD5";
        GR_ID_KEY = "FIELD6";
    }

    public String getEmailAddress()
    {
        return (String)internalMap.get(EMAIL_ADDRESS_KEY);
    }

    public void putEmailAddress(Object emailAddress)
    {
        internalMap.put(EMAIL_ADDRESS_KEY, emailAddress);
    }

    public List<String> getValuesForEquality()
    {
        List<String> valuesForEquality = Lists.newArrayList();

        valuesForEquality.add(getFirstName());
        valuesForEquality.add(getLastName());
        valuesForEquality.add(getStandardizedFirstName());
        valuesForEquality.add(getEmailAddress());

        return valuesForEquality;
    }
}
