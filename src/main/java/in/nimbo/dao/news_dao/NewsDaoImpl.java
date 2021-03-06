package in.nimbo.dao.news_dao;

import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Filter;
import in.nimbo.News;
import in.nimbo.SearchFilter;
import in.nimbo.exception.NewsDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {
    private static final Logger logger = LoggerFactory.getLogger(NewsDao.class);
    private final HikariDataSource dataSource;
    private final String TABLENAME;


    public NewsDaoImpl(String TABLENAME, HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.TABLENAME = TABLENAME;
    }


    @Override
    public List<News> search(SearchFilter searchFilter) throws NewsDaoException {
        StringBuilder sql = new StringBuilder("select * from " + TABLENAME + " where ");
        for (Filter value : Filter.values()) {
            for (String filterText : searchFilter.getFilterTexts(value)) {
                sql.append(value.toString()).append(" like ").append("\'%").append(filterText).append("%\'");
                sql.append(" and ");
            }
        }
        sql.delete(sql.length() - 5, sql.length());//deleting last additional "and"
        Connection conn = getConnection();
        try (PreparedStatement s = conn.prepareStatement(sql.toString())) {
            return getResultsFromResultSet(s.executeQuery());
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    private Connection getConnection() throws NewsDaoException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    @Override
    public List<News> getNews() throws NewsDaoException {
        String sql = "select * from " + TABLENAME;
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                return getResultsFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    private List<News> getResultsFromResultSet(ResultSet resultSet) throws NewsDaoException {
        try {
            List<News> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new News(resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("link"),
                        resultSet.getString("agency"),
                        resultSet.getTimestamp("date")));
            }
            return list;
        } catch (SQLException e) {

            throw new NewsDaoException(e);
        }
    }

    public void insertAllNews(List<News> allNews) {
        for (News news : allNews) {
            try {
                insertNews(news);
            } catch (NewsDaoException e) {
                logger.error("one of the newsFeeds from link " + news.getLink() + "can't be added to database", e);
            }
        }
    }

    @Override
    public void insertNews(News news) throws NewsDaoException {
        String sql = "insert into " + TABLENAME + " (title, description, link, agency, date) values (?, ?, ?, ?, ?);";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (newsExist(news)) {
                return;
            }
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getDescription());
            ps.setString(3, news.getLink());
            ps.setString(4, news.getAgency());
            ps.setTimestamp(5, news.getDate());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }
    }

    @Override
    public boolean newsExist(News news) throws NewsDaoException {
        String sql = "select count(*) as cnt from " + TABLENAME + " where link = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getLink());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.first()) {
                    return resultSet.getInt("cnt") > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            throw new NewsDaoException(e);
        }

    }

}
