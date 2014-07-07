package org.cru.deserialization;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.model.PersonName;
import org.cru.model.PhoneNumber;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.atlassian.hamcrest.DeepIsEqual.deeplyEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests the {@link PersonDeserializer} class
 *
 * Created by William.Randall on 6/23/14.
 */
@Test
public class PersonDeserializerTest
{
    private PersonDeserializer personDeserializer;
    private DeserializationContext context;
    private JsonFactory factory = new JsonFactory();

    private String integrationId = "123456";
    private String globalRegistryId = "be20d878-dd9e-11e3-9615-12768b82bfd3";
    private String relayGuid = "be20d878-dd9e-11e3-9615-12768b82bfd5";
    private String contactId = "1-31dav";
    private String employeeNumber = "111111111";
    private String testDateTime = "2014-06-21 13:41:21";

    private Address testAddress1 = createTestAddress1();
    private Address testAddress2 = createTestAddress2();
    private Address minimalAddress = createMinimalAddress();
    private PersonName testPersonName = createTestPersonName();
    private PersonName minimalPersonName = createMinimalPersonName();

    @BeforeMethod
    public void setup() throws Exception
    {
        personDeserializer = new PersonDeserializer();
        context = mock(DeserializationContext.class);
    }

    @Test
    public void testDeserializationOneAddressEmailPhone() throws Exception
    {
        JsonParser jsonParser = factory.createJsonParser(createJsonWithOneAddressEmailAndPhone());
        ObjectCodec codec = new ObjectMapper(factory);
        jsonParser.setCodec(codec);
        Person testPerson = personDeserializer.deserialize(jsonParser, context);

        commonPersonAssertions(testPerson);
        emailAndPhoneAssertions(testPerson);
    }

    @Test
    public void testDeserializationMultipleEmails() throws Exception
    {
        JsonParser jsonParser = factory.createJsonParser(createJsonWithMultipleEmails());
        ObjectCodec codec = new ObjectMapper(factory);
        jsonParser.setCodec(codec);
        Person testPerson = personDeserializer.deserialize(jsonParser, context);

        commonPersonAssertions(testPerson);
        emailAndPhoneAssertions(testPerson);

        assertTrue(testPerson.getEmailAddresses().size() > 1);
        assertEquals(testPerson.getEmailAddresses().get(1).getId(), integrationId);
        assertEquals(testPerson.getEmailAddresses().get(1).getEmail(), "wee@wee.org");
    }

    @Test
    public void testDeserializationMultipleAddresses() throws Exception
    {
        JsonParser jsonParser = factory.createJsonParser(createJsonWithMultipleAddresses());
        ObjectCodec codec = new ObjectMapper(factory);
        jsonParser.setCodec(codec);
        Person testPerson = personDeserializer.deserialize(jsonParser, context);

        commonPersonAssertions(testPerson);
        emailAndPhoneAssertions(testPerson);

        assertTrue(testPerson.getAddresses().size() > 1);
        assertThat(testPerson.getAddresses().get(1), deeplyEqualTo(testAddress2));
    }

    @Test
    public void testDeserializationMultiplePhones() throws Exception
    {
        JsonParser jsonParser = factory.createJsonParser(createJsonWithMultiplePhones());
        ObjectCodec codec = new ObjectMapper(factory);
        jsonParser.setCodec(codec);
        Person testPerson = personDeserializer.deserialize(jsonParser, context);

        commonPersonAssertions(testPerson);
        emailAndPhoneAssertions(testPerson);

        assertTrue(testPerson.getPhoneNumbers().size() > 1);
        PhoneNumber phoneNumber = testPerson.getPhoneNumbers().get(1);
        assertEquals(phoneNumber.getId(), integrationId);
        assertEquals(phoneNumber.getLocation(), "home");
        assertEquals(phoneNumber.getNumber(), "5555555554");
    }

    @Test
    public void testDeserializationMinimalData() throws Exception
    {
        JsonParser jsonParser = factory.createJsonParser(createJsonWithMinimalData());
        ObjectCodec codec = new ObjectMapper(factory);
        jsonParser.setCodec(codec);
        Person testPerson = personDeserializer.deserialize(jsonParser, context);

        commonPersonAssertions(testPerson, minimalPersonName, minimalAddress);
    }

    private void commonPersonAssertions(Person testPerson)
    {
        commonPersonAssertions(testPerson, testPersonName, testAddress1);
    }

    private void commonPersonAssertions(Person testPerson, PersonName personName, Address testAddress)
    {
        assertEquals(testPerson.getGlobalRegistryId(), globalRegistryId);
        assertEquals(testPerson.getClientIntegrationId(), integrationId);
        assertThat(testPerson.getName(), deeplyEqualTo(personName));
        assertEquals(testPerson.getGender(), "Male");
        assertEquals(testPerson.getAuthentication().getRelayGuid(), relayGuid);
        assertEquals(testPerson.getLinkedIdentities().getSiebelContactId(), contactId);
        assertEquals(testPerson.getLinkedIdentities().getEmployeeNumber(), employeeNumber);

        assertEquals(testPerson.getClientUpdatedAt().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), testDateTime);

        //Address
        assertFalse(testPerson.getAddresses().isEmpty());
        Address address = testPerson.getAddresses().get(0);
        assertThat(address, deeplyEqualTo(testAddress));
    }

    private void emailAndPhoneAssertions(Person testPerson)
    {
        //Email
        assertFalse(testPerson.getEmailAddresses().isEmpty());
        assertEquals(testPerson.getEmailAddresses().get(0).getId(), integrationId);
        assertEquals(testPerson.getEmailAddresses().get(0).getEmail(), "blah@blah.com");

        //Phone number
        assertFalse(testPerson.getPhoneNumbers().isEmpty());
        PhoneNumber phoneNumber = testPerson.getPhoneNumbers().get(0);
        assertEquals(phoneNumber.getId(), integrationId);
        assertEquals(phoneNumber.getNumber(), "5555555555");
        assertEquals(phoneNumber.getLocation(), "mobile");
    }

    private String createJsonWithOneAddressEmailAndPhone()
    {
        return
            "{" +
                "\"person\": {" +
                    "\"id\": \"" + globalRegistryId + "\"," +
                    "\"email_address\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"email\": \"blah@blah.com\"" +
                    "}," +
                    "\"last_name\": \"" + testPersonName.getLastName() + "\"," +
                    "\"first_name\": \"" + testPersonName.getFirstName() + "\"," +
                    "\"middle_name\": \"" + testPersonName.getMiddleName() + "\"," +
                    "\"preferred_name\": \"" + testPersonName.getPreferredName() + "\"," +
                    "\"title\": \"" + testPersonName.getTitle() + "\"," +
                    "\"suffix\": \"" + testPersonName.getSuffix() + "\"," +
                    "\"gender\": \"Male\"," +
                    "\"phone_number\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"number\": \"5555555555\"," +
                        "\"location\": \"mobile\"" +
                    "}," +
                    "\"client_integration_id\": \"" + integrationId + "\"," +
                    "\"address\": {" +
                        "\"id\": \"" + testAddress1.getId() + "\"," +
                        "\"address_1\": \"" + testAddress1.getAddressLine1() + "\"," +
                        "\"address_2\": \"" + testAddress1.getAddressLine2() + "\"," +
                        "\"address_3\": \"" + testAddress1.getAddressLine3() + "\"," +
                        "\"address_4\": \"" + testAddress1.getAddressLine4() + "\"," +
                        "\"city\": \"" + testAddress1.getCity() + "\"," +
                        "\"state\": \"" + testAddress1.getState() + "\"," +
                        "\"zip_code\": \"" + testAddress1.getZipCode() + "\"," +
                        "\"country\": \"" + testAddress1.getCountry() + "\"" +
                    "}," +
                    "\"authentication\": {" +
                        "\"relay_guid\": \"" + relayGuid + "\"" +
                    "}," +
                    "\"linked_identities\": {" +
                        "\"siebel_contact_id\": \"" + contactId + "\"," +
                        "\"employee_number\": \"" + employeeNumber + "\"" +
                    "}," +
                    "\"client_updated_at\": \"" + testDateTime + "\"" +
                "}" +
            "}";
    }

    private String createJsonWithMultipleAddresses()
    {
        return
            "{" +
                "\"person\": {" +
                    "\"id\": \"" + globalRegistryId + "\"," +
                    "\"email_address\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"email\": \"blah@blah.com\"" +
                    "}," +
                    "\"last_name\": \"" + testPersonName.getLastName() + "\"," +
                    "\"first_name\": \"" + testPersonName.getFirstName() + "\"," +
                    "\"middle_name\": \"" + testPersonName.getMiddleName() + "\"," +
                    "\"preferred_name\": \"" + testPersonName.getPreferredName() + "\"," +
                    "\"title\": \"" + testPersonName.getTitle() + "\"," +
                    "\"suffix\": \"" + testPersonName.getSuffix() + "\"," +
                    "\"gender\": \"Male\"," +
                    "\"phone_number\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"number\": \"5555555555\"," +
                        "\"location\": \"mobile\"" +
                    "}," +
                    "\"client_integration_id\": \"" + integrationId + "\"," +
                    "\"address\": [" +
                        "{" +
                            "\"id\": \"" + testAddress1.getId() + "\"," +
                            "\"address_1\": \"" + testAddress1.getAddressLine1() + "\"," +
                            "\"address_2\": \"" + testAddress1.getAddressLine2() + "\"," +
                            "\"address_3\": \"" + testAddress1.getAddressLine3() + "\"," +
                            "\"address_4\": \"" + testAddress1.getAddressLine4() + "\"," +
                            "\"city\": \"" + testAddress1.getCity() + "\"," +
                            "\"state\": \"" + testAddress1.getState() + "\"," +
                            "\"zip_code\": \"" + testAddress1.getZipCode() + "\"," +
                            "\"country\": \"" + testAddress1.getCountry() + "\"" +
                        "}," +
                        "{" +
                            "\"id\": \"" + testAddress2.getId() + "\"," +
                            "\"address_1\": \"" + testAddress2.getAddressLine1() + "\"," +
                            "\"address_2\": \"" + testAddress2.getAddressLine2() + "\"," +
                            "\"address_3\": \"" + testAddress2.getAddressLine3() + "\"," +
                            "\"address_4\": \"" + testAddress2.getAddressLine4() + "\"," +
                            "\"city\": \"" + testAddress2.getCity() + "\"," +
                            "\"state\": \"" + testAddress2.getState() + "\"," +
                            "\"zip_code\": \"" + testAddress2.getZipCode() + "\"," +
                            "\"country\": \"" + testAddress2.getCountry() + "\"" +
                        "}" +
                    "]," +
                    "\"authentication\": {" +
                        "\"relay_guid\": \"" + relayGuid + "\"" +
                    "}," +
                    "\"linked_identities\": {" +
                        "\"siebel_contact_id\": \"" + contactId + "\"," +
                        "\"employee_number\": \"" + employeeNumber + "\"" +
                    "}," +
                    "\"client_updated_at\": \"" + testDateTime + "\"" +
                "}" +
            "}";
    }

    private String createJsonWithMultipleEmails()
    {
        return
            "{" +
                "\"person\": {" +
                    "\"id\": \"" + globalRegistryId + "\"," +
                    "\"email_address\": [" +
                        "{" +
                            "\"id\": \"" + integrationId + "\"," +
                            "\"email\": \"blah@blah.com\"" +
                        "}," +
                        "{" +
                            "\"id\": \"" + integrationId + "\"," +
                            "\"email\": \"wee@wee.org\"" +
                        "}" +
                    "]," +
                    "\"last_name\": \"" + testPersonName.getLastName() + "\"," +
                    "\"first_name\": \"" + testPersonName.getFirstName() + "\"," +
                    "\"middle_name\": \"" + testPersonName.getMiddleName() + "\"," +
                    "\"preferred_name\": \"" + testPersonName.getPreferredName() + "\"," +
                    "\"title\": \"" + testPersonName.getTitle() + "\"," +
                    "\"suffix\": \"" + testPersonName.getSuffix() + "\"," +
                    "\"gender\": \"Male\"," +
                    "\"phone_number\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"number\": \"5555555555\"," +
                        "\"location\": \"mobile\"" +
                    "}," +
                    "\"client_integration_id\": \"" + integrationId + "\"," +
                    "\"address\": {" +
                        "\"id\": \"" + testAddress1.getId() + "\"," +
                        "\"address_1\": \"" + testAddress1.getAddressLine1() + "\"," +
                        "\"address_2\": \"" + testAddress1.getAddressLine2() + "\"," +
                        "\"address_3\": \"" + testAddress1.getAddressLine3() + "\"," +
                        "\"address_4\": \"" + testAddress1.getAddressLine4() + "\"," +
                        "\"city\": \"" + testAddress1.getCity() + "\"," +
                        "\"state\": \"" + testAddress1.getState() + "\"," +
                        "\"zip_code\": \"" + testAddress1.getZipCode() + "\"," +
                        "\"country\": \"" + testAddress1.getCountry() + "\"" +
                    "}," +
                    "\"authentication\": {" +
                        "\"relay_guid\": \"" + relayGuid + "\"" +
                    "}," +
                    "\"linked_identities\": {" +
                        "\"siebel_contact_id\": \"" + contactId + "\"," +
                        "\"employee_number\": \"" + employeeNumber + "\"" +
                    "}," +
                    "\"client_updated_at\": \"" + testDateTime + "\"" +
                "}" +
            "}";
    }

    private String createJsonWithMultiplePhones()
    {
        return
            "{" +
                "\"person\": {" +
                    "\"id\": \"" + globalRegistryId + "\"," +
                    "\"email_address\": {" +
                        "\"id\": \"" + integrationId + "\"," +
                        "\"email\": \"blah@blah.com\"" +
                    "}," +
                    "\"last_name\": \"" + testPersonName.getLastName() + "\"," +
                    "\"first_name\": \"" + testPersonName.getFirstName() + "\"," +
                    "\"middle_name\": \"" + testPersonName.getMiddleName() + "\"," +
                    "\"preferred_name\": \"" + testPersonName.getPreferredName() + "\"," +
                    "\"title\": \"" + testPersonName.getTitle() + "\"," +
                    "\"suffix\": \"" + testPersonName.getSuffix() + "\"," +
                    "\"gender\": \"Male\"," +
                    "\"phone_number\": [" +
                        "{" +
                            "\"id\": \"" + integrationId + "\"," +
                            "\"number\": \"5555555555\"," +
                            "\"location\": \"mobile\"" +
                        "}," +
                        "{" +
                            "\"id\": \"" + integrationId + "\"," +
                            "\"number\": \"5555555554\"," +
                            "\"location\": \"home\"" +
                        "}" +
                    "]," +
                    "\"client_integration_id\": \"" + integrationId + "\"," +
                    "\"address\": {" +
                        "\"id\": \"" + testAddress1.getId() + "\"," +
                        "\"address_1\": \"" + testAddress1.getAddressLine1() + "\"," +
                        "\"address_2\": \"" + testAddress1.getAddressLine2() + "\"," +
                        "\"address_3\": \"" + testAddress1.getAddressLine3() + "\"," +
                        "\"address_4\": \"" + testAddress1.getAddressLine4() + "\"," +
                        "\"city\": \"" + testAddress1.getCity() + "\"," +
                        "\"state\": \"" + testAddress1.getState() + "\"," +
                        "\"zip_code\": \"" + testAddress1.getZipCode() + "\"," +
                        "\"country\": \"" + testAddress1.getCountry() + "\"" +
                    "}," +
                    "\"authentication\": {" +
                        "\"relay_guid\": \"" + relayGuid + "\"" +
                    "}," +
                    "\"linked_identities\": {" +
                        "\"siebel_contact_id\": \"" + contactId + "\"," +
                        "\"employee_number\": \"" + employeeNumber + "\"" +
                    "}," +
                    "\"client_updated_at\": \"" + testDateTime + "\"" +
                "}" +
            "}";
    }

    private String createJsonWithMinimalData()
    {
        return
            "{" +
                "\"person\": {" +
                    "\"id\": \"" + globalRegistryId + "\"," +
                    "\"last_name\": \"" + testPersonName.getLastName() + "\"," +
                    "\"first_name\": \"" + testPersonName.getFirstName() + "\"," +
                    "\"title\": \"" + testPersonName.getTitle() + "\"," +
                    "\"gender\": \"Male\"," +
                    "\"client_integration_id\": \"" + integrationId + "\"," +
                    "\"address\": {" +
                        "\"id\": \"" + minimalAddress.getId() + "\"," +
                        "\"address_1\": \"" + minimalAddress.getAddressLine1() + "\"," +
                        "\"city\": \"" + minimalAddress.getCity() + "\"," +
                        "\"state\": \"" + minimalAddress.getState() + "\"," +
                        "\"zip_code\": \"" + minimalAddress.getZipCode() + "\"," +
                        "\"country\": \"" + minimalAddress.getCountry() + "\"" +
                    "}," +
                    "\"authentication\": {" +
                        "\"relay_guid\": \"" + relayGuid + "\"" +
                    "}," +
                    "\"linked_identities\": {" +
                        "\"siebel_contact_id\": \"" + contactId + "\"," +
                        "\"employee_number\": \"" + employeeNumber + "\"" +
                    "}," +
                    "\"client_updated_at\": \"" + testDateTime + "\"" +
                "}" +
            "}";
    }

    private Address createTestAddress1()
    {
        Address testAddress1 = new Address();
        testAddress1.setId(integrationId);
        testAddress1.setAddressLine1("Address Line 1");
        testAddress1.setAddressLine2("Address Line 2");
        testAddress1.setAddressLine3("Address Line 3");
        testAddress1.setAddressLine4("Address Line 4");
        testAddress1.setCity("City");
        testAddress1.setState("FL");
        testAddress1.setZipCode("12345");
        testAddress1.setCountry("USA");

        return testAddress1;
    }

    private Address createTestAddress2()
    {
        Address testAddress1 = new Address();
        testAddress1.setId(integrationId);
        testAddress1.setAddressLine1("Line 1");
        testAddress1.setAddressLine2("Line 2");
        testAddress1.setAddressLine3("Line 3");
        testAddress1.setAddressLine4("Line 4");
        testAddress1.setCity("City2");
        testAddress1.setState("IN");
        testAddress1.setZipCode("54321");
        testAddress1.setCountry("USA");

        return testAddress1;
    }

    private Address createMinimalAddress()
    {
        Address testAddress1 = new Address();
        testAddress1.setId(integrationId);
        testAddress1.setAddressLine1("Add 1");
        testAddress1.setAddressLine2("");
        testAddress1.setAddressLine3("");
        testAddress1.setAddressLine4("");
        testAddress1.setCity("City3");
        testAddress1.setState("AK");
        testAddress1.setZipCode("22222");
        testAddress1.setCountry("USA");

        return testAddress1;
    }

    private PersonName createTestPersonName()
    {
        PersonName testPersonName = createMinimalPersonName();
        testPersonName.setMiddleName("TestMiddleName");
        testPersonName.setPreferredName("TestPreferredName");
        testPersonName.setSuffix("Jr.");

        return testPersonName;
    }

    private PersonName createMinimalPersonName()
    {
        PersonName testPersonName = new PersonName();
        testPersonName.setFirstName("TestFirstName");
        testPersonName.setLastName("TestLastName");
        testPersonName.setTitle("Mr.");
        testPersonName.setMiddleName("");
        testPersonName.setPreferredName("");
        testPersonName.setSuffix("");

        return testPersonName;
    }
}
