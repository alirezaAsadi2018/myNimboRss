package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;
import org.h2.tools.Server;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DaoImpl implements Dao {
    private static DaoImpl instance;
    private static DaoImpl testInstance;
    private static String hostName = "localhost";
    private static String port = "3306";
    private static String dbName = "news";
    private static String dbTableName = "news_table";
    private static String dbUser = "admin";
    private static String dbPassword = "admin";
    private Connection conn;


    private DaoImpl(boolean test) throws SQLException {
        if (test) {
            String url = "jdbc:h2:~/" + dbName;
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            Statement statement = conn.createStatement();
            createTableIfNotExists(statement);
        } else {
            String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            Statement statement = conn.createStatement();
            createTableIfNotExists(statement);
        }
    }

    /**
     * get the only instance of this singletone class
     *
     * @return the only instance of this class
     */
    public static DaoImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new DaoImpl(false);
        }
        return instance;
    }

    public static DaoImpl getTestInstance() throws SQLException {
        if (testInstance == null) {
            testInstance = new DaoImpl(true);
        }
        return testInstance;
    }

    public void createTableIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + dbTableName + "(\n"
                + "	id integer PRIMARY KEY auto_increment,\n"
                + "	title text, dscp text, agency text, dt datetime);";
        statement.execute(sql);
    }

    @Override
    public List<POJO> searchByTitle(String title) throws SQLException {
        Statement statement = conn.createStatement();
        PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName + " where title like  ? ");
        preparedStatement.setString(1, "%" + title + "%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<POJO> list = new LinkedList<>();
        while (resultSet.next()) {
            list.add(new POJO(resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4), (Date) resultSet.getObject(5)));
        }
        return list;
    }

    @Override
    public List<POJO> getNews() throws SQLException {
        Statement statement = conn.createStatement();
        PreparedStatement preparedStatement = conn.prepareStatement("select * from " + dbTableName);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<POJO> list = new LinkedList<>();
        while (resultSet.next()) {
            list.add(new POJO(resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4), (Date) resultSet.getObject(5)));
        }
        return list;
    }

    @Override
    public void insertCandidate(POJO pojo) throws SQLException {
        if (candidateExists(pojo)) {
            return;
        }
        PreparedStatement preparedStatement = conn.prepareStatement("insert into " + dbTableName + " (title, dscp, agency, dt) values (?, ?, ?, ?)");
        preparedStatement.setString(1, pojo.getTitle());
        preparedStatement.setString(2, pojo.getDscp());
        preparedStatement.setString(3, pojo.getAgency());
        preparedStatement.setDate(4, new java.sql.Date(pojo.getDt().getTime()));
        preparedStatement.executeUpdate();
    }

    @Override
    public boolean candidateExists(POJO pojo) throws SQLException {
        String sql = "select * from " + dbTableName + " where title = ? and dt = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, pojo.getTitle());
        preparedStatement.setDate(2, new java.sql.Date(pojo.getDt().getTime()));
        return preparedStatement.execute();

    }


    public void close() throws SQLException {
        conn.close();
    }
}
