package org.cru.redegg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.cru.redegg.recording.api.ParameterSanitizer;

import java.util.List;
import java.util.Set;

/**
 * Created by William.Randall on 8/19/2014.
 */
public class OafRedeggSanitizer implements ParameterSanitizer
{

    @Override
    public List<String> sanitizeQueryStringParameter(String parameterName, List<String> parameterValues)
    {
        return sanitize(parameterName, parameterValues);
    }

    @Override
    public List<String> sanitizePostBodyParameter(String parameterName, List<String> parameterValues)
    {
        return sanitize(parameterName, parameterValues);
    }

    @Override
    public List<String> sanitizeHeader(String headerName, List<String> headerValues)
    {
        if (headerName.equalsIgnoreCase("Authorization"))
            return sanitizeAuthorizationHeader(headerValues);
        else
            return headerValues;
    }

    private List<String> sanitize(String parameterName, List<String> parameterValues)
    {
        if (parameterName.equals("access_token"))
            return ImmutableList.of("<removed>");
        else
            return parameterValues;
    }

    private List<String> sanitizeAuthorizationHeader(List<String> headerValues)
    {
        String value = headerValues.get(0);

        Set<String> knownSchemes = ImmutableSet.of("Bearer");
        for (String scheme : knownSchemes)
        {
            String prefix = scheme + " ";
            if (value.startsWith(prefix))
            {
                return sanitizeAuthorizationValue(value, prefix);
            }
        }
        return ImmutableList.of("<removed>");
    }

    private List<String> sanitizeAuthorizationValue(String value, String prefix)
    {
        String token = value.substring(prefix.length());
        String sanitizedToken = token.substring(0, 5) + "<removed>";
        return ImmutableList.of(prefix + sanitizedToken);
    }
}
