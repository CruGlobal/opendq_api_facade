package org.cru.model.map;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by William.Randall on 8/15/2014.
 */
public class NameAndCommunicationIndexData extends IndexData
{
    private static final String COMMUNICATION_DATA_KEY = "FIELD3";  // Can be either Phone or Email

    public NameAndCommunicationIndexData()
    {
        FIRST_NAME_KEY = "FIELD1";
        LAST_NAME_KEY = "FIELD2";
        PARTY_ID_KEY = "FIELD4";
        GR_ID_KEY = "FIELD5";
    }

    public String getCommunicationData()
    {
        return (String)internalMap.get(COMMUNICATION_DATA_KEY);
    }

    public void putCommunicationData(Object communicationData)
    {
        internalMap.put(COMMUNICATION_DATA_KEY, communicationData);
    }

    public List<String> getValuesForEquality()
    {
        List<String> valuesForEquality = Lists.newArrayList();

        valuesForEquality.add(getFirstName());
        valuesForEquality.add(getLastName());
        valuesForEquality.add(getStandardizedFirstName());
        valuesForEquality.add(getCommunicationData());

        return valuesForEquality;
    }
}
