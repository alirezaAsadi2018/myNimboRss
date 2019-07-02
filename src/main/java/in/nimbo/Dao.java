package in.nimbo;

import java.sql.Connection;
import java.sql.SQLException;

public interface Dao {
    public void connect();
    public boolean checkTableExists(Connection conn) throws SQLException;

}
