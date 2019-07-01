package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;

/**
 * Hello world!
 *
 */
public class App 
{
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
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: "+e.getMessage());
        }
    }
}
