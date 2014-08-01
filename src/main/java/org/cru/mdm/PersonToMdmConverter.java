package org.cru.mdm;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTOList;
import com.infosolve.openmdm.webservices.provider.impl.ObjEntityDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.Address;
import org.cru.model.Authentication;
import org.cru.model.EmailAddress;
import org.cru.model.LinkedIdentity;
import org.cru.model.Person;
import org.cru.model.PersonAttributeDataId;
import org.cru.model.PhoneNumber;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to help with conversion from {@link Person} to Mdm entities
 *
 * Created by William.Randall on 6/24/14.
 */
public class PersonToMdmConverter
{
    private String opendqDatePattern = "MM/dd/YYYY";
    private String action;
    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

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

    void setCommonAttributeData(ObjAttributeDataDTO attributeData, Person person, LocalDate today, PersonAttributeDataId key)
    {
        Map mdmPersonAttributeIdMap = person.getMdmPersonAttributesIdMap();
        if(mdmPersonAttributeIdMap == null || mdmPersonAttributeIdMap.isEmpty() || mdmPersonAttributeIdMap.get(key) == null)
        {
            attributeData.setObjAdId(MdmConstants.JUNK_ID);
        }
        else attributeData.setObjAdId((String)mdmPersonAttributeIdMap.get(key));

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
        sourceDetails.setMultDetTypeLev2("SOURCEDETAILS");

        sourceDetails.setField1(person.getSource().getSystemId());
        sourceDetails.setField2(person.getSource().getSystemId());

        PersonAttributeDataId idKey = new PersonAttributeDataId();
        idKey.setAttributeDataType(sourceDetails.getMultDetTypeLev2());
        idKey.setSecondaryIdentifier(sourceDetails.getField2());

        setCommonAttributeData(sourceDetails, person, today, idKey);

        return sourceDetails;
    }

    ObjAttributeDataDTO createHouseholdData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO householdData = new ObjAttributeDataDTO();
        householdData.setMultDetTypeLev2("HOUSEHOLD");

        householdData.setField1(person.getId()); //TODO: Object ID
        householdData.setField2(person.getFirstName());
        householdData.setField3(person.getLastName());

        PersonAttributeDataId idKey = new PersonAttributeDataId();
        idKey.setAttributeDataType(householdData.getMultDetTypeLev2());
        idKey.setSecondaryIdentifier(householdData.getField2());

        setCommonAttributeData(householdData, person, today, idKey);

        return householdData;
    }

    List<ObjAttributeDataDTO> createAccountData(Person person, LocalDate today)
    {
        List<ObjAttributeDataDTO> accountDataList = Lists.newArrayList();
        List<LinkedIdentity> identitiesList = person.getLinkedIdentities();

        if(identitiesList == null || identitiesList.isEmpty()) return null;

        for(LinkedIdentity identity : identitiesList)
        {
            ObjAttributeDataDTO accountData = new ObjAttributeDataDTO();
            accountData.setMultDetTypeLev2("ACCOUNTDATA");

            accountData.setField1(identity.getSystemId());
            accountData.setField2(person.getAccountNumber());  //TODO: Is this correct?
            accountData.setField3(identity.getClientIntegrationId());

            if(person.getClientUpdatedAt() == null)
            {
                accountData.setField4(null);
            }
            else
            {
                accountData.setField4(person.getClientUpdatedAt().toString(dateFormatter));
            }

            PersonAttributeDataId idKey = new PersonAttributeDataId();
            idKey.setAttributeDataType(accountData.getMultDetTypeLev2());
            idKey.setSecondaryIdentifier(accountData.getField2());

            setCommonAttributeData(accountData, person, today, idKey);

            accountDataList.add(accountData);
        }

        return accountDataList;
    }

    ObjAttributeDataDTO createRelayDetails(Person person, LocalDate today)
    {
        ObjAttributeDataDTO relayDetails = new ObjAttributeDataDTO();
        relayDetails.setMultDetTypeLev2("RELAYDETAILS");

        if(person.getAuthentication() != null)
        {
            relayDetails.setField1(person.getSource().getSystemId());
            relayDetails.setField2(person.getAuthentication().getEmployeeRelayGuid());
            relayDetails.setField3(person.getAuthentication().getRelayGuid());

            PersonAttributeDataId idKey = new PersonAttributeDataId();
            idKey.setAttributeDataType(relayDetails.getMultDetTypeLev2());
            idKey.setSecondaryIdentifier(relayDetails.getField2());

            setCommonAttributeData(relayDetails, person, today, idKey);

            return relayDetails;
        }
        return null;
    }

    List<ObjAttributeDataDTO> createIdentityData(Person person, LocalDate today)
    {
        List<ObjAttributeDataDTO> identityDataList = Lists.newArrayList();
        List<LinkedIdentity> identitiesList = person.getLinkedIdentities();

        if(identitiesList == null || identitiesList.isEmpty()) return null;

        for(LinkedIdentity identity : identitiesList)
        {
            ObjAttributeDataDTO identityData = new ObjAttributeDataDTO();
            identityData.setMultDetTypeLev2("IDENTITIES");

            identityData.setField1(identity.getSystemId()); //TODO: Name
            identityData.setField2(identity.getClientIntegrationId());
            identityData.setField3(identity.getSystemId());

            if(person.getClientUpdatedAt() == null)
            {
                identityData.setField4(null);
            }
            else
            {
                identityData.setField4(person.getClientUpdatedAt().toString(dateFormatter));
            }

            PersonAttributeDataId idKey = new PersonAttributeDataId();
            idKey.setAttributeDataType(identityData.getMultDetTypeLev2());
            idKey.setSecondaryIdentifier(identityData.getField2());

            setCommonAttributeData(identityData, person, today, idKey);

            identityDataList.add(identityData);
        }

        return identityDataList;
    }

    List<ObjAttributeDataDTO> createAuthProviderData(Person person, LocalDate today)
    {
        Authentication personAuthentication = person.getAuthentication();
        if(personAuthentication == null) return null;

        List<ObjAttributeDataDTO> authProviderDataList = Lists.newArrayList();
        addAuthProviderIfAvailable(authProviderDataList, person, "Facebook", personAuthentication.getFacebookUid(), today);
        addAuthProviderIfAvailable(authProviderDataList, person, "Google Apps", personAuthentication.getGoogleAppsUid(), today);
        addAuthProviderIfAvailable(authProviderDataList, person, "Relay", personAuthentication.getRelayGuid(), today);
        addAuthProviderIfAvailable(authProviderDataList, person, "Relay (Employee)", personAuthentication.getEmployeeRelayGuid(), today);
        addAuthProviderIfAvailable(authProviderDataList, person, "The Key", personAuthentication.getKeyGuid(), today);

        return authProviderDataList;
    }

    private void addAuthProviderIfAvailable(
        List<ObjAttributeDataDTO> authProviderDataList,
        Person person,
        String name,
        String id,
        LocalDate today)
    {
        if(Strings.isNullOrEmpty(id)) return;

        ObjAttributeDataDTO authProviderData = new ObjAttributeDataDTO();
        authProviderData.setMultDetTypeLev2("AUTHPROVIDER");

        authProviderData.setField1(name);
        authProviderData.setField2(id);
        authProviderData.setField3(new DateTime().toString(dateFormatter));

        PersonAttributeDataId idKey = new PersonAttributeDataId();
        idKey.setAttributeDataType(authProviderData.getMultDetTypeLev2());
        idKey.setSecondaryIdentifier(authProviderData.getField2());

        setCommonAttributeData(authProviderData, person, today, idKey);

        authProviderDataList.add(authProviderData);
    }

    /**
     * This includes account information
     */
    List<ObjAttributeDataDTO> createPersonAttributesAttributeData(Person person, LocalDate today)
    {
        List<ObjAttributeDataDTO> personAttributes = new ArrayList<ObjAttributeDataDTO>();

        addIfNotNull(personAttributes, createSourceDetails(person, today));
        addIfNotNull(personAttributes, createHouseholdData(person, today));
        addIfNotEmpty(personAttributes, createAccountData(person, today));
        addIfNotNull(personAttributes, createRelayDetails(person, today));
        addIfNotEmpty(personAttributes, createIdentityData(person, today));
        addIfNotEmpty(personAttributes, createAuthProviderData(person, today));

        return personAttributes;
    }

    private void addIfNotNull(List<ObjAttributeDataDTO> personAttributes, ObjAttributeDataDTO personAttribute)
    {
        if(personAttribute != null) personAttributes.add(personAttribute);
    }
    
    private void addIfNotEmpty(List<ObjAttributeDataDTO> personAttributes, List<ObjAttributeDataDTO> personAttributeList)
    {
        if(personAttributeList != null && !personAttributeList.isEmpty()) personAttributes.addAll(personAttributeList);
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

        List<LinkedIdentity> identitiesList = person.getLinkedIdentities();
        if(identitiesList != null && !identitiesList.isEmpty())
        {
            for(LinkedIdentity identity : identitiesList)
            {
                if(!Strings.isNullOrEmpty(identity.getEmployeeNumber()))
                {
                    attributeData.setField8(identity.getEmployeeNumber());
                }
            }
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
