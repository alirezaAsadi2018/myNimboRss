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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
    private ConcurrentHashMap<String, ConfigValue> configMap = new ConcurrentHashMap<>();
    private Config config;
    private HikariConfig hikariConfig;

    public ConfigurationLoader() {
        config = ConfigFactory.load("dbConfig");
        hikariConfig = new HikariConfig();
        for (Map.Entry<String, ConfigValue> es : config.entrySet()) {
            configMap.put(es.getKey(), es.getValue());
        }
    }

    public ConcurrentHashMap<String, ConfigValue> getConfigMap() {
        return configMap;
    }

    public Config getDbConfig() {
        return config;
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public String get(String test) {
        return configMap.get(test).unwrapped().toString();
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
