package in.nimbo.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.ConfigurationLoader;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnPool {
    private HikariDataSource ds;

    public ConnPool(ConfigurationLoader cofig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(cofig.get("db.url"));
        hikariConfig.setUsername(cofig.get("db.username"));
        hikariConfig.setPassword(cofig.get("db.password"));
        hikariConfig.setDataSourceProperties(cofig.getDsProp());
//        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
//        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("minimumIdle", 2048);
        hikariConfig.addDataSourceProperty("maximumPoolSize", 2048);
//        hikariConfig.setMinimumIdle(20);
//        hikariConfig.setMaximumPoolSize(20);
        this.ds = new HikariDataSource(hikariConfig);
    }

    public HikariDataSource getDs() {
        return ds;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
