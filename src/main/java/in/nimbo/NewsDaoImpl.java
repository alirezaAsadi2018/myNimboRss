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
            Config defaultConfig = ConfigFactory.parseFile(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()));
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
    public List<News> search(String request, String text) throws SQLException {
        String sql;
        switch (request){
            case "title":
                sql = "select * from " + dbTableName + " where title like  ? ";
                break;
            case "date":
                sql = "select * from " + dbTableName + " where date like  ? ";
                break;
            case "dscp":
                sql = "select * from " + dbTableName + " where dscp like  ? ";
                break;
            case "link":
                sql = "select * from " + dbTableName + " where link like  ? ";
                break;
            case "agency":
                sql = "select * from " + dbTableName + " where agency like  ? ";
                break;
            default:
                throw new IllegalArgumentException("wrong query is requested to be searched!");
        }
        try(PreparedStatement ps  = conn.prepareStatement(sql)){
            ps.setString(1, "%" + text + "%");
            return getResultsFromResultSet(ps.executeQuery());
        }
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
                    resultSet.getString(4), resultSet.getString(5), new java.util.Date(((Timestamp) resultSet.getObject(6)).getTime())));
        }
        return list;
    }



    @Override
    public void insertCandidate(News news) throws SQLException {
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
    }

    @Override
    public boolean candidateExists(News news) throws SQLException {
        String sql = "select count(*) as cnt from " + dbTableName + " where title = ? and date = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, news.getTitle());
        preparedStatement.setDate(2, new java.sql.Date(news.getDate().getTime()));
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getInt("cnt") > 0;
        }
        return true;

    }

    public void createTableIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + dbTableName + "(\n"
                + "	id integer PRIMARY KEY auto_increment,\n"
                + "	title text, dscp text, agency text, dt datetime);";
        statement.execute(sql);
    }

    public void close() throws SQLException {
        conn.close();
    }
}
