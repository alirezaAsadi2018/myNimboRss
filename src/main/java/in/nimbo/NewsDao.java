package in.nimbo;

import java.sql.SQLException;
import java.util.List;

public interface NewsDao {
    void insertCandidate(News news) throws SQLException;

    List<News> getNews() throws SQLException;

    boolean candidateExists(News news) throws SQLException;

    void close() throws SQLException;

    List<News> search(String title, String text) throws SQLException;
}
