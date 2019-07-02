package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        try {
            URL feedUrl = new URL("https://www.tabnak.ir/fa/rss/allnews");

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for (SyndEntry entry : feed.getEntries()) {

                System.out.println(entry.getPublishedDate() + "\n"
                        + entry.getTitle() + "\n"
                        + entry.getDescription());

            }
            DaoImpl dao = new DaoImpl();
            dao.connect();

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: "+e.getMessage());
        }
    }
}
