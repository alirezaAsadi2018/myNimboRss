package in.nimbo;

import java.sql.*;

public class Dao {
    private String dbName = "news";
    private String dbTableName = "news_table";
    private String dbUser = "admin";
    private String dbPassword = "admin";

    public void connect() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/"+dbName;
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
            checkDbExists(conn);
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
    private boolean checkDbExists(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, dbName, null);
        return rs.getRow() == 1;
    }
}
