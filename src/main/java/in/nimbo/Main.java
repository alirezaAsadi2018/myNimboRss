package in.nimbo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.news_dao.NewsDaoImpl;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.dao.url_dao.UrlDaoImpl;
import in.nimbo.exception.NewsDaoException;
import in.nimbo.exception.UrlDaoException;
import in.nimbo.exception.UrlExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. add url");
            System.out.println("2. print all");
            System.out.println("3. searchFilters");
            System.out.println("0. exit");
            switch (scanner.nextInt()) {
                case 1:
                    System.out.println("enter rss url:");
                    String url = scanner.next();
                    try {
                        URL feedUrl = new URL(url);
                        urlDao.insertUrl(feedUrl);
                    } catch (MalformedURLException e) {
                        System.err.println("The url is not well-formed. Change it! ");
                    } catch (UrlDaoException e) {
                        logger.error("url database insertion error", e);
                        System.err.println("Error: can not insert url.");
                    } catch (UrlExistsException e) {
                        logger.error("this url already exists in the database");
                    }
                    break;
                case 2:
                    try {
                        System.out.println(newsDao.getNews());
                    } catch (NewsDaoException e) {
                        logger.error("news database read error", e);
                        System.err.println("Error: can not load news.");
                    }
                    break;
                case 3:
                    searchCommand(scanner, newsDao, new SearchFilter());
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("enter a valid number");
            }
        }
    }


    private static void searchCommand(Scanner scanner, NewsDao newsDao, SearchFilter searchFilter) {
        System.out.println("searchFilters by:");
        System.out.println("1. title");
        System.out.println("2. report");
        System.out.println("3. agency");
        int num = scanner.nextInt();
        scanner.nextLine();
        System.out.print("string to searchFilters: ");
        try {
            switch (num) {
                case 1:
                    searchFilter.addFilter(scanner.next(), Filter.title);
                    System.out.println(newsDao.search(searchFilter));
                    break;
                case 2:
                    searchFilter.addFilter(scanner.next(), Filter.description);
                    System.out.println(newsDao.search(searchFilter));
                    break;
                case 3:
                    searchFilter.addFilter(scanner.next(), Filter.agency);
                    System.out.println(newsDao.search(searchFilter));
                    break;
                default:
                    System.out.println("the number is not valid");
            }
        } catch (NewsDaoException e) {
            System.err.println("news database error.");
            logger.error("Error: can't search in database", e);
        }
    }
}
