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
import in.nimbo.NewsDao.NewsDao;
import in.nimbo.NewsDao.NewsDaoImpl;
import in.nimbo.exception.NewsDaoException;
import in.nimbo.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * Main class for reading RSS from atleast 5 htmlUrls created by Alireza Asadi and Mostafa Ojaghi
 */
public class RssFeedReader {
    private static final Logger logger = LoggerFactory.getLogger(RssFeedReader.class);
    private final NewsDao newsDao;

    public RssFeedReader(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    /**
     * builder is for excluding Main class from getting concerned with getting instance from newsDaoImpl
     *
     * @return instance of this class
     */
    public static RssFeedReader build() {
        return new RssFeedReader(NewsDaoImpl.getInstance());
    }

//    public static void main(String[] args) {
//        RssFeedReader rssFeedReader = null;
//        try {
//            URL feedUrl = new URL("https://www.mashreghnews.ir/rss");
//            rssFeedReader = new RssFeedReader(NewsDaoImpl.getInstance(), feedUrl);
//            logger.debug(rssFeedReader.search("title", "trump").toString());
//        } catch (SQLException e) {
//            logger.error("error happened when calling rssFeedReader.search(...) ", e);
//        } catch (MalformedURLException e) {
//            logger.error("the url is not well-formed ", e);
//        }
//    }

    /**
     * reads rss from the feedUrl passed to the constructor and inserts into the database
     *
     * @throws IOException
     * @throws FeedException
     * @throws SQLException
     * @throws BoilerpipeProcessingException
     * @throws SAXException
     */
    public void readRSS(URL feedUrl) throws FeedException, SAXException, BoilerpipeProcessingException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        String dscp = "";
        for (SyndEntry entry : feed.getEntries()) {
            try {
                dscp = getDscp(entry.getLink());
            } catch (IOException e) {
                logger.error("cannot connect to the the link: " + entry.getLink() + "; access denied!! ", e);
            }
            try {
                newsDao.insertCandidate(new News(entry.getTitle(), dscp, entry.getLink(), entry.getAuthor(), entry.getPublishedDate()));
            } catch (NewsDaoException e) {
                e.printStackTrace();
            }
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

    public List<News> search(String request, String title) throws ServiceException {
        try {
            return newsDao.search(request, title);
        } catch (NewsDaoException e) {
            throw new ServiceException("searching failed cannot access to database ", e);
        }
    }

    public List<News> getNews() throws ServiceException {
        try {
            return newsDao.getNews();
        } catch (NewsDaoException e) {
            throw new ServiceException("getting news failed cannot access to database ", e);
        }
    }

}
