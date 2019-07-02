package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;

import java.sql.SQLException;
import java.sql.Statement;

public interface Dao {
//    int insertCandidate(String title, String dscp, String agency, String dt);

    void connect() throws DBNotExistsExp;

    void createTableIfNotExists(Statement statement) throws SQLException;

}
