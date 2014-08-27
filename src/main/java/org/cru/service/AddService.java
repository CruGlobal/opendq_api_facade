package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.apache.log4j.Logger;
import org.cru.mdm.MdmConstants;
import org.cru.mdm.PersonToMdmConverter;
import org.cru.model.Address;
import org.cru.model.EmailAddress;
import org.cru.model.Person;
import org.cru.model.PhoneNumber;
import org.cru.model.map.IndexData;
import org.cru.model.map.NameAndAddressIndexData;
import org.cru.model.map.NameAndCommunicationIndexData;
import org.cru.qualifiers.Add;
import org.cru.qualifiers.Nickname;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.ConnectException;
import java.util.List;

/**
 * Service to handle complexity of adding a {@link Person} to the index
 *
 * Created by William.Randall on 6/9/14.
 */
@Add
public class AddService extends IndexingService
{
    AddressNormalizationService addressNormalizationService;
    NicknameService nicknameService;

    private static final String ACTION = "A";  // A = Add
    private static Logger log = Logger.getLogger(AddService.class);

    @SuppressWarnings("unused")  //used by CDI
    public AddService() {}

    @Inject
    public AddService(
        OpenDQProperties openDQProperties,
        AddressNormalizationService addressNormalizationService,
        @Nickname NicknameService nicknameService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
        this.nicknameService = nicknameService;
    }

    public void addPerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;

        if(person.getAddresses() != null && !person.getAddresses().isEmpty())
        {
            for(Address personAddress : person.getAddresses())
            {
                addressNormalizationService.normalizeAddress(personAddress);
            }
        }

        RealTimeObjectActionDTO addedPerson = addPersonToMdm(person);
        addPersonToIndex(person, addedPerson);
    }

    private RealTimeObjectActionDTO addPersonToMdm(Person person)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation("contact");
        PersonToMdmConverter personToMdmConverter = new PersonToMdmConverter(ACTION);

        RealTimeObjectActionDTO returnedObject;
        try
        {
            returnedObject = mdmService.addObject(personToMdmConverter.createRealTimeObjectFromPerson(person));
        }
        catch(Throwable t)
        {
            log.error("Failed to add person to MDM", t);
            throw new WebApplicationException("Failed to add person to mdm: " + t.getMessage());
        }

        //If we come back with the same party ID that we sent in for adding (0), then the add did not happen
        if(MdmConstants.JUNK_ID.equals(returnedObject.getObjectEntity().getPartyId()))
        {
            log.error("Failed to add person to MDM - Returned with Junk ID");
            throw new WebApplicationException("Failed to add person to mdm.");
        }

        return returnedObject;
    }

    void addPersonToIndex(Person person, RealTimeObjectActionDTO mdmPerson) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS;

        if(person.getAddresses() != null && !person.getAddresses().isEmpty())
        {
            this.stepName = "RtMatchAddr";
            runtimeMatchWS = configureAndRetrieveRuntimeMatchService("contact");
            //If more than one address was passed in, add them all to the index
            for(Address personAddress : person.getAddresses())
            {
                addPersonToIndexWithAddress(runtimeMatchWS, person, mdmPerson, personAddress);
            }
        }
        if(person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty())
        {
            this.stepName = "RtMatchComm";
            runtimeMatchWS = configureAndRetrieveRuntimeMatchService("communication");
            //If more than one email address was passed in, add them all to the index
            for(EmailAddress emailAddress : person.getEmailAddresses())
            {
                addPersonToIndexWithEmail(runtimeMatchWS, person, mdmPerson, emailAddress);
            }
        }
        if(person.getPhoneNumbers() != null && !person.getPhoneNumbers().isEmpty())
        {
            this.stepName = "RtMatchComm";
            runtimeMatchWS = configureAndRetrieveRuntimeMatchService("communication");
            //If more than one phone number was passed in, add them all to the index
            for(PhoneNumber phoneNumber : person.getPhoneNumbers())
            {
                addPersonToIndexWithPhoneNumber(runtimeMatchWS, person, mdmPerson, phoneNumber);
            }
        }
    }

    private void addPersonToIndexWithAddress(
        RuntimeMatchWS runtimeMatchWS,
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        Address addressToUse) throws ConnectException
    {
        addToIndex(
            runtimeMatchWS,
            generateFieldNamesAndValuesForNameAndAddressIndex(person, mdmPerson, addressToUse));
    }

    private void addToIndex(
        RuntimeMatchWS runtimeMatchWS,
        IndexData fieldNamesAndValues) throws ConnectException
    {
        List<String> fieldNames = Lists.newArrayList();
        fieldNames.addAll(fieldNamesAndValues.keySet());

        //while updateSlot sounds like an update, it is actually inserting an entry into the index
        ServiceResult addResponse = runtimeMatchWS.updateSlot(slotName, fieldNames, fieldNamesAndValues.stringValues());

        if(addResponse.isError())
        {
            log.error("Failed to add index: " + addResponse.getMessage());
            throw new WebApplicationException(addResponse.getMessage());
        }
    }

    private void addPersonToIndexWithEmail(
        RuntimeMatchWS runtimeMatchWS,
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        EmailAddress emailAddressToUse) throws ConnectException
    {
        addToIndex(
            runtimeMatchWS,
            generateFieldNamesAndValuesForEmailIndex(person, mdmPerson, emailAddressToUse));
    }

    private void addPersonToIndexWithPhoneNumber(
        RuntimeMatchWS runtimeMatchWS,
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        PhoneNumber phoneNumberToUse) throws ConnectException
    {
        addToIndex(
            runtimeMatchWS,
            generateFieldNamesAndValuesForPhoneNumberIndex(person, mdmPerson, phoneNumberToUse));
    }

    IndexData generateFieldNamesAndValuesForNameAndAddressIndex(
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        Address addressToUse) throws ConnectException
    {
        NameAndAddressIndexData indexData = new NameAndAddressIndexData();

        indexData.putFirstName(person.getFirstName());
        indexData.putLastName(person.getLastName());

        String standardizedFirstName = nicknameService.getStandardizedNickName(person.getFirstName());
        log.info("Using " + standardizedFirstName + " to represent First Name: " + person.getFirstName());
        indexData.putStandardizedFirstName(standardizedFirstName);

        indexData.putPartyId(mdmPerson.getObjectEntity().getPartyId());
        indexData.putGlobalRegistryId(person.getId());

        if(addressToUse != null)
        {
            indexData.putAddressLine1(addressToUse.getAddressLine1());
            indexData.putAddressLine2(addressToUse.getAddressLine2());
            indexData.putCity(addressToUse.getCity());
            indexData.putState(addressToUse.getState());
            indexData.putZipCode(addressToUse.getZipCode());
        }

        return indexData;
    }

    NameAndCommunicationIndexData generateFieldNamesAndValuesForCommunicationIndex(Person person, RealTimeObjectActionDTO mdmPerson)
    {
        NameAndCommunicationIndexData indexData = new NameAndCommunicationIndexData();

        indexData.putFirstName(person.getFirstName());
        indexData.putLastName(person.getLastName());
        indexData.putPartyId(mdmPerson.getObjectEntity().getPartyId());
        indexData.putGlobalRegistryId(person.getId());

        return indexData;
    }

    IndexData generateFieldNamesAndValuesForEmailIndex(
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        EmailAddress emailAddressToUse)
    {
        NameAndCommunicationIndexData indexData = generateFieldNamesAndValuesForCommunicationIndex(person, mdmPerson);
        indexData.putCommunicationData(emailAddressToUse.getEmail());
        return indexData;
    }

    IndexData generateFieldNamesAndValuesForPhoneNumberIndex(
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        PhoneNumber phoneNumberToUse)
    {
        NameAndCommunicationIndexData indexData = generateFieldNamesAndValuesForCommunicationIndex(person, mdmPerson);
        indexData.putCommunicationData(phoneNumberToUse.getNumber());
        return indexData;
    }
}
