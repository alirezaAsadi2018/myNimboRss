package in.nimbo;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        DaoImpl dao = null;
        try {
            dao = DaoImpl.getInstance();
            dao.connect();
            URL feedUrl = new URL("https://www.tabnak.ir/fa/rss/allnews");
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for (SyndEntry entry : feed.getEntries()) {
                String dscp="";
                if(entry.getDescription() != null)
                    dscp = entry.getDescription().toString();
                dao.insertCandidate(entry.getTitle(), dscp, entry.getAuthor(), entry.getPublishedDate());
//                System.out.println(entry.getPublishedDate() + "\n"
//                        + entry.getTitle() + "\n"
//                        + entry.getDescription());

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: "+e.getMessage());
        }
        finally {
            try {
                dao.close();
            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
