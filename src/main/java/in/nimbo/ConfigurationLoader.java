package in.nimbo;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    private Config config;
    private static final String[] configFileNamesToLoad = {"dbConfig", "dsConfig"};

    public ConfigurationLoader() {

    }

    public Config getDbConfig() {
        return config;
    }


    public String get(String test) {
        return config.getString(test);
    }

    public Properties getDsProp() {
        ClassLoader classLoader = getClass().getClassLoader();
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File(classLoader.getResource("dsConfig.properties").getFile())));
            return properties;
        } catch (IOException e) {
            logger.error("cannot load properties file for data source properties in resources of main directory", e);
        }
        return null;
    }
}
