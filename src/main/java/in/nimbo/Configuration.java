package in.nimbo;


import java.io.File;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration {
    private Config dbConfig;

    public Configuration() {
        dbConfig = ConfigFactory.load(ConfigFactory.parseFile(new File("src/main/resources/dbConfig.properties")));
    }

    public Config getDbConfig() {
        return dbConfig;
    }

}
