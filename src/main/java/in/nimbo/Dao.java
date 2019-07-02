package in.nimbo;

import in.nimbo.Exp.DBNotExistsExp;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface Dao {
    public void insertCandidate(POJO pojo) throws SQLException;

    void createTableIfNotExists(Statement statement) throws SQLException;

    List<POJO> searchByTitle(String title) throws SQLException;

    List<POJO> getNews() throws SQLException;

    public boolean candidateExists(POJO pojo) throws SQLException;
}
