package org.cru.util;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is a class to make PostalsoftService.properties more accessible and injectable
 *
 * Created by William.Randall on 6/6/14.
 */
@ApplicationScoped
public class PostalsoftServiceProperties
{
    private static Properties properties;
    private static Logger log = Logger.getLogger(PostalsoftServiceProperties.class);

    public PostalsoftServiceProperties() {}

    @PostConstruct
    public void init()
    {
        properties = new Properties();

        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/PostalsoftService.properties");
            try
            {
                properties.load(inputStream);
            }
            finally
            {
                inputStream.close();
            }
        }
        catch(IOException ioe)
        {
            log.error("Problem loading PostalsoftService.properties", ioe);
        }
    }

    public String getProperty(String propertyName)
    {
        if(properties == null) throw new IllegalStateException("Properties file not loaded!");
        if(Strings.isNullOrEmpty(propertyName)) return null;
        return properties.getProperty(propertyName);
    }
}
