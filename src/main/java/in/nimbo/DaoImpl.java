package in.nimbo;

import java.sql.*;

public class DaoImpl implements Dao {
    private String hostName = "localhost";
    private String port = "3306";
    private String dbName = "news";
    private String dbTableName = "news_table";
    private String dbUser = "admin";
    private String dbPassword = "admin";

    public void connect() {
        try {
            String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            checkTableExists(conn);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM news_table");
            while (resultSet.next()){
                System.out.println("id " + resultSet.getInt(1));
                System.out.println("title " + resultSet.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public boolean checkTableExists(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, dbName, null);
        return rs.getRow() == 1;
    }
}
