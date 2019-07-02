package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public interface Dao {
    void insertCandidate(String title, String dscp, String agency, Date dt) throws SQLException;

    void connect() throws DBNotExistsExp;

    void createTableIfNotExists(Statement statement) throws SQLException;

}
