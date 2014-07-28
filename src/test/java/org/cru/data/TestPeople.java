package org.cru.data;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.mdm.MdmCodes;
import org.cru.mdm.MdmConstants;
import org.cru.model.Address;
import org.cru.model.Authentication;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentity;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.model.Source;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class to create {@link Person} objects that are shared among
 * the different tests.
 *
 * Created by William.Randall on 7/17/2014.
 */
public class TestPeople
{
    private static String opendqDatePattern = "MM/dd/YYYY";

    public static Person generatePersonWithLotsOfData()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setId("kses34223-dk43-9493-394nfa2348d1");
        testAddress.setAddressLine1("1125 Blvd Way");
        testAddress.setCity("Las Vegas");
        testAddress.setState("NV");
        testAddress.setZipCode("84253");
        testAddress.setCountry("USA");

        Address testAddress2 = new Address();
        testAddress2.setId("65a4sdf4-dk43-9493-394nfa2348d1");
        testAddress2.setAddressLine1("5499 Lake Dr");
        testAddress2.setCity("Juneau");
        testAddress2.setState("AK");
        testAddress2.setZipCode("99954");
        testAddress2.setCountry("USA");

        List<Address> addresses = Lists.newArrayList();
        addresses.add(testAddress);
        addresses.add(testAddress2);

        testPerson.setTitle("Ms.");
        testPerson.setFirstName("Nom");
        testPerson.setLastName("Nom");

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("nom.nom@crutest.org");
        emailAddress.setId("kses34223-dk43-9493-394nfa2348d2");
        List<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        emailAddresses.add(emailAddress);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("5555555553");
        phoneNumber.setLocation("work");
        phoneNumber.setId("kses34223-dk43-9493-394nfa2348d3");
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        phoneNumbers.add(phoneNumber);

        LinkedIdentity linkedIdentity = new LinkedIdentity();
        linkedIdentity.setClientIntegrationId("1-6T4D4");

        Authentication authentication = new Authentication();
        authentication.setRelayGuid("Re1ay-6u1d");

        testPerson.setId("3ikfj32-8rt4-9493-394nfa2348da");
        testPerson.setClientIntegrationId("1-6T4D4");
        testPerson.setLinkedIdentities(Lists.newArrayList(linkedIdentity));
        testPerson.setAuthentication(authentication);

        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("F");

        Source testSource = new Source();
        testSource.setClientIntegrationId("1-6T4D4");
        testSource.setSystemId("OAF");

        testPerson.setSource(testSource);

        return testPerson;
    }

    public static Person createPersonFromSoapUITestData()
    {
        Person testPerson = new Person();

        testPerson.setFirstName("Susan");
        testPerson.setLastName("Snowa");
        testPerson.setMdmPartyId("100");
        testPerson.setId("SUSAN"); //TODO: Replace with real global registry id when available

        Address testAddress = new Address();
        testAddress.setAddressLine1("2824 McManaway Dr");
        testAddress.setCity("Midlothian");
        testAddress.setState("VA");

        testPerson.setAddresses(Lists.newArrayList(testAddress));

        return testPerson;
    }

    public static Person createTestPersonGotMarried()
    {
        Person testPerson = generatePersonWithLotsOfData();

        Address marriedAddress = new Address();
        marriedAddress.setAddressLine1("11526 Way Blvd");
        testPerson.getAddresses().add(marriedAddress);
        testPerson.setTitle("Mrs.");
        testPerson.setLastName("Tom");

        return testPerson;
    }

    public static Person createTestPersonHasBeenDeleted()
    {
        Person deletedPerson = new Person();

        Address address = new Address();
        address.setAddressLine1("4 Quarter Ln");
        address.setCity("Austin");
        address.setState("TX");

        deletedPerson.setFirstName("Pandemic");
        deletedPerson.setLastName("Handy");

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        deletedPerson.setAddresses(addresses);
        deletedPerson.setId("6");

        return deletedPerson;
    }


    public static RealTimeObjectActionDTO createMockMdmPerson()
    {
        String partyId = "1073";
        RealTimeObjectActionDTO mdmPerson = new RealTimeObjectActionDTO();
        String todayString = new LocalDate().toString(opendqDatePattern);

        ObjEntityDTO objEntity = new ObjEntityDTO();
        objEntity.setPartyId(partyId);
        objEntity.setSrcId("221568");
        objEntity.setActive("Y");


        ObjAddressDTO objAddress = new ObjAddressDTO();
        objAddress.setAddressId("786");
        objAddress.setCodId(MdmCodes.HOME_ADDRESS.getId());
        objAddress.setComExclusionType("N");
        
        objAddress.setAddressLine1("1125 Blvd Way");
        objAddress.setCityName("Las Vegas");
        objAddress.setStateName("NV");
        objAddress.setZip("84253");
        objAddress.setCryName("USA");

        objAddress.setFromDate(todayString);
        objAddress.setDateCreated(todayString);
        objAddress.setUserCreated(MdmConstants.USER);
        objAddress.setSource("OAF");
        objAddress.setAction("U");
        objAddress.setUserDef1("786841");

        ObjAddressDTOList objAddresses = new ObjAddressDTOList();
        List<ObjAddressDTO> addressInnerList = Lists.newArrayList();
        addressInnerList.add(objAddress);


        ObjCommunicationDTO emailCommunication = new ObjCommunicationDTO();
        emailCommunication.setComId("648");
        emailCommunication.setCodId(MdmCodes.PERSONAL_EMAIL.getId());
        emailCommunication.setPartyId(partyId);
        emailCommunication.setCommdata("nom.nom@crutest.org");
        emailCommunication.setComExclusionType("N");
        emailCommunication.setDateCreated(todayString);
        emailCommunication.setUserCreated(MdmConstants.USER);
        emailCommunication.setSource("OAF");
        emailCommunication.setAction("U");
        emailCommunication.setClientId(MdmConstants.CLIENT_ID);
        emailCommunication.setTypId(MdmConstants.TYP_ID);
        emailCommunication.setUserDef1("6487564");

        ObjCommunicationDTO phoneCommunication = new ObjCommunicationDTO();
        phoneCommunication.setComId("649");
        phoneCommunication.setCodId(MdmCodes.HOME_PHONE.getId());
        phoneCommunication.setPartyId(partyId);
        phoneCommunication.setCommdata("5555555553");
        phoneCommunication.setComExclusionType("N");
        phoneCommunication.setDateCreated(todayString);
        phoneCommunication.setUserCreated(MdmConstants.USER);
        phoneCommunication.setSource("OAF");
        phoneCommunication.setAction("U");
        phoneCommunication.setUserDef1("6495484");
        phoneCommunication.setUserDef2("home");
        phoneCommunication.setClientId(MdmConstants.CLIENT_ID);
        phoneCommunication.setTypId(MdmConstants.TYP_ID);

        ObjCommunicationDTOList objCommunications = new ObjCommunicationDTOList();
        List<ObjCommunicationDTO> communicationInnerList = Lists.newArrayList();
        communicationInnerList.add(emailCommunication);
        communicationInnerList.add(phoneCommunication);


        ObjAttributeDataDTO accountData = new ObjAttributeDataDTO();
        setCommonAttributeData(accountData);
        accountData.setObjAdId("1464");
        accountData.setField1("OAF");
        accountData.setField3("1-6T4D4");
        accountData.setMultDetTypeLev2("ACCOUNTDATA");

        ObjAttributeDataDTO personData = new ObjAttributeDataDTO();
        setCommonAttributeData(personData);
        personData.setObjAdId("1465");
        personData.setField1("Ms.");  //Title
        personData.setField2("Nom");  //First Name
        personData.setField4("Nom");  //Last Name
        personData.setField6("F");    //Gender
        personData.setMultDetTypeLev1("PERSON");

        ObjAttributeDataDTO householdData = new ObjAttributeDataDTO();
        setCommonAttributeData(householdData);
        householdData.setObjAdId("1466");
        householdData.setField1(partyId); //Object Id
        householdData.setField2("Nom"); //First Name
        householdData.setField3("Nom"); //Last Name
        householdData.setMultDetTypeLev2("HOUSEHOLD");

        ObjAttributeDataDTO identityData = new ObjAttributeDataDTO();
        setCommonAttributeData(identityData);
        identityData.setObjAdId("1467");
        identityData.setField1("OAF");
        identityData.setField2("1-6T4D4");
        identityData.setField3("OAF");
        identityData.setMultDetTypeLev2("IDENTITIES");

        ObjAttributeDataDTO sourceDetails = new ObjAttributeDataDTO();
        setCommonAttributeData(sourceDetails);
        sourceDetails.setField1("OAF");
        sourceDetails.setField2("OAF");
        sourceDetails.setMultDetTypeLev2("SOURCEDETAILS");

        ObjAttributeDataDTOList objAttributeDataDTOList = new ObjAttributeDataDTOList();
        List<ObjAttributeDataDTO> attributeDataList = objAttributeDataDTOList.getObjectAttributeData();
        attributeDataList.add(accountData);
        attributeDataList.add(personData);
        attributeDataList.add(householdData);
        attributeDataList.add(identityData);
        attributeDataList.add(sourceDetails);


        mdmPerson.setObjectAttributeDatas(objAttributeDataDTOList);
        mdmPerson.setObjectEntity(objEntity);
        mdmPerson.setObjectAddresses(objAddresses);
        mdmPerson.setObjectCommunications(objCommunications);

        return mdmPerson;
    }

    private static void setCommonAttributeData(ObjAttributeDataDTO attributeData)
    {
        String todayString = new LocalDate().toString(opendqDatePattern);
        attributeData.setFromDate(todayString);  // This is overwritten on insert
        attributeData.setTypId(MdmConstants.TYP_ID);
        attributeData.setDateCreated(todayString);
        attributeData.setUserCreated(MdmConstants.USER);
        attributeData.setSource("OAF");
        attributeData.setAction("U");
        attributeData.setMultDetTypeLev1("PERSONATTRIBUTES");
        attributeData.setClientId(MdmConstants.CLIENT_ID);
    }
}
