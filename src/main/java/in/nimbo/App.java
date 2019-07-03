package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import in.nimbo.Exp.DBNotExistsExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * Main class for reading RSS from atleast 5 htmlUrls created by Alireza Asadi and Mostafa Ojaghi
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private final NewsDao newsDao;
    private final URL feedUrl;

    public App(NewsDao newsDao, URL feedUrl) {
        this.newsDao = newsDao;
        this.feedUrl = feedUrl;
    }

    public static void main(String[] args) {
        logger.debug("debuging...");
        App app = null;
        try {
            URL feedUrl = new URL("https://www.yjc.ir/en/rss/allnews");
            app = new App(NewsDaoImpl.getInstance(), feedUrl);
            app.readRSS();
            System.out.println(app.getNews());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (DBNotExistsExp dbNotExistsExp) {
            dbNotExistsExp.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            assert app != null;
            app.closeDao();
        }
    }

    /**
     * reads rss from the feedUrl passed to the constructor and inserts into the database
     *
     * @throws IOException
     * @throws FeedException
     * @throws SQLException
     * @throws BoilerpipeProcessingException
     * @throws SAXException
     */
    public void readRSS() throws FeedException, SQLException, BoilerpipeProcessingException, SAXException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        String dscp = "";
        for (SyndEntry entry : feed.getEntries()) {
            try {
                dscp = getDscp(entry.getLink());
            }catch (IOException e){
                logger.error(e.getMessage());
            }
            newsDao.insertCandidate(new News(entry.getTitle(), dscp, entry.getLink(), entry.getPublishedDate()));
        }
    }

    /**
     * reads information from an Html link and boilerpipes(extracts the article) of it
     *
     * @param htmlUrl url to read information from
     * @return string containing the main article of the htmlUrl passed to it
     * @throws BoilerpipeProcessingException
     * @throws SAXException
     * @throws IOException
     */
    public String getDscp(String htmlUrl) throws BoilerpipeProcessingException, SAXException, IOException {
        HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(htmlUrl));
        TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        return CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
    }

    public List<News> searchByTitle(String title) throws SQLException {
        return newsDao.searchByTitle(title);
    }

    public List<News> getNews() throws SQLException {
        return newsDao.getNews();
    }

    private void closeDao() {
        try {
            newsDao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
