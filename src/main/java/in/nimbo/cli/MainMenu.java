package in.nimbo.cli;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import in.nimbo.SearchFilter;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.exception.NewsDaoException;
import in.nimbo.exception.UrlDaoException;
import in.nimbo.exception.UrlExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainMenu {
    private static final Logger logger = LoggerFactory.getLogger(MainMenu.class);
    private UrlDao urlDao;
    private NewsDao newsDao;

    public MainMenu(UrlDao urlDao, NewsDao newsDao) {
        this.urlDao = urlDao;
        this.newsDao = newsDao;
    }

    @Command(description = "add rss url")
    public String addUrl(String url) {
        try {
            URL feedUrl = new URL(url);
            urlDao.insertUrl(feedUrl);
            return "Url added successfully. We will get it's news from now.";
        } catch (MalformedURLException e) {
            return "The url is not well-formed. Please fix it!";
        } catch (UrlDaoException e) {
            logger.error("url database insertion error", e);
            return "Error: can not insert url.";
        } catch (UrlExistsException e) {
            return "Url already exists";
        }
    }

    @Command
    public String printAllNews() {
        StringBuilder news = new StringBuilder();
        try {
            news.append(newsDao.getNews());
            news.append("\n");
            return news.toString();
        } catch (NewsDaoException e) {
            logger.error("news database read error", e);
            return "Error: can not load news.";
        }
    }

    @Command
    public void search() throws IOException {
        ShellFactory.createConsoleShell("search", "", new SearchMenu(new SearchFilter(), newsDao))
                .commandLoop();
    }
}
