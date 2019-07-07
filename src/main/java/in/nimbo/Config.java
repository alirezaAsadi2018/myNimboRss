package in.nimbo;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private String dbTableName;
    private String user;
    private String pass;
    private String url;

    public Config() {
        init();
    }


    public String getDbTableName() {
        return dbTableName;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getUrl() {
        return url;
    }


    public void init() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(Thread.currentThread().getContextClassLoader()
                    .getResource("dbConfig.properties").getPath()));
        } catch (FileNotFoundException e) {
            logger.error("fileReader cannot open the config file ", e);
        } catch (IOException e) {
            logger.error(" An error occurred when reading from the input stream ", e);
        }

        dbTableName = properties.getProperty("db.tableName");
        user = properties.getProperty("db.username");
        pass = properties.getProperty("db.password");
        url = properties.getProperty("db.url");
    }
}
