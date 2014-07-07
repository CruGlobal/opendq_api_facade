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
import org.cru.model.PersonName;
import org.cru.model.PhoneNumber;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

        addAddressesIfExist(realTimeObject, createAddressDTOList(person.getAddresses(), today));
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

    ObjEntityDTO createObjEntity(Person person, LocalDate today)
    {
        ObjEntityDTO objEntityDTO = new ObjEntityDTO();

        objEntityDTO.setPartyId(person.getMdmPartyId());
        objEntityDTO.setTypId(MdmConstants.TYP_ID);
        objEntityDTO.setCustomer("Y"); //TODO: Not Always...Come from Client?
        objEntityDTO.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        objEntityDTO.setDateCreated(today.toString(opendqDatePattern));
        objEntityDTO.setUserCreated(MdmConstants.USER);
        objEntityDTO.setSource(MdmConstants.SOURCE);
        objEntityDTO.setAction(action);
        objEntityDTO.setSrcId(person.getClientIntegrationId());
        objEntityDTO.setStatus(MdmStatus.APPROVED.getStatusCode()); //TODO: What should go here?
        objEntityDTO.setActive("Y"); //TODO: Should this ever be 'N'?
        objEntityDTO.setClientId(MdmConstants.CLIENT_ID);

        return objEntityDTO;
    }

    ObjAddressDTOList createAddressDTOList(List<Address> addresses, LocalDate today)
    {
        ObjAddressDTOList addressDTOList = new ObjAddressDTOList();
        List<ObjAddressDTO> internalList = addressDTOList.getObjectAddress();

        int numAddress = 0;
        for(Address address : addresses)
        {
            ObjAddressDTO addressToAdd = new ObjAddressDTO();

            addressToAdd.setAddressId(address.getMdmAddressId());
            addressToAdd.setComExclusionType("N");  //TODO: May need to get from client

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
            addressToAdd.setSource(MdmConstants.SOURCE);
            addressToAdd.setAction(action);
            addressToAdd.setUserDef1(address.getId());

            //TODO: This should come in from the json
            if(numAddress == 0) addressToAdd.setCodId(MdmCodes.MAILING_ADDRESS.getId());
            else addressToAdd.setCodId(MdmCodes.BILLING_ADDRESS.getId());

            addressToAdd.setClientId(MdmConstants.CLIENT_ID);
            addressToAdd.setTypId(MdmConstants.TYP_ID);


            internalList.add(addressToAdd);
            numAddress++;
        }

        return addressDTOList;
    }

    ObjCommunicationDTOList createObjCommunicationList(Person person, LocalDate today)
    {
        List<EmailAddress> personEmails = person.getEmailAddresses();
        List<PhoneNumber> personPhoneNumbers = person.getPhoneNumbers();
        ObjCommunicationDTOList communicationDTOList = new ObjCommunicationDTOList();
        List<ObjCommunicationDTO> innerList = communicationDTOList.getObjectCommunication();

        if(personEmails != null && !personEmails.isEmpty())
        {
            int numEmail = 0;
            for(EmailAddress personEmail : personEmails)
            {
                ObjCommunicationDTO emailCommunication = new ObjCommunicationDTO();

                emailCommunication.setComId(personEmail.getMdmCommunicationId());
                emailCommunication.setPartyId(person.getMdmPartyId());

                //TODO: This should come in from the json
                if(numEmail == 0) emailCommunication.setCodId(MdmCodes.PRIMARY_EMAIL.getId());
                else emailCommunication.setCodId(MdmCodes.SECONDARY_EMAIL.getId());

                emailCommunication.setCommdata(personEmail.getEmail());
                emailCommunication.setComExclusionType("N");  //TODO: May need to get from client
                emailCommunication.setDateCreated(today.toString(opendqDatePattern));
                emailCommunication.setUserCreated(MdmConstants.USER);
                emailCommunication.setSource(MdmConstants.SOURCE);
                emailCommunication.setAction(action);
                emailCommunication.setClientId(MdmConstants.CLIENT_ID);
                emailCommunication.setTypId(MdmConstants.TYP_ID);
                emailCommunication.setUserDef1(personEmail.getId());

                innerList.add(emailCommunication);
                numEmail++;
            }
        }

        if(personPhoneNumbers != null && !personPhoneNumbers.isEmpty())
        {
            int numPhone = 0;
            for(PhoneNumber personPhone : personPhoneNumbers)
            {
                ObjCommunicationDTO phoneCommunication = new ObjCommunicationDTO();

                phoneCommunication.setComId(personPhone.getMdmCommunicationId());
                phoneCommunication.setPartyId(person.getMdmPartyId());

                //TODO: This should come in from the json (determined by location perhaps)
                if(numPhone == 0) phoneCommunication.setCodId(MdmCodes.PRIMARY_PHONE.getId());
                else phoneCommunication.setCodId(MdmCodes.SECONDARY_PHONE.getId());

                phoneCommunication.setCommdata(personPhone.getNumber());
                phoneCommunication.setComExclusionType("N");  //TODO: May need to get from client
                phoneCommunication.setDateCreated(today.toString(opendqDatePattern));
                phoneCommunication.setUserCreated(MdmConstants.USER);
                phoneCommunication.setSource(MdmConstants.SOURCE);
                phoneCommunication.setAction(action);
                phoneCommunication.setUserDef1(personPhone.getId());
                phoneCommunication.setUserDef2(personPhone.getLocation());
                phoneCommunication.setClientId(MdmConstants.CLIENT_ID);
                phoneCommunication.setTypId(MdmConstants.TYP_ID);

                innerList.add(phoneCommunication);
                numPhone++;
            }
        }

        return communicationDTOList;
    }

    ObjAttributeDataDTOList createAttributeDataDTOList(Person person, LocalDate today)
    {
        ObjAttributeDataDTOList objAttributeDataDTOList = new ObjAttributeDataDTOList();
        List<ObjAttributeDataDTO> internalList = objAttributeDataDTOList.getObjectAttributeData();

        internalList.add(createPersonAttributesAttributeData(person, today));
        internalList.add(createPersonAttributeData(person, today));

        return objAttributeDataDTOList;
    }

    /**
     * This includes account information
     */
    ObjAttributeDataDTO createPersonAttributesAttributeData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO personAttributes = new ObjAttributeDataDTO();

        personAttributes.setObjAdId(person.getMdmPersonAttributesId());
        personAttributes.setField1(MdmConstants.SOURCE);
        personAttributes.setField2(person.getLinkedIdentities().getEmployeeNumber());
        personAttributes.setField3(person.getLinkedIdentities().getSiebelContactId());
        personAttributes.setField4(new DateTime().toString());  //TODO: Source Modified Date
        personAttributes.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        personAttributes.setTypId(MdmConstants.TYP_ID);
        personAttributes.setDateCreated(today.toString(opendqDatePattern));
        personAttributes.setUserCreated(MdmConstants.USER);
        personAttributes.setSource(MdmConstants.SOURCE);
        personAttributes.setAction(action);
        personAttributes.setMultDetTypeLev1("PERSONATTRIBUTES");
        personAttributes.setMultDetTypeLev2("AccountData");
        personAttributes.setClientId(MdmConstants.CLIENT_ID);

        return personAttributes;
    }

    ObjAttributeDataDTO createPersonAttributeData(Person person, LocalDate today)
    {
        ObjAttributeDataDTO attributeData = new ObjAttributeDataDTO();
        PersonName personName = person.getName();

        attributeData.setObjAdId(person.getMdmPersonId());
        attributeData.setField1(personName.getTitle());
        attributeData.setField2(personName.getFirstName());
        attributeData.setField3(personName.getMiddleName());
        attributeData.setField4(personName.getLastName());
        attributeData.setField5(personName.getSuffix());
        attributeData.setField6(person.getGender());

        attributeData.setFromDate(today.toString(opendqDatePattern));  // This is overwritten on insert
        attributeData.setTypId(MdmConstants.TYP_ID);
        attributeData.setDateCreated(today.toString(opendqDatePattern));
        attributeData.setUserCreated(MdmConstants.USER);
        attributeData.setSource(MdmConstants.SOURCE);
        attributeData.setAction(action);
        attributeData.setMultDetTypeLev1("PERSON");
        attributeData.setClientId(MdmConstants.CLIENT_ID);

        return attributeData;
    }
}
