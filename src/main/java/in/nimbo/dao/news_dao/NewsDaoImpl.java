package in.nimbo.dao.news_dao;

import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Filter;
import in.nimbo.News;
import in.nimbo.SearchFilters;
import in.nimbo.exception.NewsDaoException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {
    private final HikariDataSource dataSource;
    private String tableName;


    public NewsDaoImpl(String tableName, HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }


    @Override
    public List<News> search(SearchFilters searchFilters) throws NewsDaoException {
        StringBuilder sql = new StringBuilder("select * from " + tableName + " where ");
        for (Filter value : Filter.values()) {
            for (String filterText : searchFilters.getFilterTexts(value)) {
                sql.append(value.toString()).append(" like ").append("\"%").append(filterText).append("%\"");
                sql.append(" and ");
            }
        }
        sql.delete(sql.length() - 5, sql.length());//deleting last "and"
        Connection conn = getConnection();
        try (Statement s = conn.createStatement()) {
            return getResultsFromResultSet(s.executeQuery(sql.toString()));
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    public Connection getConnection() throws NewsDaoException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new NewsDaoException(e);
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
            throw new NewsDaoException(e);
        }
    }

    public List<News> getResultsFromResultSet(ResultSet resultSet) throws NewsDaoException {
        try {
            List<News> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new News(resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getString("link"), resultSet.getString("agency"), resultSet.getDate("date")));
            }
            return list;
        } catch (SQLException e) {

            throw new NewsDaoException(e);
        }
    }

    @Override
    public void insert(News news) throws NewsDaoException {
        String sql = "insert into " + tableName + " (title, dscp, link, agency, date) values (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (newsExist(news)) {
                return;
            }
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getDescription());
            ps.setString(3, news.getLink());
            ps.setString(4, news.getAgency());
            ps.setTimestamp(5, new Timestamp(news.getDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    @Override
    public boolean newsExist(News news) throws NewsDaoException {
        String sql = "select count(*) as cnt from " + tableName + " where title = ? and date = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setTimestamp(2, new Timestamp(news.getDate().getTime()));
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("cnt") > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }

    }

}
