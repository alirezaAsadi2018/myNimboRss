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
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Main business class for reading RSS from atleast 5 htmlUrls created by Alireza Asadi and Mostafa Ojaghi
 */
public class RssFeedReader {
    private static final Logger logger = LoggerFactory.getLogger(RssFeedReader.class);
    private SyndFeedInput syndFeedInput;

    RssFeedReader(SyndFeedInput syndFeedInput) {
        this.syndFeedInput = syndFeedInput;
    }
    RssFeedReader(){

    }

    /**
     * reads feeds from the feedUrl passed to the constructor with rome tool and returns a list of news
     *
     * @param feedUrl the rssUrl to find news from
     * @return list of news fetched from feedUrl
     * @throws IOException   thrown if XmlReader can't read feedUrl
     * @throws FeedException thrown if rome library feed reader can not parse or generate a feed.
     */
    public List<News> getNewsFromRss(URL feedUrl) throws IOException, FeedException {
        SyndFeed feeds = syndFeedInput.build(new XmlReader(feedUrl));
        List<News> newsList = new LinkedList<>();
        for (SyndEntry entry : feeds.getEntries()) {
            try {
                String description = getNewsContent(entry.getLink());
                newsList.add(new News(entry.getTitle(), description, entry.getLink(), entry.getAuthor(), entry.getPublishedDate().toString()));
            } catch (IOException e) {
                logger.error("cannot connect to the the link: " + entry.getLink() + "; access denied!! ", e);
            } catch (SAXException | BoilerpipeProcessingException e) {
                logger.error("cannot convert HTMlDocument to TextDocument", e);
            }
        }
        return newsList;
    }

    String fetchHtml(String htmlUrl) throws IOException {
        return Jsoup.connect(htmlUrl).get().html();
    }

    String getNewsContent(String htmlUrl) throws BoilerpipeProcessingException, SAXException, IOException {
        String html = fetchHtml(htmlUrl);
        TextDocument doc = new BoilerpipeSAXInput(new InputSource(new StringReader(html))).getTextDocument();
        return CommonExtractors.ARTICLE_EXTRACTOR.getText(doc);
    }

}
