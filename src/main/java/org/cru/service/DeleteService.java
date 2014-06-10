package org.cru.service;

import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Service to handle complexity of deleting a {@link Person} from the index
 *
 * Created by William.Randall on 6/10/14.
 */
public class DeleteService
{
    @Inject
    private OpenDQProperties openDQProperties;

    private String slotName;
    private String transformationFileLocation;
    private String step;

    public void deletePerson(String globalRegistryId, String slotName) throws ConnectException
    {
        this.slotName = slotName;
        callRuntimeMatchService(globalRegistryId);
    }

    private void callRuntimeMatchService(String globalRegistryId) throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();

        configureSlot(runtimeMatchWS);
        deleteFromIndex(globalRegistryId);
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS) throws ConnectException
    {
        ServiceResult configureResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, step);

        if(configureResponse.isError())
        {
            throw new RuntimeException(configureResponse.getMessage());
        }
    }

    private void deleteFromIndex(String globalRegistryId)
    {
        //TODO: Decide what to do here: a) Update MDM  b) Maintain a file of deleted indexes
    }

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");
        step = "RtMatch";

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    void setOpenDQProperties(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }
}
