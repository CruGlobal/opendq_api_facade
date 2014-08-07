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
import org.cru.model.Person;
import org.cru.model.map.IndexData;
import org.cru.qualifiers.Add;
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
    private static final String ACTION = "A";  // A = Add
    private static Logger log = Logger.getLogger(AddService.class);

    @SuppressWarnings("unused")  //used by CDI
    public AddService() {}

    @Inject
    public AddService(OpenDQProperties openDQProperties, AddressNormalizationService addressNormalizationService)
    {
        this.openDQProperties = openDQProperties;
        this.addressNormalizationService = addressNormalizationService;
    }

    public void addPerson(Person person, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        this.stepName = "RtMatchAddr";

        for(Address address : person.getAddresses())
        {
            addressNormalizationService.normalizeAddress(address);
        }

        RealTimeObjectActionDTO addedPerson = addPersonToMdm(person);
        addPersonToIndex(person, addedPerson);
    }

    private RealTimeObjectActionDTO addPersonToMdm(Person person)
    {
        DataManagementWSImpl mdmService = getMdmServiceImplementation();
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
        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService();

        //Handle cases where no address was passed in
        if(person.getAddresses() == null || person.getAddresses().isEmpty())
        {
            addPersonToIndexWithAddress(runtimeMatchWS, person, mdmPerson, null);
        }

        //If more than one address was passed in, add them all to the index
        for(Address personAddress : person.getAddresses())
        {
            addPersonToIndexWithAddress(runtimeMatchWS, person, mdmPerson, personAddress);
        }
    }

    private void addPersonToIndexWithAddress(
        RuntimeMatchWS runtimeMatchWS,
        Person person,
        RealTimeObjectActionDTO mdmPerson,
        Address addressToUse)
    {
        IndexData fieldNamesAndValues = generateFieldNamesAndValues(person, mdmPerson, addressToUse);

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

    IndexData generateFieldNamesAndValues(Person person, RealTimeObjectActionDTO mdmPerson, Address addressToUse)
    {
        IndexData indexData = new IndexData();

        indexData.putFirstName(person.getFirstName());
        indexData.putLastName(person.getLastName());
        indexData.putStandardizedFirstName(person.getFirstName());
        indexData.putPartyId(mdmPerson.getObjectEntity().getPartyId());

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
}
