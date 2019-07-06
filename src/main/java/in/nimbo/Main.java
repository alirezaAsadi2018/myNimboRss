package in.nimbo;

import com.rometools.rome.io.FeedException;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
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
                        App app = new App(NewsDaoImpl.getInstance(), feedUrl);
                        app.readRSS();
                    } catch (FeedException | SQLException | SAXException | BoilerpipeProcessingException | IOException e) {
                        logger.error("", e);
                    }
                    break;
                case 2:
                    try {
                        System.out.println(NewsDaoImpl.getInstance().getNews());
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                    break;
                case 3:
                    searchCommand(scanner);
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("enter a valid number");
            }
        }
    }

    private static void searchCommand(Scanner scanner) {
        System.out.println("search by:");
        System.out.println("1. title");
        System.out.println("2. report");
        System.out.println("3. agency");
        int num = scanner.nextInt();
        scanner.nextLine();
        switch (num) {
            case 1:
                System.out.println("string to search");
                try {
                    System.out.println(NewsDaoImpl.getInstance().search("title", scanner.nextLine()));
                } catch (SQLException e) {
                    logger.error("", e);
                }
                break;
            case 2:
                System.out.println("string to search");
                try {
                    System.out.println(NewsDaoImpl.getInstance().search("dscp", scanner.nextLine()));
                } catch (SQLException e) {
                    logger.error("", e);
                }
                break;
            case 3:
                System.out.println("string to search");
                try {
                    System.out.println(NewsDaoImpl.getInstance().search("agency", scanner.nextLine()));
                } catch (SQLException e) {
                    logger.error("", e);
                }
                break;
            default:
                System.out.println("the number is not valid");
        }
    }
}
