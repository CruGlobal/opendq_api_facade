package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImplService;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.util.OpenDQProperties;

import javax.ws.rs.WebApplicationException;
import java.net.ConnectException;

/**
 * Parent service for holding common functionality between
 * {@link AddService} and {@link MatchingService}
 *
 * Created by William.Randall on 6/25/14.
 */
public class IndexingService
{
    OpenDQProperties openDQProperties;
    String slotName;
    String transformationFileLocation;
    String stepName;

    RuntimeMatchWS callRuntimeMatchService() throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = configureRuntimeService();
        configureSlot(runtimeMatchWS);
        return runtimeMatchWS;
    }

    private void configureSlot(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, stepName);

        if(configurationResponse.isError())
        {
            throw new WebApplicationException(configurationResponse.getMessage());
        }
    }

    private RuntimeMatchWS configureRuntimeService()
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");

        RuntimeMatchWSService runtimeMatchWSService = new RuntimeMatchWSService();
        return runtimeMatchWSService.getRuntimeMatchWSPort();
    }

    DataManagementWSImpl configureMdmService()
    {
        DataManagementWSImplService mdmService = new DataManagementWSImplService();
        return mdmService.getDataManagementWSImplPort();
    }
}
