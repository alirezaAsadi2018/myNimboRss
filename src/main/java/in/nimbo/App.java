package in.nimbo;
import com.mysql.cj.log.Slf4JLogger;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.Exp.DBNotExistsExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Slf4JLogger slf4JLogger = new Slf4JLogger("App");
    private final NewsDao newsDao;
    private final URL feedUrl;

    public App(NewsDao newsDao, URL feedUrl) throws DBNotExistsExp {
        this.newsDao = newsDao;
        this.feedUrl = feedUrl;
    }

    public void readRSS() throws IOException, FeedException, SQLException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        for (SyndEntry entry : feed.getEntries()) {
            String dscp="";
            if(entry.getDescription() != null) {
                dscp = entry.getDescription().getValue();
            }
            newsDao.insertCandidate(new News(entry.getTitle(), dscp, entry.getAuthor(), entry.getPublishedDate()));
        }
    }
    public List<News> searchByTitle(String title) throws SQLException {
        return newsDao.searchByTitle(title);
    }
    public List<News> getNews() throws SQLException {
        return newsDao.getNews();
    }
    public static void main( String[] args )
    {
        App app = null;
        try {
            URL feedUrl = new URL("https://www.yjc.ir/en/rss/allnews");
            app = new App(NewsDaoImpl.getInstance(), feedUrl);
            app.readRSS();
            System.out.println(app.getNews());
//            System.out.println(app.searchByTitle("تهران").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: "+e.getMessage());
        }
        finally {
            assert app != null;
            app.closeDao();
        }
    }

    private void closeDao() {
        try {
            newsDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
