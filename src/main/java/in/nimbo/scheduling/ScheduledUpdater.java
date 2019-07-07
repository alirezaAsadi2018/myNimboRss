package in.nimbo.scheduling;

import in.nimbo.RssFeedReader;
import in.nimbo.url_dao.UrlDao;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class ScheduledUpdater {
    private UrlDao urlDao;
    private Executor executor;
    private long period;

    public ScheduledUpdater(UrlDao urlDao, Executor executor, long interval) {
        this.urlDao = urlDao;
        this.executor = executor;
        this.period = interval * 1000L;
    }

    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                urlDao.getUrls().forEach(url -> executor.execute(new ReedFeedTask(url, RssFeedReader.build())));
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 0, period);
    }
}
