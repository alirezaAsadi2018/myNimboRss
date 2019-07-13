package in.nimbo.scheduling;

import com.rometools.rome.io.FeedException;
import in.nimbo.RssFeedReader;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.exception.UrlDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class ScheduledUpdater {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledUpdater.class);
    private UrlDao urlDao;
    private ExecutorService executor;
    private NewsDao newsDao;
    private long period;
    private RssFeedReader rssFeedReader;

    public ScheduledUpdater(UrlDao urlDao, ExecutorService executor, NewsDao newsDao, RssFeedReader rssFeedReader, long interval) {
        this.urlDao = urlDao;
        this.executor = executor;
        this.newsDao = newsDao;
        this.rssFeedReader = rssFeedReader;
        this.period = interval * 1000L;
    }

    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    urlDao.getUrls().forEach(ScheduledUpdater.this::readUrl);
                } catch (UrlDaoException e) {
                    logger.error("can't get urls from database", e);
                }
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 0, period);
    }

    private void readUrl(URL url) {
        executor.submit(() -> {
            try {
                newsDao.insertAllNews(rssFeedReader.getNewsFromRss(url));
            } catch (IOException | FeedException e) {
                logger.error("XmlReader cannot read the feedUrl caused by rome library SyndFeed reader", e);
            }
        });
    }
}
