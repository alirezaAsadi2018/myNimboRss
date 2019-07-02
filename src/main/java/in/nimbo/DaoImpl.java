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
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;
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
        DateFormat date = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        Statement statement = conn.createStatement();
        String sql;
        if (dscp.length() == 0)
            sql = "insert into " + dbTableName + " (title, agency, dt) values(" + "\'" + title + "\'" + ", " +
                    "\'" + agency + "\'" + ", " + "\'" + date.format(dt) + "\'" + ");";
        else
            sql = "insert into " + dbTableName + " (title, dscp, agency, dt) values(" + "\'" + title + "\'" + ", " + "\'" + dscp +
                    "\'" + ", " + "\'" + agency + "\'" + ", " + "\'" + date.format(dt) + "\'" + ");";
        statement.execute(sql);
    }

    public void close() throws SQLException {
        conn.close();
    }
}
