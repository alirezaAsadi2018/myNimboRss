package in.nimbo.dao.news_dao;

import in.nimbo.News;
import in.nimbo.SearchFilters;
import in.nimbo.exception.NewsDaoException;

import java.sql.Connection;
import java.util.List;

public interface NewsDao {

    void insertCandidate(News news) throws NewsDaoException;

    List<News> getNews() throws NewsDaoException;

    boolean candidateExists(News news) throws NewsDaoException;

    List<News> search(SearchFilters searchFilters) throws NewsDaoException;

    Connection getConnection() throws NewsDaoException;


}
