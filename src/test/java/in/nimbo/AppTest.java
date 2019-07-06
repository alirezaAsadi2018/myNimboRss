package in.nimbo;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import in.nimbo.Exp.DBNotExistsExp;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    private App app;

    /**
     * Rigorous Test :-)
     */
    @Before
    public void init(){
        try {
            URL siteUrl = new URL("https://www.yjc.ir/en/rss/allnews");
            app = new App(NewsDaoImpl.getInstance(), siteUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void getDscpTestOutput() {
        String url = "https://www.farsnews.com/news/13980412001127/%D8%AE%D8%A8%D8%B1%DA%AF%D8%B2%D8%A7%D8%B1%DB%8C-%D9%81%D8%A7%D8%B1%D8%B3-%D8%A8%D9%87-%D8%AF%D9%84%DB%8C%D9%84-%D8%A7%D9%87%D8%A7%D9%86%D8%AA-%D8%B3%D9%81%D8%A7%D8%B1%D8%AA-%D8%A7%D9%86%DA%AF%D9%84%DB%8C%D8%B3-%D8%A8%D9%87-%D8%B1%D8%B3%D8%A7%D9%86%D9%87%E2%80%8C%D9%87%D8%A7-%D8%AF%D8%B1-%D9%86%D8%B4%D8%B3%D8%AA-%D9%85%D8%B7%D8%A8%D9%88%D8%B9%D8%A7%D8%AA%DB%8C";
        try {
            String out = app.getDscp(url);
            app.getDscp("invalid input");
            fail();
        } catch (BoilerpipeProcessingException e) {
            //
        } catch (SAXException e) {
            //
        } catch (IOException e) {
           //
        }

    }
}
