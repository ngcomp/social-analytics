package com.ngcomp.analytics.engine.util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * TumblerUser: rparashar
 * Date: 7/20/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropUtils {

    private static final Logger logger = Logger.getLogger(PropUtils.class);

    private static PropUtils instance;

    private static Properties props;

    /**
     * @param configFilePath
     * @throws java.io.IOException
     */
    static
    {
        ClassPathResource resource = new ClassPathResource("config.properties");
        try {
            instance = new PropUtils(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PropUtils(InputStream inputStream) throws IOException
    {
        Properties p = new Properties();
        try
        {
            p.load(inputStream);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
        props = p;
    }

    public static String getModalFile() {
        return props.getProperty("model_file").trim();
    }

    public static String getStopWordFile() {
        return props.getProperty("stop_words_file").trim();
    }

    public static String getSpanishStopWordFile() {
        return props.getProperty("spanish_stop_words_file").trim();
    }



    /**
     * @param prop
     * @param defaultVal
     * @return
     */
    public static String getVal(String prop, String defaultVal) {
        if (props.containsKey(prop)) {
            return props.getProperty(prop, defaultVal).trim();
        } else {
            return defaultVal;
        }
    }

    public static String getVal(String prop) {
        if (props.containsKey(prop)) {
            return (String)props.getProperty(prop).trim();
        }else{
            return null;
        }
    }


}