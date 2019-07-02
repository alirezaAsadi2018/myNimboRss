package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DaoImpl implements Dao {
    private static DaoImpl instance;
    private String hostName = "localhost";
    private String port = "3306";
    private String dbName = "news";
    private String dbTableName = "news_table";
    private String dbUser = "admin";
    private String dbPassword = "admin";
    private Connection conn;

    private DaoImpl() throws DBNotExistsExp {
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
        try {
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            Statement statement = conn.createStatement();
            createTableIfNotExists(statement);
        } catch (Exception e) {
            throw new DBNotExistsExp("database doesn't exist");
        }
    }

    /**
     * get the only instance of this singletone class
     *
     * @return the only instance of this class
     */
    public static DaoImpl getInstance() throws DBNotExistsExp {
        if (instance == null)
            return new DaoImpl();
        return instance;
    }

    public void connect() throws DBNotExistsExp {


    }

    public void createDatabaseIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS " + dbName + ")";
        statement.execute(sql);
    }

    public void createTableIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + dbTableName + "(\n"
                + "	id integer PRIMARY KEY auto_increment,\n"
                + "	title text, dscp text, agency text, dt datetime);";
        statement.execute(sql);
    }

    @Override
    public void insertCandidate(String title, String dscp, String agency, Date dt) throws SQLException {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        String date = dateFormat.format(dt);
        Statement statement = conn.createStatement();
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(dbTableName);
        sql.append("(title, dscp, agency, dt) values(");
        sql.append("\'"+title+"\',");
        sql.append("\'"+dscp+"\',");
        sql.append("\'" + agency + "\',");
        sql.append("\'" + date + "\'");
        sql.append(")");
        statement.execute(sql.toString());
    }

    public void close() throws SQLException {
        conn.close();
    }
}
