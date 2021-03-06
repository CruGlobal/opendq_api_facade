package org.cru.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.apache.log4j.Logger;
import org.cru.mdm.MdmCodes;
import org.cru.mdm.PersonToMdmConverter;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.Person;
import org.cru.model.PersonAttributeDataId;
import org.cru.model.PhoneNumber;
import org.cru.qualifiers.Nickname;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle complexity of updating a {@link Person} in the index
 *
 * Created by William.Randall on 6/25/14.
 */
public class UpdateService extends AddService
{
    private static final String ACTION = "U";
    private static Logger log = Logger.getLogger(UpdateService.class);

    @SuppressWarnings("unused")  //Used by CDI
    public UpdateService() {}

    @Inject
    public UpdateService(OpenDQProperties openDQProperties,
        AddressNormalizationService addressNormalizationService,
        @Nickname NicknameService nicknameService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
        this.nicknameService = nicknameService;
    }

    public void updatePerson(Person person, RealTimeObjectActionDTO foundPerson, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";

        if(person.getAddresses() != null && !person.getAddresses().isEmpty())
        {
            for(Address address : person.getAddresses())
            {
                addressNormalizationService.normalizeAddress(address);
            }
        }

        RealTimeObjectActionDTO updatedPerson = updateMdm(person, foundPerson);
        addPersonToIndex(person, updatedPerson);
    }

    private RealTimeObjectActionDTO updateMdm(Person person, RealTimeObjectActionDTO foundPerson)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation("contact");

        setAddressIds(foundPerson, person);
        setCommunicationIds(foundPerson, person);
        person.setMdmPartyId(foundPerson.getObjectEntity().getPartyId());
        person.setMdmPersonAttributesIdMap(createPersonAttributesIdMap(foundPerson));
        person.setMdmPersonId(obtainPersonId(foundPerson));

        PersonToMdmConverter personToMdmConverter = new PersonToMdmConverter(ACTION);

        RealTimeObjectActionDTO returnedObject;
        try
        {
            returnedObject = mdmService.updateObject(personToMdmConverter.createRealTimeObjectFromPerson(person));
        }
        catch(Throwable t)
        {
            log.error("Failed to update person with GR ID: " + person.getId(), t);
            throw new WebApplicationException("Failed to update person: " + t.getMessage());
        }

        if(returnedObject == null)
        {
            log.error("Failed to update MDM - returned object was null");
            throw new WebApplicationException(Response.serverError().entity("Failed to update MDM").build());
        }

        return returnedObject;
    }

    private void setAddressIds(RealTimeObjectActionDTO foundObject, Person person)
    {
        List<ObjAddressDTO> foundAddresses = foundObject.getObjectAddresses().getObjectAddress();
        List<Address> passedInAddresses = person.getAddresses();

        if(passedInAddresses == null || passedInAddresses.isEmpty()) return;
        if(foundAddresses == null || foundAddresses.isEmpty()) return;

        Map<String, String> existingAddressIds = new HashMap<String, String>();

        for(ObjAddressDTO foundAddress : foundAddresses)
        {
            existingAddressIds.put(foundAddress.getUserDef1(), foundAddress.getAddressId());
        }

        for(Address passedAddress : passedInAddresses)
        {
            String mdmAddressId = existingAddressIds.get(passedAddress.getId());
            if(!Strings.isNullOrEmpty(mdmAddressId))
            {
                passedAddress.setMdmAddressId(mdmAddressId);
            }
        }
    }

    private void setCommunicationIds(RealTimeObjectActionDTO foundObject, Person person)
    {
        List<ObjCommunicationDTO> foundCommunications = foundObject.getObjectCommunications().getObjectCommunication();
        if(foundCommunications == null || foundCommunications.isEmpty()) return;

        setEmailIds(foundCommunications, person);
        setPhoneIds(foundCommunications, person);
    }

    private void setEmailIds(List<ObjCommunicationDTO> foundCommunications, Person person)
    {
        List<EmailAddress> passedInEmails = person.getEmailAddresses();
        List<MdmCodes> validEmailCodes = ImmutableList.of(MdmCodes.PERSONAL_EMAIL, MdmCodes.WORK_EMAIL);

        if(passedInEmails == null || passedInEmails.isEmpty()) return;

        Map<String, String> existingEmailIds = new HashMap<String, String>();

        for(ObjCommunicationDTO communication : foundCommunications)
        {
            if(validEmailCodes.contains(MdmCodes.getCodeWithId(communication.getCodId())))
            {
                existingEmailIds.put(communication.getUserDef1(), communication.getComId());
            }
        }

        for(EmailAddress emailAddress : passedInEmails)
        {
            String mdmCommunicationId = existingEmailIds.get(emailAddress.getId());
            if(!Strings.isNullOrEmpty(mdmCommunicationId))
            {
                emailAddress.setMdmCommunicationId(mdmCommunicationId);
            }
        }
    }

    private void setPhoneIds(List<ObjCommunicationDTO> foundCommunications, Person person)
    {
        List<PhoneNumber> passedInPhones = person.getPhoneNumbers();
        List<MdmCodes> validPhoneCodes = ImmutableList.of(MdmCodes.HOME_PHONE);

        if(passedInPhones == null || passedInPhones.isEmpty()) return;

        Map<String, String> existingPhoneIds = new HashMap<String, String>();

        for(ObjCommunicationDTO communication : foundCommunications)
        {
            if(validPhoneCodes.contains(MdmCodes.getCodeWithId(communication.getCodId())))
            {
                existingPhoneIds.put(communication.getUserDef1(), communication.getComId());
            }
        }

        for(PhoneNumber phoneNumber : passedInPhones)
        {
            String mdmCommunicationId = existingPhoneIds.get(phoneNumber.getId());
            if(!Strings.isNullOrEmpty(mdmCommunicationId))
            {
                phoneNumber.setMdmCommunicationId(mdmCommunicationId);
            }
        }
    }

    private Map<PersonAttributeDataId, String> createPersonAttributesIdMap(RealTimeObjectActionDTO foundObject)
    {
        List<ObjAttributeDataDTO> attributeData = foundObject.getObjectAttributeDatas().getObjectAttributeData();
        Map<PersonAttributeDataId, String> mdmPersonAttributesIdMap = new HashMap<PersonAttributeDataId, String>();

        if(attributeData == null || attributeData.isEmpty()) return mdmPersonAttributesIdMap;

        for(ObjAttributeDataDTO attributes : attributeData)
        {
            if("PERSONATTRIBUTES".equals(attributes.getMultDetTypeLev1()))
            {
                PersonAttributeDataId idKey = new PersonAttributeDataId();
                idKey.setAttributeDataType(attributes.getMultDetTypeLev2());
                //Each type of attribute has at least 2 fields, and Field 2 is a good identifier for each type
                idKey.setSecondaryIdentifier(attributes.getField2());
                mdmPersonAttributesIdMap.put(idKey, attributes.getObjAdId());
            }
        }

        return mdmPersonAttributesIdMap;
    }

    //There should only be 1 row with PERSON
    private String obtainPersonId(RealTimeObjectActionDTO foundObject)
    {
        List<ObjAttributeDataDTO> attributeData = foundObject.getObjectAttributeDatas().getObjectAttributeData();

        if(attributeData == null || attributeData.isEmpty()) return null;

        for(ObjAttributeDataDTO attributes : attributeData)
        {
            if("PERSON".equals(attributes.getMultDetTypeLev1()))
            {
                return attributes.getObjAdId();
            }
        }

        return null;
    }
}
