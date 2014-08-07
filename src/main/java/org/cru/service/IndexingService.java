package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImplService;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWSService;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import org.apache.log4j.Logger;
import org.cru.util.OpenDQProperties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
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
    private static Logger log = Logger.getLogger(IndexingService.class);

    RuntimeMatchWS configureAndRetrieveRuntimeMatchService() throws ConnectException
    {
        RuntimeMatchWS runtimeMatchWS = getRuntimeServiceImplementation();
        configureRuntimeMatchService(runtimeMatchWS);
        return runtimeMatchWS;
    }

    private void configureRuntimeMatchService(RuntimeMatchWS runtimeMatchWS)
    {
        ServiceResult configurationResponse = runtimeMatchWS.configureSlot(slotName, transformationFileLocation, stepName);

        if(configurationResponse.isError())
        {
            log.error("Failed to configure index: " + configurationResponse.getMessage());
            throw new WebApplicationException(configurationResponse.getMessage());
        }
    }

    private RuntimeMatchWS getRuntimeServiceImplementation()
    {
        return ((RuntimeMatchWSService) getServiceImplementation("runtime", RuntimeMatchWSService.class)).getRuntimeMatchWSPort();
    }

    DataManagementWSImpl getMdmServiceImplementation()
    {
        return ((DataManagementWSImplService) getServiceImplementation("mdm", DataManagementWSImplService.class)).getDataManagementWSImplPort();
    }

    Service getServiceImplementation(String serviceName, Class serviceImplType)
    {
        transformationFileLocation = openDQProperties.getProperty("transformationFileLocation");
        QName qName = new QName(
            openDQProperties.getProperty(serviceName + ".namespaceURI"),
            openDQProperties.getProperty(serviceName + ".serviceName"));
        URL wsdlUrl;

        try
        {
            wsdlUrl = new URL(openDQProperties.getProperty(serviceName + ".wsdlUrl"));
        }
        catch(MalformedURLException e)
        {
            log.error("Bad WSDL URL", e);
            throw new WebApplicationException(Response.serverError().entity(e.getMessage()).build());
        }

        if(serviceImplType.equals(DataManagementWSImplService.class)) return new DataManagementWSImplService(wsdlUrl, qName);
        else if(serviceImplType.equals(RuntimeMatchWSService.class)) return new RuntimeMatchWSService(wsdlUrl, qName);

        log.error("Invalid Service: " + serviceImplType.getSimpleName());
        throw new IllegalArgumentException(serviceImplType.getSimpleName() + " is not a valid service");
    }
}
