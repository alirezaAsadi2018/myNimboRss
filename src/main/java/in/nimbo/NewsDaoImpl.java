package in.nimbo;

import com.mysql.cj.log.Slf4JLogger;
import in.nimbo.Exp.DBNotExistsExp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class NewsDaoImpl implements NewsDao {
    private static final Slf4JLogger logger = new Slf4JLogger("NewsDaoImpl");
    private static NewsDaoImpl instance;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String dbTableName;
    private Connection conn;

    private NewsDaoImpl() throws DBNotExistsExp {
        try(InputStream stream = new FileInputStream("src/main/resources/config.properties")){
            Properties dbProperties = new Properties();
            dbProperties.load(stream);
            dbName = dbProperties.getProperty("db.name");
            dbTableName = dbProperties.getProperty("db.tableName");
            String url = dbProperties.getProperty("db.url");
            conn = DriverManager.getConnection(url, dbProperties.getProperty("db.username"), dbProperties.getProperty("db.password"));
        } catch (SQLException e) {
            throw new DBNotExistsExp("database doesn't exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the only instance of this singletone class
     *
     * @return the only instance of this class
     */
    public static NewsDaoImpl getInstance() throws DBNotExistsExp {
        if (instance == null)
            return new NewsDaoImpl();
        return instance;
    }

    @Override
    public List<News> searchByTitle(String title) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName + " where title like  ? ");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, "%" + title + "%");
            List<News> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new News(resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), (Date) resultSet.getObject(5)));
            }
            return list;
        }
    }

    @Override
    public List<News> getNews() throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<News> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new News(resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), new java.util.Date(((Timestamp) resultSet.getObject(5)).getTime())));
            }
            return list;
        }
    }

    @Override
    public void insertCandidate(News news) throws SQLException {
        if (candidateExists(news)) {
            return;
        }
        PreparedStatement preparedStatement = conn.prepareStatement("insert into " + dbTableName + " (title, dscp, agency, dt) values (?, ?, ?, ?)");
        preparedStatement.setString(1, news.getTitle());
        preparedStatement.setString(2, news.getDscp());
        preparedStatement.setString(3, news.getAgency());
        preparedStatement.setDate(4, new java.sql.Date(news.getDt().getTime()));
        preparedStatement.executeUpdate();
    }

    @Override
    public boolean candidateExists(News news) throws SQLException {
        String sql = "select * from " + dbTableName + " where title = ? and dt = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, news.getTitle());
        preparedStatement.setDate(2, new java.sql.Date(news.getDt().getTime()));
        return preparedStatement.execute();

    }


    public void close() throws SQLException {
        conn.close();
    }
}
