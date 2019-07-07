package in.nimbo;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import in.nimbo.NewsDao.NewsDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Unit test for simple RssFeedReader.
 */
public class AppTest {
    private RssFeedReader rssFeedReader;

    /**
     * Rigorous Test :-)
     */
    @Before
    public void init() {
        try {
            URL siteUrl = new URL("https://www.yjc.ir/en/rss/allnews");
            rssFeedReader = new RssFeedReader(NewsDaoImpl.getInstance());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void getDscpTestOutput() {


    }
}
