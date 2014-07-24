package org.cru.mdm;

import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to help with conversion from {@link Person} to Mdm entities
 *
 * Created by William.Randall on 6/24/14.
 */
public class PersonToMdmConverter
{
    private String opendqDatePattern = "MM/dd/YYYY";
    private String action;

    public PersonToMdmConverter(String action)
    {
        this.action = action;
    }

    public RealTimeObjectActionDTO createRealTimeObjectFromPerson(Person person)
    {
        LocalDate today = new LocalDate();

        RealTimeObjectActionDTO realTimeObject = new RealTimeObjectActionDTO();

        addAddressesIfExist(realTimeObject, createAddressDTOList(person.getAddresses(), today, person.getSource().getSystemId()));
        addAttributeDataIfExists(realTimeObject, createAttributeDataDTOList(person, today));
        addCommunicationsIfExist(realTimeObject, createObjCommunicationList(person, today));

        realTimeObject.setObjectEntity(createObjEntity(person, today));

        return realTimeObject;
    }

    private void addAddressesIfExist(RealTimeObjectActionDTO realTimeObject, ObjAddressDTOList addressList)
    {
        if(addressList.getObjectAddress() != null && !addressList.getObjectAddress().isEmpty())
        {
            realTimeObject.setObjectAddresses(addressList);
        }
    }

    private void addCommunicationsIfExist(RealTimeObjectActionDTO realTimeObject, ObjCommunicationDTOList communicationList)
    {
        if(communicationList.getObjectCommunication() != null && !communicationList.getObjectCommunication().isEmpty())
        {
            realTimeObject.setObjectCommunications(communicationList);
        }
    }

    private void addAttributeDataIfExists(RealTimeObjectActionDTO realTimeObject, ObjAttributeDataDTOList attributeDataList)
    {
        if(attributeDataList.getObjectAttributeData() != null && !attributeDataList.getObjectAttributeData().isEmpty())
        {
            realTimeObject.setObjectAttributeDatas(attributeDataList);
        }
    }

    private ObjEntityDTO createObjEntity(Person person, LocalDate today)
    {
        ObjEntityDTO objEntityDTO = new ObjEntityDTO();

        objEntityDTO.setPartyId(person.getMdmPartyId());
        objEntityDTO.setTypId(MdmConstants.TYP_ID);
        objEntityDTO.setCustomer("Y");
        objEntityDTO.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        objEntityDTO.setDateCreated(today.toString(opendqDatePattern));
        objEntityDTO.setUserCreated(MdmConstants.USER);
        objEntityDTO.setSource(person.getSource().getSystemId());
        objEntityDTO.setAction(action);
        objEntityDTO.setSrcId(person.getClientIntegrationId());
        objEntityDTO.setStatus(MdmStatus.APPROVED.getStatusCode());
        objEntityDTO.setActive("Y");
        objEntityDTO.setClientId(MdmConstants.CLIENT_ID);

        return objEntityDTO;
    }

    private ObjAddressDTOList createAddressDTOList(List<Address> addresses, LocalDate today, String sourceId)
    {
        ObjAddressDTOList addressDTOList = new ObjAddressDTOList();
        List<ObjAddressDTO> internalList = addressDTOList.getObjectAddress();

        for(Address address : addresses)
        {
            ObjAddressDTO addressToAdd = new ObjAddressDTO();

            addressToAdd.setAddressId(address.getMdmAddressId());
            addressToAdd.setComExclusionType("N");

            addressToAdd.setAddressLine1(address.getAddressLine1());
            addressToAdd.setAddressLine2(address.getAddressLine2());
            addressToAdd.setAddressLine3(address.getAddressLine3());
            addressToAdd.setAddressLine4(address.getAddressLine4());
            addressToAdd.setCityName(address.getCity());
            addressToAdd.setStateName(address.getState());
            addressToAdd.setCryName(address.getCountry());
            addressToAdd.setZip(address.getZipCode());

            addressToAdd.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
            addressToAdd.setDateCreated(today.toString(opendqDatePattern));
            addressToAdd.setUserCreated(MdmConstants.USER);
            addressToAdd.setSource(sourceId);
            addressToAdd.setAction(action);
            addressToAdd.setUserDef1(address.getId());

            addressToAdd.setCodId(MdmCodes.HOME_ADDRESS.getId());
            addressToAdd.setClientId(MdmConstants.CLIENT_ID);
            addressToAdd.setTypId(MdmConstants.TYP_ID);

            internalList.add(addressToAdd);
        }

        return addressDTOList;
    }

    private ObjCommunicationDTOList createObjCommunicationList(Person person, LocalDate today)
    {
        List<EmailAddress> personEmails = person.getEmailAddresses();
        List<PhoneNumber> personPhoneNumbers = person.getPhoneNumbers();
        ObjCommunicationDTOList communicationDTOList = new ObjCommunicationDTOList();
        List<ObjCommunicationDTO> innerList = communicationDTOList.getObjectCommunication();

        if(personEmails != null && !personEmails.isEmpty())
        {
            for(EmailAddress personEmail : personEmails)
            {
                ObjCommunicationDTO emailCommunication = new ObjCommunicationDTO();

                emailCommunication.setComId(personEmail.getMdmCommunicationId());
                emailCommunication.setPartyId(person.getMdmPartyId());
                emailCommunication.setCodId(MdmCodes.PERSONAL_EMAIL.getId());

                emailCommunication.setCommdata(personEmail.getEmail());
                emailCommunication.setComExclusionType("N");
                emailCommunication.setDateCreated(today.toString(opendqDatePattern));
                emailCommunication.setUserCreated(MdmConstants.USER);
                emailCommunication.setSource(person.getSource().getSystemId());
                emailCommunication.setAction(action);
                emailCommunication.setClientId(MdmConstants.CLIENT_ID);
                emailCommunication.setTypId(MdmConstants.TYP_ID);
                emailCommunication.setUserDef1(personEmail.getId());

                innerList.add(emailCommunication);
            }
        }

        if(personPhoneNumbers != null && !personPhoneNumbers.isEmpty())
        {
            for(PhoneNumber personPhone : personPhoneNumbers)
            {
                ObjCommunicationDTO phoneCommunication = new ObjCommunicationDTO();

                phoneCommunication.setComId(personPhone.getMdmCommunicationId());
                phoneCommunication.setPartyId(person.getMdmPartyId());
                phoneCommunication.setCodId(MdmCodes.HOME_PHONE.getId());

                phoneCommunication.setCommdata(personPhone.getNumber());
                phoneCommunication.setComExclusionType("N");
                phoneCommunication.setDateCreated(today.toString(opendqDatePattern));
                phoneCommunication.setUserCreated(MdmConstants.USER);
                phoneCommunication.setSource(person.getSource().getSystemId());
                phoneCommunication.setAction(action);
                phoneCommunication.setUserDef1(personPhone.getId());
                phoneCommunication.setUserDef2(personPhone.getLocation());
                phoneCommunication.setClientId(MdmConstants.CLIENT_ID);
                phoneCommunication.setTypId(MdmConstants.TYP_ID);

                innerList.add(phoneCommunication);
            }
        }

        return communicationDTOList;
    }

    private ObjAttributeDataDTOList createAttributeDataDTOList(Person person, LocalDate today)
    {
        ObjAttributeDataDTOList objAttributeDataDTOList = new ObjAttributeDataDTOList();
        List<ObjAttributeDataDTO> internalList = objAttributeDataDTOList.getObjectAttributeData();

        internalList.addAll(createPersonAttributesAttributeData(person, today));
        internalList.add(createPersonAttributeData(person, today));

        return objAttributeDataDTOList;
    }

    void setCommonAttributeData(ObjAttributeDataDTO attributeData, Person person, LocalDate today)
    {
        attributeData.setObjAdId(person.getMdmPersonAttributesId());
        attributeData.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        attributeData.setTypId(MdmConstants.TYP_ID);
        attributeData.setDateCreated(today.toString(opendqDatePattern));
        attributeData.setUserCreated(MdmConstants.USER);
        attributeData.setSource(person.getSource().getSystemId());
        attributeData.setAction(action);
        attributeData.setMultDetTypeLev1("PERSONATTRIBUTES");
        attributeData.setClientId(MdmConstants.CLIENT_ID);
    }

    ObjAttributeDataDTO createSourceDetails(Person person, LocalDate today)
    {
        ObjAttributeDataDTO sourceDetails = new ObjAttributeDataDTO();
        setCommonAttributeData(sourceDetails, person, today);
        sourceDetails.setMultDetTypeLev2("SOURCEDETAILS");

        sourceDetails.setField1(person.getSource().getSystemId()); //TODO: Source System
        sourceDetails.setField2(person.getSource().getSystemId()); //TODO: Source ID

        return sourceDetails;
    }

    ObjAttributeDataDTO createHouseholdData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO householdData = new ObjAttributeDataDTO();
        setCommonAttributeData(householdData, person, today);
        householdData.setMultDetTypeLev2("HOUSEHOLD");

        householdData.setField1(person.getId()); //TODO: Object ID
        householdData.setField2(person.getFirstName());
        householdData.setField3(person.getLastName());

        return householdData;
    }

    ObjAttributeDataDTO createAccountData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO accountData = new ObjAttributeDataDTO();
        setCommonAttributeData(accountData, person, today);
        accountData.setMultDetTypeLev2("ACCOUNTDATA");

        accountData.setField1(MdmConstants.SOURCE); //TODO: Account Source name

        if(person.getLinkedIdentities() != null)
        {
            accountData.setField2(person.getAccountNumber());  //TODO: Is this correct?
            accountData.setField3(person.getLinkedIdentities().getSiebelContactId());
        }

        if(person.getClientUpdatedAt() == null)
        {
            accountData.setField4(null);
        }
        else
        {
            accountData.setField4(person.getClientUpdatedAt().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return accountData;
    }

    ObjAttributeDataDTO createRelayDetails(Person person, LocalDate today)
    {
        ObjAttributeDataDTO relayDetails = new ObjAttributeDataDTO();
        setCommonAttributeData(relayDetails, person, today);
        relayDetails.setMultDetTypeLev2("RELAYDETAILS");

        if(person.getAuthentication() != null)
        {
            relayDetails.setField1(person.getSource().getSystemId());
            relayDetails.setField2(person.getAuthentication().getEmployeeRelayGuid());
            relayDetails.setField3(person.getAuthentication().getRelayGuid());

            return relayDetails;
        }
        return null;
    }

    ObjAttributeDataDTO createIdentityData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO identityData = new ObjAttributeDataDTO();
        setCommonAttributeData(identityData, person, today);
        identityData.setMultDetTypeLev2("IDENTITIES");

        identityData.setField1("Name"); //TODO: Name
        identityData.setField2("Identifier"); //TODO: Identifier
        identityData.setField3("Source System"); //TODO: Source Systems

        if(person.getClientUpdatedAt() == null)
        {
            identityData.setField4(null);
        }
        else
        {
            identityData.setField4(person.getClientUpdatedAt().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return identityData;
    }

    ObjAttributeDataDTO createAuthProviderData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO authProviderData = new ObjAttributeDataDTO();
        setCommonAttributeData(authProviderData, person, today);
        authProviderData.setMultDetTypeLev2("AUTHPROVIDER");

        authProviderData.setField1("Auth Source Name"); //TODO: Auth Source Name
        authProviderData.setField2("Auth Source Identifier"); //TODO: Auth Source Identifier
        authProviderData.setField3("Auth Create Date"); //TODO: Auth Create Date

        return authProviderData;
    }

    /**
     * This includes account information
     */
    List<ObjAttributeDataDTO> createPersonAttributesAttributeData(Person person, LocalDate today)
    {
        List<ObjAttributeDataDTO> personAttributes = new ArrayList<ObjAttributeDataDTO>();

        personAttributes.add(createSourceDetails(person, today));
        personAttributes.add(createHouseholdData(person, today));
        personAttributes.add(createAccountData(person, today));
        personAttributes.add(createRelayDetails(person, today));
        personAttributes.add(createIdentityData(person, today));
        personAttributes.add(createAuthProviderData(person, today));

        return personAttributes;
    }

    private ObjAttributeDataDTO createPersonAttributeData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO attributeData = new ObjAttributeDataDTO();

        attributeData.setObjAdId(person.getMdmPersonId());
        attributeData.setField1(person.getTitle());
        attributeData.setField2(person.getFirstName());
        attributeData.setField3(person.getMiddleName());
        attributeData.setField4(person.getLastName());
        attributeData.setField5(person.getSuffix());
        attributeData.setField6(person.getGender());

        if(person.getAuthentication() != null)
        {
            attributeData.setField7(person.getAuthentication().getRelayGuid());
            attributeData.setField9(person.getAuthentication().getEmployeeRelayGuid());
        }
        if(person.getLinkedIdentities() != null)
        {
            attributeData.setField8(person.getLinkedIdentities().getEmployeeNumber());
        }

        attributeData.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        attributeData.setTypId(MdmConstants.TYP_ID);
        attributeData.setDateCreated(today.toString(opendqDatePattern));
        attributeData.setUserCreated(MdmConstants.USER);
        attributeData.setSource(person.getSource().getSystemId());
        attributeData.setAction(action);
        attributeData.setMultDetTypeLev1("PERSON");
        attributeData.setClientId(MdmConstants.CLIENT_ID);

        return attributeData;
    }
}
