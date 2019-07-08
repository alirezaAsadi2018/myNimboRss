package in.nimbo.news_dao;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Configuration;
import in.nimbo.News;
import in.nimbo.Search;
import in.nimbo.exception.NewsDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {
    private static final Logger logger = LoggerFactory.getLogger(NewsDaoImpl.class);
    private String tableName;
    private HikariConfig hikariConfig;
    private Search search;
    private HikariDataSource ds;

    /**
     * this constructor is created because a no-arg constructor is necessary for mock junit testing
     */
    public NewsDaoImpl() {
    }

    public NewsDaoImpl(Configuration configuration, HikariDataSource ds, Search search) {
        this.ds = ds;
        Config config = configuration.getDbConfig();
        this.search = search;
        tableName = config.getString("newsTable.tableName");
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public HikariDataSource getDs() {
        return ds;
    }

    public String getTableName() {
        return tableName;
    }

    public Connection getConnection() throws NewsDaoException {
        try {
            Connection conn = ds.getConnection();
            return conn;
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<News> search() throws NewsDaoException {
        Connection conn = getConnection();
        try (Statement s = conn.createStatement()) {
            return getResultsFromResultSet(s.executeQuery(search.getSql()));
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<News> getNews() throws NewsDaoException {
        String sql = "select * from " + tableName;
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                return getResultsFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    public List<News> getResultsFromResultSet(ResultSet resultSet) throws NewsDaoException {
        try {
            List<News> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new News(resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), new java.util.Date(((Timestamp) resultSet.getObject(6)).getTime())));
            }
            return list;
        } catch (SQLException e) {

            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public void insertCandidate(News news) throws NewsDaoException {
        String sql = "insert into " + tableName + " (title, dscp, link, agency, date) values (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (candidateExists(news)) {
                return;
            }
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getDscp());
            ps.setString(3, news.getLink());
            ps.setString(4, news.getAgency());
            ps.setDate(5, new Date(news.getDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public boolean candidateExists(News news) throws NewsDaoException {
        String sql = "select count(*) as cnt from " + tableName + " where title = ? and date = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setDate(2, new java.sql.Date(news.getDate().getTime()));
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("cnt") > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }

    }

}
