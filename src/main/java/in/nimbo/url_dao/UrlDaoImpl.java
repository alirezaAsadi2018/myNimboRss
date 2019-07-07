package in.nimbo.url_dao;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Configuration;
import in.nimbo.exception.UrlDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UrlDaoImpl implements UrlDao {
    private static final Logger logger = LoggerFactory.getLogger(UrlDaoImpl.class);
    private String tableName;
    private HikariConfig hikariConfig;


    public UrlDaoImpl(Configuration configuration) {
        Config config = configuration.getDbConfig();
        tableName = config.getString("urlTable.tableName");
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getString("db.url"));
        hikariConfig.setUsername(config.getString("db.username"));
        hikariConfig.setPassword(config.getString("db.password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(20);
    }

    @Override
    public void insertUrl(URL url) throws UrlDaoException {
        if (urlExists(url))
            return;
        String sql = "insert into " + tableName + "(url) values (?)";
        try (HikariDataSource ds = new HikariDataSource(hikariConfig);
             Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            throw new UrlDaoException(e.getMessage(), e);
        }
    }

    private boolean urlExists(URL url) throws UrlDaoException {
        String sql = "select count(*) as cnt from " + tableName + " where url = ?";
        try (HikariDataSource ds = new HikariDataSource(hikariConfig);
             Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("cnt") > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new UrlDaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<URL> getUrls() throws UrlDaoException {
        String sql = "select * from " + tableName;
        try (HikariDataSource ds = new HikariDataSource(hikariConfig);
             Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {
            List<URL> list = new LinkedList<>();
            if (resultSet.next()) {
                list.add(new URL(resultSet.getString(1)));
            }
            return list;
        } catch (SQLException e) {
            logger.error("error with connection to the database and making connection pools");
            throw new UrlDaoException(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.error("the url read from the database is not well-formed");
            throw new UrlDaoException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteUrl(URL url) throws UrlDaoException {
        if (!urlExists(url))
            return;
        String sql = "delete from " + tableName + " where url = ?";
        try (HikariDataSource ds = new HikariDataSource(hikariConfig);
             Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            logger.error("error with connection to the database and making connection pools");
            throw new UrlDaoException(e.getMessage(), e);
        }
    }
}
