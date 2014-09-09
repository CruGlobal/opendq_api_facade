package org.cru.service;

import org.cru.model.Person;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by William.Randall on 7/24/2014.
 */
@Test
public class PersonDeserializationServiceTest
{
    private PersonDeserializationService personDeserializationService;
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeClass
    public void setup()
    {
        personDeserializationService = new PersonDeserializationService();
    }

    @Test
    public void testDeserialization()
    {
        Person deserializedPerson = personDeserializationService.deserializePerson(getJson());

        assertNotNull(deserializedPerson);
        assertEquals(deserializedPerson.getId(), "k3rfjs3-f8g9-hfi8-5521-12a6er5423");

        assertEquals(deserializedPerson.getEmailAddresses().size(), 1);
        assertEquals(deserializedPerson.getEmailAddresses().get(0).getId(), "455322");
        assertEquals(deserializedPerson.getEmailAddresses().get(0).getEmail(), "wee@wee.net");

        assertEquals(deserializedPerson.getFirstName(), "Boo");
        assertEquals(deserializedPerson.getLastName(), "Vom");
        assertEquals(deserializedPerson.getMiddleName(), "Slam");
        assertEquals(deserializedPerson.getTitle(), "Mr.");
        assertEquals(deserializedPerson.getSuffix(), "II");

        assertEquals(deserializedPerson.getGender(), "Male");

        assertEquals(deserializedPerson.getPhoneNumbers().size(), 1);
        assertEquals(deserializedPerson.getPhoneNumbers().get(0).getId(), "885462");
        assertEquals(deserializedPerson.getPhoneNumbers().get(0).getNumber(), "5555555555");
        assertEquals(deserializedPerson.getPhoneNumbers().get(0).getLocation(), "mobile");

        assertEquals(deserializedPerson.getClientIntegrationId(), "102546");

        assertEquals(deserializedPerson.getAddresses().size(), 1);
        assertEquals(deserializedPerson.getAddresses().get(0).getId(), "4459874");
        assertEquals(deserializedPerson.getAddresses().get(0).getAddressLine1(), "Line 1");
        assertEquals(deserializedPerson.getAddresses().get(0).getAddressLine2(), "Line 2");
        assertEquals(deserializedPerson.getAddresses().get(0).getCity(), "Orlando");
        assertEquals(deserializedPerson.getAddresses().get(0).getZipCode(), "32832");
        assertEquals(deserializedPerson.getAddresses().get(0).getCountry(), "USA");

        assertNotNull(deserializedPerson.getAuthentication());
        assertNotNull(deserializedPerson.getAuthentication().getRelayGuidList());
        assertEquals(deserializedPerson.getAuthentication().getRelayGuidList().get(0), "f435f4-5f5e-8934-fjda-jk2354oia");
        assertNotNull(deserializedPerson.getAuthentication().getFacebookUidList());
        assertEquals(deserializedPerson.getAuthentication().getFacebookUidList().get(0), "500015648");

        assertEquals(deserializedPerson.getLinkedIdentities().size(), 1);
        assertEquals(deserializedPerson.getLinkedIdentities().get(0).getClientIntegrationId(), "1-FG32A");
        assertEquals(deserializedPerson.getLinkedIdentities().get(0).getEmployeeNumber(), "012345678");

        assertEquals(deserializedPerson.getClientUpdatedAt(), dateTimeFormatter.parseDateTime("2014-06-21 13:41:21"));

        assertEquals(deserializedPerson.getSource().getSystemId(), "8d72ac1e-5b0d-4b51-8768-12aa14dc64e8");
        assertEquals(deserializedPerson.getSource().getClientIntegrationId(), "102546");
    }

    private String getJson()
    {
        return "{" +
            "    \"person\": {" +
            "        \"id\": \"k3rfjs3-f8g9-hfi8-5521-12a6er5423\"," +
            "        \"email_address\": {" +
            "            \"id\": \"455322\"," +
            "            \"email\": \"wee@wee.net\"" +
            "        }," +
            "        \"last_name\": \"Vom\"," +
            "        \"first_name\": \"Boo\"," +
            "        \"middle_name\": \"Slam\"," +
            "        \"preferred_name\": \"Pogs\"," +
            "        \"title\": \"Mr.\"," +
            "        \"suffix\": \"II\"," +
            "        \"gender\": \"Male\"," +
            "        \"phone_number\": {" +
            "            \"id\": \"885462\"," +
            "            \"number\": \"5555555555\"," +
            "            \"location\": \"mobile\"" +
            "        }," +
            "        \"client_integration_id\": \"102546\"," +
            "        \"address\": {" +
            "            \"id\": \"4459874\"," +
            "            \"address_1\": \"Line 1\"," +
            "            \"address_2\": \"Line 2\"," +
            "            \"city\": \"Orlando\"," +
            "            \"state\": \"FL\"," +
            "            \"zip_code\": \"32832\"," +
            "            \"country\": \"USA\"" +
            "        }," +
            "        \"authentication\": {" +
            "            \"relay_guid\": \"f435f4-5f5e-8934-fjda-jk2354oia\"," +
            "            \"facebook_uid\": 500015648," +
            "            \"google_apps_uid\": [\"f46cf4-5d3e-8934-fjda-j43354fsa3\"]" +
            "        }," +
            "        \"linked_identities\": {" +
            "            \"client_integration_id\": \"1-FG32A\"," +
            "            \"employee_number\": \"012345678\"" +
            "        }," +
            "        \"client_updated_at\": \"2014-06-21 13:41:21\"," +
            "        \"source\" : {" +
            "            \"system_id\": \"8d72ac1e-5b0d-4b51-8768-12aa14dc64e8\"," +
            "            \"client_integration_id\": \"102546\"" +
            "        }" +
            "    }" +
            "}";
    }
}
