package org.cru.util;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is a class to make opendq.properties more accessible and injectable
 *
 * Created by William.Randall on 6/9/14.
 */
public class OpenDQProperties
{
    private static Properties properties;
    private static Logger log = Logger.getLogger(OpenDQProperties.class);

    public OpenDQProperties() {}

    @PostConstruct
    public void init()
    {
        properties = new Properties();

        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/opendq.properties");
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
            log.error("Problem loading opendq.properties", ioe);
        }
    }

    public String getProperty(String propertyName)
    {
        if(properties == null) throw new IllegalStateException("Properties file not loaded!");
        if(Strings.isNullOrEmpty(propertyName)) return null;
        return properties.getProperty(propertyName);
    }
}
