package org.cru.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.ObjAddressDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import com.infosolve.openmdm.webservices.provider.impl.ObjCommunicationDTO;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import org.cru.mdm.MdmCodes;
import org.cru.mdm.PersonToMdmConverter;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
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

    @SuppressWarnings("unused")  //Used by CDI
    public UpdateService() {}

    @Inject
    public UpdateService(OpenDQProperties openDQProperties, AddressNormalizationService addressNormalizationService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
    }

    public void updatePerson(Person person, RealTimeObjectActionDTO foundPerson, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtIndex";
        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        RealTimeObjectActionDTO updatedPerson = updateMdm(person, foundPerson);
        RuntimeMatchWS runtimeMatchWS = callRuntimeMatchService();
        addSlot(runtimeMatchWS, person, updatedPerson);
    }

    private RealTimeObjectActionDTO updateMdm(Person person, RealTimeObjectActionDTO foundPerson)
    {
        DataManagementWSImpl mdmService = configureMdmService();

        setAddressIds(foundPerson, person);
        setCommunicationIds(foundPerson, person);
        person.setMdmPartyId(foundPerson.getObjectEntity().getPartyId());
        person.setMdmPersonAttributesId(obtainPersonAttributesId(foundPerson));
        person.setMdmPersonId(obtainPersonId(foundPerson));

        PersonToMdmConverter personToMdmConverter = new PersonToMdmConverter(ACTION);

        RealTimeObjectActionDTO returnedObject =
            mdmService.updateObject(personToMdmConverter.createRealTimeObjectFromPerson(person));

        if(returnedObject == null)
        {
            throw new WebApplicationException(Response.serverError().entity("Failed to update MDM").build());
        }

        return returnedObject;
    }

    private void setAddressIds(RealTimeObjectActionDTO foundObject, Person person)
    {
        List<ObjAddressDTO> foundAddresses = foundObject.getObjectAddresses().getObjectAddress();
        List<Address> passedInAddresses = person.getAddresses();

        if(passedInAddresses == null || passedInAddresses.isEmpty()) return;

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

    //There should only be 1 row with PERSON and 1 row with PERSONATTRIBUTES per Person
    private String obtainPersonAttributesId(RealTimeObjectActionDTO foundObject)
    {
        List<ObjAttributeDataDTO> attributeData = foundObject.getObjectAttributeDatas().getObjectAttributeData();

        for(ObjAttributeDataDTO attributes : attributeData)
        {
            if("PERSONATTRIBUTES".equals(attributes.getMultDetTypeLev1()))
            {
                return attributes.getObjAdId();
            }
        }

        return null;
    }

    private String obtainPersonId(RealTimeObjectActionDTO foundObject)
    {
        List<ObjAttributeDataDTO> attributeData = foundObject.getObjectAttributeDatas().getObjectAttributeData();

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
