package in.nimbo.scheduling;

import com.rometools.rome.io.FeedException;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import in.nimbo.RssFeedReader;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

public class ReedFeedTask implements Runnable{
    private URL url;
    private RssFeedReader feedReader;

    public ReedFeedTask(URL url, RssFeedReader feedReader) {
        this.url = url;
        this.feedReader = feedReader;
    }

    @Override
    public void run() {
        try {
            feedReader.readRSS(url);
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();
        }
    }
}
