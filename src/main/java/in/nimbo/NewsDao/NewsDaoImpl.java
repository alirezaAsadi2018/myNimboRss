package in.nimbo.NewsDao;

import in.nimbo.ConfigsInCommon;
import in.nimbo.News;
import in.nimbo.exception.NewsDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.sql.ResultSet;

public class NewsDaoImpl implements NewsDao, ConfigsInCommon {
    private static final Logger logger = LoggerFactory.getLogger(NewsDaoImpl.class);
    private static NewsDaoImpl instance;
    private String dbTableName;
    private Connection conn;


    private NewsDaoImpl() {

    }

    /**
     * get the only instance of this singletone class
     *
     * @return the only instance of this class
     */
    public static NewsDaoImpl getInstance() {
        if (instance == null) {
            instance = new NewsDaoImpl();
            try {
                instance.dbTableName = config.getDbTableName();
                instance.conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass());
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return instance;
    }

    @Override
    public List<News> search(String request, String text) throws NewsDaoException {
        String sql;
        switch (request) {
            case "title":
                sql = "select * from " + dbTableName + " where title like  ? ";
                break;
            case "date":
                sql = "select * from " + dbTableName + " where date like  ? ";
                break;
            case "dscp":
                sql = "select * from " + dbTableName + " where dscp like  ? ";
                break;
            case "agency":
                sql = "select * from " + dbTableName + " where agency like  ? ";
                break;
            default:
                throw new IllegalArgumentException("wrong query is requested to be searched!");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + text + "%");
            return getResultsFromResultSet(ps.executeQuery());
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<News> getNews() throws NewsDaoException {
        try (PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return getResultsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    private List<News> getResultsFromResultSet(ResultSet resultSet) throws NewsDaoException {
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
        try {
            if (candidateExists(news)) {
                return;
            }
            PreparedStatement preparedStatement = conn.prepareStatement("insert into " + dbTableName + " (title, dscp, link, agency, date) values (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, news.getTitle());
            preparedStatement.setString(2, news.getDscp());
            preparedStatement.setString(3, news.getLink());
            preparedStatement.setString(4, news.getAgency());
            preparedStatement.setDate(5, new java.sql.Date(news.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }
    }

    @Override
    public boolean candidateExists(News news) throws NewsDaoException {
        try {
            String sql = "select count(*) as cnt from " + dbTableName + " where title = ? and date = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, news.getTitle());
            preparedStatement.setDate(2, new java.sql.Date(news.getDate().getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("cnt") > 0;
            }
            return true;
        } catch (SQLException e) {
            throw new NewsDaoException(e.getMessage(), e);
        }

    }

}
