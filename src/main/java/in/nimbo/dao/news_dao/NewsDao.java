package in.nimbo.dao.news_dao;

import in.nimbo.News;
import in.nimbo.SearchFilter;
import in.nimbo.exception.NewsDaoException;

import java.util.List;

public interface NewsDao {

    void insert(News news) throws NewsDaoException;

    List<News> getNews() throws NewsDaoException;

    boolean newsExist(News news) throws NewsDaoException;

    List<News> search(SearchFilter searchFilter) throws NewsDaoException;

}
