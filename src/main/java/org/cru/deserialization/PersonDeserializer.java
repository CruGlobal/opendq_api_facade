package org.cru.deserialization;

import com.google.common.base.Strings;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.cru.model.Address;
import org.cru.model.Authentication;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentities;
import org.cru.model.Person;
import org.cru.model.PersonName;
import org.cru.model.PhoneNumber;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializer for Global Registry data, to put it into a {@link Person} object.
 * The data that comes in looks different enough from the model structure to need
 * a custom deserializer.
 *
 * Created by William.Randall on 6/23/14.
 */
public class PersonDeserializer extends JsonDeserializer<Person>
{
    public Person deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException
    {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        JsonNode data = root.path("person");
        Person person = new Person();

        person.setGlobalRegistryId(data.path("id").asText());
        person.setName(deserializeName(data));
        person.setGender(data.path("gender").asText());
        person.setAccountNumber(data.path("account_number").asText());
        person.setClientIntegrationId(data.path("client_integration_id").asText());

        person.setAddresses(handleOneOrMoreAddresses(data.path("address")));
        person.setEmailAddresses(handleOneOrMoreEmailAddresses(data.path("email_address")));
        person.setPhoneNumbers(handleOneOrMorePhoneNumbers(data.path("phone_number")));
        person.setAuthentication(deserializeAuthentication(data.path("authentication")));
        person.setLinkedIdentities(deserializeLinkedIdentities(data.path("linked_identities")));
        person.setClientUpdatedAt(deserializeDateTime(data.path("client_updated_at")));

        return person;
    }

    private List<Address> handleOneOrMoreAddresses(JsonNode addressData)
    {
        List<Address> addresses = new ArrayList<Address>();

        if(addressData.isArray())
        {
            for (JsonNode addressNode : addressData)
            {
                addresses.add(deserializeAddress(addressNode));
            }
        }
        else
        {
            addresses.add(deserializeAddress(addressData));
        }

        return addresses;
    }

    private List<EmailAddress> handleOneOrMoreEmailAddresses(JsonNode emailAddressData)
    {
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();

        if(emailAddressData.isArray())
        {
            for(JsonNode emailAddressNode : emailAddressData)
            {
                emailAddresses.add(deserializeEmailAddress(emailAddressNode));
            }
        }
        else
        {
            emailAddresses.add(deserializeEmailAddress(emailAddressData));
        }

        return emailAddresses;
    }


    private List<PhoneNumber> handleOneOrMorePhoneNumbers(JsonNode phoneNumberData)
    {
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

        if(phoneNumberData.isArray())
        {
            for(JsonNode phoneNumberNode : phoneNumberData)
            {
                phoneNumbers.add(deserializePhoneNumber(phoneNumberNode));
            }
        }
        else
        {
            phoneNumbers.add(deserializePhoneNumber(phoneNumberData));
        }

        return phoneNumbers;
    }

    private PersonName deserializeName(JsonNode data)
    {
        PersonName personName = new PersonName();
        personName.setTitle(data.path("title").asText());
        personName.setFirstName(data.path("first_name").asText());
        personName.setMiddleName(data.path("middle_name").asText());
        personName.setLastName(data.path("last_name").asText());
        personName.setPreferredName(data.path("preferred_name").asText());
        personName.setSuffix(data.path("suffix").asText());

        return personName;
    }

    private Address deserializeAddress(JsonNode addressNode)
    {
        Address address = new Address();
        address.setId(addressNode.path("id").asText());
        address.setAddressLine1(addressNode.path("address_1").asText());
        address.setAddressLine2(addressNode.path("address_2").asText());
        address.setAddressLine3(addressNode.path("address_3").asText());
        address.setAddressLine4(addressNode.path("address_4").asText());
        address.setCity(addressNode.path("city").asText());
        address.setState(addressNode.path("state").asText());
        address.setZipCode(addressNode.path("zip_code").asText());
        address.setCountry(addressNode.path("country").asText());

        return address;
    }

    private EmailAddress deserializeEmailAddress(JsonNode emailAddressNode)
    {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setId(emailAddressNode.path("id").asText());
        emailAddress.setEmail(emailAddressNode.path("email").asText());

        return emailAddress;
    }

    private PhoneNumber deserializePhoneNumber(JsonNode phoneNumberNode)
    {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(phoneNumberNode.path("id").asText());
        phoneNumber.setLocation(phoneNumberNode.path("location").asText());
        phoneNumber.setNumber(phoneNumberNode.path("number").asText());

        return phoneNumber;
    }

    private Authentication deserializeAuthentication(JsonNode authenticationNode)
    {
        Authentication authentication = new Authentication();
        authentication.setRelayGuid(authenticationNode.path("relay_guid").asText());

        return authentication;
    }

    private LinkedIdentities deserializeLinkedIdentities(JsonNode linkedIdentitiesNode)
    {
        LinkedIdentities linkedIdentities = new LinkedIdentities();
        linkedIdentities.setEmployeeNumber(linkedIdentitiesNode.path("employee_number").asText());
        linkedIdentities.setSiebelContactId(linkedIdentitiesNode.path("siebel_contact_id").asText());

        return linkedIdentities;
    }

    private DateTime deserializeDateTime(JsonNode dateTime)
    {
        String dateTimeString = dateTime.asText();
        if(Strings.isNullOrEmpty(dateTimeString)) return null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.parseDateTime(dateTimeString);
    }
}
