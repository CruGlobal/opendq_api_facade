package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImplService;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.cru.util.OpenDQProperties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

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
        QName qName = new QName(openDQProperties.getProperty("mdm.namespaceURI"), openDQProperties.getProperty("mdm.serviceName"));
        URL wsdlUrl;

        try
        {
            wsdlUrl = new URL(openDQProperties.getProperty("mdm.wsdlUrl"));
        }
        catch(MalformedURLException e)
        {
            throw new WebApplicationException(Response.serverError().entity(e.getMessage()).build());
        }

        DataManagementWSImplService mdmService = new DataManagementWSImplService(wsdlUrl, qName);
        return mdmService.getDataManagementWSImplPort();
    }
}
