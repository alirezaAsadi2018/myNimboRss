package in.nimbo;

import asg.cliche.ShellFactory;
import com.rometools.rome.io.SyndFeedInput;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.cli.MainMenu;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.news_dao.NewsDaoImpl;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.dao.url_dao.UrlDaoImpl;
import in.nimbo.scheduling.ScheduledUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String DBCONFIGNAME = "dbConfig";
    private static final String DSCONFIGNAME = "dsConfig";


    public static void main(String[] args) {
        Config dbConfig = ConfigFactory.load(DBCONFIGNAME);
        Config dsConfig = ConfigFactory.load(DSCONFIGNAME);
        String newsTableName = dbConfig.getString("newsTable.tableName");
        String urlTableName = dbConfig.getString("urlTable.tableName");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbConfig.getString("db.url"));
        hikariConfig.setUsername(dbConfig.getString("db.username"));
        hikariConfig.setPassword(dbConfig.getString("db.password"));
        hikariConfig.setMaximumPoolSize(dsConfig.getInt("maximumPoolSize"));
        hikariConfig.setMinimumIdle(dsConfig.getInt("minimumIdle"));
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", dsConfig.getInt("prepStmtCacheSqlLimit"));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", dsConfig.getInt("prepStmtCacheSize"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", dsConfig.getBoolean("cachePrepStmts"));
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        NewsDao newsDao = new NewsDaoImpl(newsTableName, dataSource);
        UrlDao urlDao = new UrlDaoImpl(urlTableName, dataSource);
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        RssFeedReader rssFeedReader = new RssFeedReader(syndFeedInput);

        new ScheduledUpdater(urlDao, Executors.newFixedThreadPool(10), newsDao, rssFeedReader, 10 * 60).start();
        try {
            ShellFactory.createConsoleShell("rssReader", "", new MainMenu(urlDao, newsDao))
                    .commandLoop();
        } catch (IOException e) {
            logger.error("error in Main command loop; 'cliche' can't readLine() from input", e);
        }
    }
}
