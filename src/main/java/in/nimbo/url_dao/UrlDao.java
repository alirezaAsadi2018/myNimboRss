package in.nimbo.url_dao;

import java.net.URL;
import java.util.List;

public interface UrlDao {
    List<URL> getUrls();

    void insertUrl(URL url);

    void deleteUrl(URL url);
}
