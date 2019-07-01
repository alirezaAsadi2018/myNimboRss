package in.nimbo;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RSSFeedParser parser = new RSSFeedParser("https://www.tabnak.ir/fa/rss/allnews");
        Feed feed = parser.readFeed();
        for (FeedMessage message : feed.getMessages()) {
            System.out.print(message.title + " ");
            System.out.println(message.title);
        }
    }
}
