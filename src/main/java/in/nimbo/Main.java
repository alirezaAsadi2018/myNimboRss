package in.nimbo;

import com.rometools.rome.io.FeedException;
import com.zaxxer.hikari.HikariDataSource;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import in.nimbo.dao.ConnPool;
import in.nimbo.exception.ServiceException;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.dao.news_dao.NewsDaoImpl;
import in.nimbo.dao.url_dao.UrlDao;
import in.nimbo.dao.url_dao.UrlDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(RssFeedReader.class);


    public static void main(String[] args) {
        ConfigurationLoader configuration = new ConfigurationLoader();
        Search search = new Search(configuration);
        ConnPool pool = new ConnPool(configuration);
        NewsDao newsDao = new NewsDaoImpl(configuration.getConfigMap().get("newsTable.tableName").toString(), pool, search);
        UrlDao urlDao = new UrlDaoImpl(configuration.getConfigMap().get("urlTable.tableName").toString(), pool);
        RssFeedReader rssFeedReader = new RssFeedReader(configuration, newsDao, urlDao);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. add feed");
            System.out.println("2. print all");
            System.out.println("3. search");
            System.out.println("0. exit");
            switch (scanner.nextInt()) {
                case 1:
                    System.out.println("enter rss url:");
                    try {
                        URL feedUrl = new URL(scanner.next());
                        rssFeedReader.readRSS(feedUrl);
                    } catch (MalformedURLException e) {
                        logger.error("The url is not well-formed. Change it! ", e);
                    } catch (IOException e) {
                        logger.error("Xml reader can not read the url. ", e);
                    } catch (FeedException e) {
                        logger.error(e.getMessage(), e);
                    } catch (BoilerpipeProcessingException e) {
                        logger.error(e.getMessage(), e);
                    } catch (SAXException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
                case 2:
                    try {
                        System.out.println(rssFeedReader.getNews());
                    } catch (ServiceException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
                case 3:
                    searchCommand(scanner, rssFeedReader, search);
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("enter a valid number");
            }
        }
    }


    private static void searchCommand(Scanner scanner, RssFeedReader rssFeedReader, Search search) {
        System.out.println("search by:");
        System.out.println("1. title");
        System.out.println("2. report");
        System.out.println("3. agency");
        int num = scanner.nextInt();
        scanner.nextLine();
        System.out.print("string to search: ");
        try {
            switch (num) {
                case 1:
                    search.addFilter(scanner.next(), Filter.title);
                    System.out.println(rssFeedReader.search());
                    break;
                case 2:
                    search.addFilter(scanner.next(), Filter.dscp);
                    System.out.println(rssFeedReader.search());
                    break;
                case 3:
                    search.addFilter(scanner.next(), Filter.agency);
                    System.out.println(rssFeedReader.search());
                    break;
                default:
                    System.out.println("the number is not valid");
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        } catch (ServiceException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
