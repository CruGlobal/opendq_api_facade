package org.cru.service;

import com.beust.jcommander.internal.Lists;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.data.TestPeople;
import org.cru.mdm.MdmCodes;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentity;
import org.cru.model.Person;
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
        Person testPerson = TestPeople.generatePersonWithLotsOfData();

        testPerson.getAddresses().get(0).setAddressLine1("1126 Blvd Way");
        testPerson.setTitle("Mrs.");
        testPerson.setLastName("Tom");

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
        objAddress.setCodId(MdmCodes.HOME_ADDRESS.getId());
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
        emailCommunication.setCodId(MdmCodes.PERSONAL_EMAIL.getId());
        emailCommunication.setPartyId(partyId);
        emailCommunication.setCommdata("nom.nom@crutest.org");

        ObjCommunicationDTO phoneCommunication = new ObjCommunicationDTO();
        phoneCommunication.setComId("649");
        phoneCommunication.setCodId(MdmCodes.HOME_PHONE.getId());
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
