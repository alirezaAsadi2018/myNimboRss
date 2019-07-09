package in.nimbo.dao.url_dao;

import in.nimbo.dao.ConnPool;
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
    private final ConnPool pool;
    private String tableName;


    public UrlDaoImpl(String tableName, ConnPool pool) {
        this.pool = pool;
        this.tableName = tableName;
    }

    public Connection getConnection() throws UrlDaoException {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new UrlDaoException(e.getMessage(), e);
        }
    }


    @Override
    public void insertUrl(URL url) throws UrlDaoException {
        if (urlExists(url))
            return;
        String sql = "insert into " + tableName + "(url) values (?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            throw new UrlDaoException(e.getMessage(), e);
        }
    }

    private boolean urlExists(URL url) throws UrlDaoException {
        String sql = "select count(*) as cnt from " + tableName + " where url = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
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
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            logger.error("error with connection to the database and making connection pools");
            throw new UrlDaoException(e.getMessage(), e);
        }
    }
}
