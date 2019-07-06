package in.nimbo;

import java.sql.SQLException;
import java.util.List;

public interface NewsDao {
    public void insertCandidate(News news) throws SQLException;

    List<News> searchByTitle(String title) throws SQLException;

    List<News> getNews() throws SQLException;

    public boolean candidateExists(News news) throws SQLException;

    public void close() throws SQLException;

    public List<News> search(String title, String text) throws SQLException;
}
