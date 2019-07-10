package in.nimbo.dao.url_dao;

import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.exception.UrlDaoException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UrlDaoImpl implements UrlDao {
    private final HikariDataSource dataSource;
    private final String tableName;


    public UrlDaoImpl(String tableName, HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }


    @Override
    public void insertUrl(URL url) throws UrlDaoException {
        if (urlExists(url))
            return;
        String sql = "insert into " + tableName + "(url) values (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            throw new UrlDaoException(e);
        }
    }

    private boolean urlExists(URL url) throws UrlDaoException {
        String sql = "select count(*) as cnt from " + tableName + " where url = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("cnt") > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new UrlDaoException(e);
        }
    }

    @Override
    public List<URL> getUrls() throws UrlDaoException {
        String sql = "select * from " + tableName;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {
            List<URL> list = new LinkedList<>();
            if (resultSet.next()) {
                list.add(new URL(resultSet.getString(2)));
            }
            return list;
        } catch (SQLException | MalformedURLException e) {
            throw new UrlDaoException(e);
        }
    }

    @Override
    public void deleteUrl(URL url) throws UrlDaoException {
        if (!urlExists(url))
            return;
        String sql = "delete from " + tableName + " where url = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.toString());
            ps.execute();
        } catch (SQLException e) {
            throw new UrlDaoException(e);
        }
    }
}
