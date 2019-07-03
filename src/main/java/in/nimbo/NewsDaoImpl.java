package in.nimbo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import in.nimbo.Exp.DBNotExistsExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {
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
    public static NewsDaoImpl getInstance() throws DBNotExistsExp {
        if (instance == null)
            instance = new NewsDaoImpl();
        try {
            Config defaultConfig = ConfigFactory.parseFile(new File("src/main/resources/dbConfig.conf"));
            instance.dbTableName = defaultConfig.getString("db.tableName");
            String url = defaultConfig.getString("db.url");
            instance.conn = DriverManager.getConnection(url, defaultConfig.getString("db.username"), defaultConfig.getString("db.password"));
        } catch (SQLException e) {
            throw new DBNotExistsExp("database doesn't exist");
        }
        return instance;
    }

    @Override
    public List<News> searchByTitle(String title) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName + " where title like  ? ");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, "%" + title + "%");
            return getResultsFromResultSet(resultSet);
        }
    }

    @Override
    public List<News> search(String request) throws SQLException {
        switch (request){
            case "title":

                break;
            case "date":

                break;
            case "dscp":

                break;
            case "link":

                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public List<News> getNews() throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return getResultsFromResultSet(resultSet);
        }
    }

    private List<News> getResultsFromResultSet(ResultSet resultSet) throws SQLException {
        List<News> list = new LinkedList<>();
        while (resultSet.next()) {
            list.add(new News(resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4), new java.util.Date(((Timestamp) resultSet.getObject(5)).getTime())));
        }
        return list;
    }



    @Override
    public void insertCandidate(News news) throws SQLException {
        if (candidateExists(news)) {
            return;
        }
        PreparedStatement preparedStatement = conn.prepareStatement("insert into " + dbTableName + " (title, dscp, link, dt) values (?, ?, ?, ?)");
        preparedStatement.setString(1, news.getTitle());
        preparedStatement.setString(2, news.getDscp());
        preparedStatement.setString(3, news.getLink());
        preparedStatement.setDate(4, new java.sql.Date(news.getDt().getTime()));
        preparedStatement.executeUpdate();
    }

    @Override
    public boolean candidateExists(News news) throws SQLException {
        String sql = "select count(*) as cnt from " + dbTableName + " where title = ? and dt = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, news.getTitle());
        preparedStatement.setDate(2, new java.sql.Date(news.getDt().getTime()));
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getInt("cnt") > 0;
        }
        return true;

    }


    public void close() throws SQLException {
        conn.close();
    }
}
