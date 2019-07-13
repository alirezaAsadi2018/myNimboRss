package in.nimbo;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

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