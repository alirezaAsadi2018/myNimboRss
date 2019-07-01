package in.nimbo;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        RSSFeedParser parser = new RSSFeedParser("https://www.tabnak.ir/fa/rss/allnews");
        Feed feed = parser.readFeed();
//        System.out.println(feed.getTitle());
        for (FeedMessage message : feed.getMessages()) {
            System.out.println(message.title);
//            System.out.println(message);
        }
//        System.out.println(feed.getMessages());
    }
}
