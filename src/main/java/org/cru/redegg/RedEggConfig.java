package org.cru.redegg;

import com.google.common.collect.ImmutableList;
import org.cru.redegg.reporting.errbit.ErrbitConfig;
import org.cru.util.OafProperties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by William.Randall on 8/19/2014.
 */
public class RedEggConfig
{
    @Inject OafProperties oafProperties;

    @Produces
    public ErrbitConfig createConfig() throws URISyntaxException
    {
        ErrbitConfig errbitConfig = new ErrbitConfig();
        errbitConfig.setEndpoint(new URI(oafProperties.getProperty("redegg.url")));
        errbitConfig.setKey(oafProperties.getProperty("redegg.key"));
        errbitConfig.setEnvironmentName(oafProperties.getProperty("redegg.environment"));

        errbitConfig.getApplicationBasePackages().addAll(
            ImmutableList.of(
                "org.cru.cdi",
                "org.cru.deserialization",
                "org.cru.mdm",
                "org.cru.model",
                "org.cru.postalsoft",
                "org.cru.redegg",
                "org.cru.service",
                "org.cru.util",
                "org.cru.webservices"
            )
        );

        return errbitConfig;
    }
}
