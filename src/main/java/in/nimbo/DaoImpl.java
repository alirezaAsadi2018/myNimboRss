package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;

import java.sql.*;

public class DaoImpl implements Dao {
    private String hostName = "localhost";
    private String port = "3306";
    private String dbName = "news";
    private String dbTableName = "news_table";
    private String dbUser = "admin";
    private String dbPassword = "admin";

    public void connect() throws DBNotExistsExp {
            String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;
            try( Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                Statement statement = conn.createStatement();
                createTableIfNotExists(statement);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM news_table");
                while (resultSet.next()){
                    System.out.println("id " + resultSet.getInt(1));
                    System.out.println("title " + resultSet.getString(2));
                }
            }
            catch (Exception e) {
                throw new DBNotExistsExp("database doesn't exist");
            }

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
}
