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
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.exception.NewsDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

/**
 * Main class for reading RSS from atleast 5 htmlUrls created by Alireza Asadi and Mostafa Ojaghi
 */
public class RssFeedReader {
    private static final Logger logger = LoggerFactory.getLogger(RssFeedReader.class);
    private NewsDao newsDao;

    public RssFeedReader(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    /**
     * reads rss from the feedUrl passed to the constructor and inserts into the database
     *
     * @param feedUrl
     */
    public void readRSS(URL feedUrl) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            for (SyndEntry entry : feed.getEntries()) {
                try {
                    String description = getNewsContent(entry.getLink());
                    newsDao.insert(new News(entry.getTitle(), description, entry.getLink(), entry.getAuthor(), entry.getPublishedDate()));
                } catch (IOException e) {
                    logger.error("cannot connect to the the link: " + entry.getLink() + "; access denied!! ", e);
                } catch (NewsDaoException e) {
                    logger.error(e.getMessage(), e);
                } catch (SAXException | BoilerpipeProcessingException e) {
                    logger.error("cannot convert HTMlDocument to TextDocument", e);
                }
            }
        } catch (IOException | FeedException e) {
            logger.error("XmlReader cannot read the feedUrl caused by rome library SyndFeed reader", e);
        }
    }

    private boolean isValidUrl(URL url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return url.toString().matches(regex);
    }


    public HTMLDocument fetchHtml(String htmlUrl) throws IOException {
        return HTMLFetcher.fetch(new URL(htmlUrl));
    }

    public String getNewsContent(String htmlUrl) throws BoilerpipeProcessingException, SAXException, IOException {
        HTMLDocument htmlDoc = fetchHtml(htmlUrl);
        TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
        return CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
    }

}
