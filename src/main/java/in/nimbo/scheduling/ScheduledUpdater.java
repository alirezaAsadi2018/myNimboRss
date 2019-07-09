package in.nimbo.scheduling;

import in.nimbo.NewsDao.NewsDao;
import in.nimbo.RssFeedReader;
import in.nimbo.url_dao.UrlDao;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class ScheduledUpdater {
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
                urlDao.getUrls().forEach(ScheduledUpdater.this::readUrl);
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 0, period);
    }

    protected void readUrl(URL url) {
        executor.submit(new ReedFeedTask(url, new RssFeedReader(newsDao)));
    }
}
