package in.nimbo;
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
    private final DaoImpl dao;
    private final URL feedUrl;

    public App(URL feedUrl) throws DBNotExistsExp {
        this.dao = DaoImpl.getInstance();
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
            dao.insertCandidate(new POJO(entry.getTitle(), dscp, entry.getAuthor(), entry.getPublishedDate()));
        }
    }
    public List<POJO> searchByTitle(String title) throws SQLException {
        return dao.searchByTitle(title);
    }
    public List<POJO> getNews() throws SQLException {
        return dao.getNews();
    }
    public static void main( String[] args )
    {
        App app = null;
        try {
            URL feedUrl = new URL("https://www.yjc.ir/en/rss/allnews");
            app = new App(feedUrl);
//            feedUrl = new URL("https://www.tabnak.ir/fa/rss/allnews");
//            feedUrl = new URL("https://www.farsnews.com/rss");
//            feedUrl = new URL("https://www.varzesh3.com/rss/all");
//            feedUrl = new URL("https://news.google.com/rss");

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
            dao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
