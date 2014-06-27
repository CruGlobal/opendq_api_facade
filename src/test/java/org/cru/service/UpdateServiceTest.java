package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.mdm.MdmCodes;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.Person;
import org.cru.model.PersonName;
import org.cru.model.PhoneNumber;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests the {@link UpdateService} class
*
* Created by William.Randall on 6/25/14.
*/
@Test
public class UpdateServiceTest
{
    private UpdateService updateService;
    private AddressNormalizationService addressNormalizationService;

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        addressNormalizationService = mock(AddressNormalizationService.class);

        updateService = new UpdateService(openDQProperties, addressNormalizationService);
    }

    public void testUpdatePerson() throws Exception
    {
        Person testPerson = createTestPersonGotMarried();

        for(Address address : testPerson.getAddresses())
        {
            when(addressNormalizationService.normalizeAddress(address)).thenReturn(false);
        }
        updateService.updatePerson(testPerson, createMockMdmPerson(), "Update");
    }

    private Person createTestPersonGotMarried()
    {
        Person testPerson = new Person();

        Address testAddress = new Address();
        testAddress.setId("kses34223-dk43-9493-394nfa2348d1");
        testAddress.setAddressLine1("1126 Blvd Way");
        testAddress.setCity("Las Vegas");
        testAddress.setState("NV");
        testAddress.setZipCode("84253");
        testAddress.setCountry("USA");
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(testAddress);

        PersonName personName = new PersonName();
        personName.setTitle("Mrs.");
        personName.setFirstName("Nom");
        personName.setLastName("Tom");

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

        testPerson.setGlobalRegistryId("3ikfj32-8rt4-9493-394nfa2348da");
        testPerson.setClientIntegrationId("221568");
        testPerson.setSiebelContactId("1-6T4D4");

        testPerson.setName(personName);
        testPerson.setAddresses(addresses);
        testPerson.setEmailAddresses(emailAddresses);
        testPerson.setPhoneNumbers(phoneNumbers);
        testPerson.setGender("F");

        return testPerson;
    }

    private RealTimeObjectActionDTO createMockMdmPerson()
    {
        String partyId = "1073";
        RealTimeObjectActionDTO mdmPerson = new RealTimeObjectActionDTO();

        ObjEntityDTO objEntity = new ObjEntityDTO();
        objEntity.setPartyId(partyId);
        objEntity.setSrcId("221568");
        objEntity.setActive("Y");


        ObjAddressDTO objAddress = new ObjAddressDTO();
        objAddress.setAddressId("786");
        objAddress.setCodId(MdmCodes.MAILING_ADDRESS.getId());
        objAddress.setAddressLine1("1125 Blvd Way");
        objAddress.setCityName("Las Vegas");
        objAddress.setStateName("NV");
        objAddress.setZip("84253");
        objAddress.setCryName("USA");

        ObjAddressDTOList objAddresses = new ObjAddressDTOList();
        List<ObjAddressDTO> addressInnerList = new ArrayList<ObjAddressDTO>();
        addressInnerList.add(objAddress);


        ObjCommunicationDTO emailCommunication = new ObjCommunicationDTO();
        emailCommunication.setComId("648");
        emailCommunication.setCodId(MdmCodes.PRIMARY_EMAIL.getId());
        emailCommunication.setPartyId(partyId);
        emailCommunication.setCommdata("nom.nom@crutest.org");

        ObjCommunicationDTO phoneCommunication = new ObjCommunicationDTO();
        phoneCommunication.setComId("649");
        phoneCommunication.setCodId(MdmCodes.PRIMARY_PHONE.getId());
        phoneCommunication.setPartyId(partyId);
        phoneCommunication.setCommdata("5555555553");
        phoneCommunication.setUserDef1("work");

        ObjCommunicationDTOList objCommunications = new ObjCommunicationDTOList();
        List<ObjCommunicationDTO> communicationInnerList = new ArrayList<ObjCommunicationDTO>();
        communicationInnerList.add(emailCommunication);
        communicationInnerList.add(phoneCommunication);


        ObjAttributeDataDTO personAttributeData = new ObjAttributeDataDTO();
        personAttributeData.setObjAdId("1464");
        personAttributeData.setField1("OAF");
        personAttributeData.setField3("1-6T4D4");
        personAttributeData.setMultDetTypeLev1("PERSONATTRIBUTES");
        personAttributeData.setMultDetTypeLev2("AccountData");

        ObjAttributeDataDTO personData = new ObjAttributeDataDTO();
        personData.setObjAdId("1465");
        personData.setField1("Ms.");  //Title
        personData.setField2("Nom");  //First Name
        personData.setField4("Nom");  //Last Name
        personData.setField6("F");    //Gender
        personData.setMultDetTypeLev1("PERSON");


        mdmPerson.setObjectEntity(objEntity);
        mdmPerson.setObjectAddresses(objAddresses);
        mdmPerson.setObjectCommunications(objCommunications);

        return mdmPerson;
    }
}