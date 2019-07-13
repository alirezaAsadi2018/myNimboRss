package in.nimbo;

import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RssFeedReaderTest {
    private static final Config config = ConfigFactory.load("articleExtractorTestConfig");
    private static final int numOfTests = 3;
    private static final String TESTPATH = "src/test/resources/";
    private static Map<String, String> pathMap = new HashMap<>();
    private static String[] htmlurls = new String[numOfTests + 1];
    private final double expectedConfidence = .85;

    @BeforeClass
    public static void init() {
        for (int i = 1; i <= numOfTests; i++) {
            htmlurls[i] = config.getObject("urls").unwrapped().get(String.valueOf(i)).toString();
        }
    }

    @Test
    public void getNewsFromRssTest() throws IOException, BoilerpipeProcessingException, SAXException, FeedException {
        SyndFeedInput syndFeedInput = mock(SyndFeedInput.class);
        URL url = new URL("https://www.isna.ir/rss");
        when(syndFeedInput.build(any(Reader.class))).thenReturn(
                new SyndFeedInput().build(new XmlReader(new File(TESTPATH + "rssTest.xml")))
        );
        RssFeedReader rssFeedReader = new RssFeedReader(syndFeedInput);
        rssFeedReader = spy(rssFeedReader);
        doReturn("description").when(rssFeedReader).getNewsContent(anyString());
        List<News> list = rssFeedReader.getNewsFromRss(url);
        assertEquals(1, list.size());//there is only one entry in the rssTest.xml
        News expected = new News("Example entry",
                "description",
                "http://www.example.com/blog/post/1",
                "wikipedia",
                "Sun Sep 06 20:50:00 IRDT 2009");
        News actual = list.get(0);
        assertEquals(expected, actual);
    }

    @Test
    public void fetchHtml() throws IOException {
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        RssFeedReader rssFeedReader = new RssFeedReader(syndFeedInput);
        for (int i = 1; i <= numOfTests; i++) {
            String expected = getStringFromFile(new File(TESTPATH + "articleExtractorTest" + i + ".html"));
            String actual = rssFeedReader.fetchHtml(htmlurls[i]);
            double confidence = calculateConfidence(expected, actual);
            assertTrue(confidence >= expectedConfidence);
        }


    }

    @Test
    public void getNewsContent() throws IOException, BoilerpipeProcessingException, SAXException {
        RssFeedReader rssFeedReader = spy(RssFeedReader.class);
        for (int i = 1; i <= numOfTests; i++) {
            String html = getStringFromFile(new File(TESTPATH + "articleExtractorTest" + i + ".html"));
            doReturn(html).when(rssFeedReader).fetchHtml("");
            String actual = rssFeedReader.getNewsContent("");
            String expected = getStringFromFile(new File(TESTPATH + "articleExtractorTest" + i + "Expected.txt"));
            double confidence = calculateConfidence(expected, actual);
            assertTrue(confidence >= expectedConfidence);
        }
    }

    private double calculateConfidence(String expected, String actual) {
        String[] actualWords = actual.split(" ");
        String[] expectedWords = expected.split(" ");
        Set<String> expectedUniqueWords = Arrays.stream(expectedWords).collect(Collectors.toSet());
        int numberOfWordsInCommon = 0;
        for (String word : actualWords) {
            if (expectedUniqueWords.contains(word))
                ++numberOfWordsInCommon;
        }
        return (double) numberOfWordsInCommon / (double) actualWords.length;
    }

    private String getStringFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine())
            stringBuilder.append(scanner.nextLine());
        return stringBuilder.toString();
    }
}