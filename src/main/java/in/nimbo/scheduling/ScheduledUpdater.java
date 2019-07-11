package in.nimbo.scheduling;

import in.nimbo.RssFeedReader;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.exception.UrlDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ScheduledUpdater(UrlDao urlDao, ExecutorService executor, NewsDao newsDao, long interval) {
        this.urlDao = urlDao;
        this.executor = executor;
        this.newsDao = newsDao;
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

    protected void readUrl(URL url) {
        executor.submit(() -> new RssFeedReader(newsDao).readRSS(url));
    }
}
