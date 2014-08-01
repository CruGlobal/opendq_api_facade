package org.cru.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.cru.data.TestPeople;
import org.cru.model.Person;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by William.Randall on 8/1/2014.
 */
@Test
public class PersonToJson
{
    @DataProvider
    private Object[][] getPeople()
    {
        return new Object[][] {
            { TestPeople.createPersonForUpdate() },
            { TestPeople.createPersonForAdd() },
            { TestPeople.createPersonFromSoapUITestData() },
            { TestPeople.generatePersonWithLotsOfData() },
            { TestPeople.createTestPersonGotMarried() }
        };
    }

    @Test(dataProvider = "getPeople")
    public void printJson(Person person) throws Exception
    {
        System.out.println("{\"person\":" + personToJson(person) + "}");
    }

    private String personToJson(Person person) throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper.writeValueAsString(person);
    }
}
