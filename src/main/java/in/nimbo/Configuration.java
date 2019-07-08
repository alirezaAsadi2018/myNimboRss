package in.nimbo;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;

import java.io.File;

public class Configuration {
    private Config dbConfig;
    private HikariConfig hikariConfig;

    public Configuration() {
        dbConfig = ConfigFactory.load(ConfigFactory.parseFile(new File("src/main/resources/dbConfig.properties")));
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbConfig.getString("db.url"));
        hikariConfig.setUsername(dbConfig.getString("db.username"));
        hikariConfig.setPassword(dbConfig.getString("db.password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(20);
    }

    public Config getDbConfig() {
        return dbConfig;
    }
    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

}
