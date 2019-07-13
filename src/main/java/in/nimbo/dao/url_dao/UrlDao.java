package in.nimbo.dao.url_dao;

import in.nimbo.exception.UrlDaoException;
import in.nimbo.exception.UrlExistsException;

import java.net.URL;
import java.util.List;

public interface UrlDao {
    void insertUrl(URL url) throws UrlDaoException, UrlExistsException;

    List<URL> getUrls() throws UrlDaoException;

    void deleteUrl(URL url) throws UrlDaoException;
}
