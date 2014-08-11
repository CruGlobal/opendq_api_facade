package org.cru.service;

import org.cru.model.Credentials;
import org.cru.model.CredentialsPK;
import org.cru.qualifiers.Oaf;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

/**
 * Service to handle Authentication of client systems
 * Created by William.Randall on 8/8/2014.
 */
public class AuthService
{
    @Inject @Oaf
    EntityManager entityManager;

    public boolean hasAccess(HttpHeaders httpHeaders)
    {
        return isAuthenticated(httpHeaders);
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
        if(accessToken == null) return false;

        int separator = accessToken.indexOf('_');
        if (separator == -1 || separator == 0)
        {
            return false;
        }

        String systemName = accessToken.substring(0, separator);
        String providedKey = accessToken.substring(separator + 1);

        CredentialsPK credentialsKey = new CredentialsPK();
        credentialsKey.setSystemKey(providedKey);
        credentialsKey.setSystemName(systemName);

        Credentials foundCredentials = entityManager.find(Credentials.class, credentialsKey);

        return foundCredentials != null && providedKey.equals(foundCredentials.getCredentialsPrimaryKey().getSystemKey());
    }
}
