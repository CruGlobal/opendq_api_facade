package org.cru.service;

import org.apache.log4j.Logger;
import org.cru.model.Credentials;
import org.cru.model.CredentialsPK;
import org.cru.qualifiers.Oaf;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service to handle Authentication of client systems
 * Created by William.Randall on 8/8/2014.
 */
public class AuthService
{
    @Inject @Oaf
    EntityManager entityManager;
    private static Logger log = Logger.getLogger(AuthService.class);

    public boolean hasAccess(HttpHeaders httpHeaders)
    {
        return isAuthenticated(httpHeaders);
    }

    public Response notAuthorized(HttpHeaders httpHeaders)
    {
        String accessToken = getAccessToken(httpHeaders);
        String systemName = null;

        if(accessToken != null)
        {
            int separator = getSeparatorIndex(accessToken);
            if (separator > 0)
            {
                systemName = accessToken.substring(0, separator);
            }
        }

        log.warn("Unauthorized access attempt to matching service.  System name: " + systemName);
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("You do not have access to this service")
            .build();
    }

    private boolean isAuthenticated(HttpHeaders httpHeaders)
    {
        return validateAccessToken(getAccessToken(httpHeaders));
    }

    private String getAccessToken(HttpHeaders httpHeaders)
    {
        List<String> authorizationHeaders = httpHeaders.getRequestHeader("Authorization");
        if(authorizationHeaders == null || authorizationHeaders.isEmpty()) return null;

        String authorizationHeader = authorizationHeaders.get(0);
        if (authorizationHeader != null && authorizationHeader.trim().startsWith("Bearer "))
        {
            return authorizationHeader.trim().substring("Bearer ".length());
        }
        return null;
    }

    private boolean validateAccessToken(String accessToken)
    {
        int separator = getSeparatorIndex(accessToken);
        if(separator == -1) return false;

        String systemName = accessToken.substring(0, separator);
        String providedKey = accessToken.substring(separator + 1);

        CredentialsPK credentialsKey = new CredentialsPK();
        credentialsKey.setSystemKey(providedKey);
        credentialsKey.setSystemName(systemName);

        Credentials foundCredentials = entityManager.find(Credentials.class, credentialsKey);

        return foundCredentials != null && providedKey.equals(foundCredentials.getCredentialsPrimaryKey().getSystemKey());
    }

    private int getSeparatorIndex(String accessToken)
    {
        if(accessToken == null) return -1;

        int separator = accessToken.indexOf('_');
        if (separator == -1 || separator == 0)
        {
            return -1;
        }

        return separator;
    }
}
