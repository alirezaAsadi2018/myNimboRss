package in.nimbo.dao.url_dao;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.exception.UrlDaoException;
import in.nimbo.exception.UrlExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlDaoImplTest {
    private static final String DBCONFIGNAME = "dbConfig";
    private static final String DBCONFIGDIR = "src/sql/url-table-schema.sql";
    private static URL[] urls;
    private static HikariDataSource ds;
    private static Config dbConfig = ConfigFactory.load(DBCONFIGNAME);
    private static final String TABLENAME = dbConfig.getString("db.urlTableName");
    private static UrlDaoImpl dao;
    private static Connection connection;
    private static URL expectedUrl;

    @BeforeClass
    public static void init() throws SQLException, FileNotFoundException, MalformedURLException {
        connection = DriverManager.getConnection(dbConfig.getString("db.url"), "", "");
        ds = mock(HikariDataSource.class);
        when(ds.getConnection()).thenReturn(connection);
        dao = new UrlDaoImpl(TABLENAME, ds);
        Scanner scanner = new Scanner(new FileInputStream(DBCONFIGDIR));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            sb.append(line);
        }
        Statement statement = connection.createStatement();
        statement.execute(sb.toString());

        expectedUrl = new URL("https://www.farsnews.com/rss");
        urls = new URL[]{
                new URL("https://news.google.com/rss"),
                new URL("https://www.tabnak.ir/fa/rss/allnews"),
                new URL("https://www.farsnews.com/rss"),
                new URL("https://www.varzesh3.com/rss/all"),
                new URL("https://www.yjc.ir/en/rss/allnews")
        };

    }

    @Before
    public void before() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from " + TABLENAME + ";");
        ps.executeUpdate();
    }

    @After
    public void after() throws SQLException {
        ds = mock(HikariDataSource.class);
        when(ds.getConnection()).thenReturn(connection);
        dao = new UrlDaoImpl(TABLENAME, ds);
    }

    private void insert(URL url) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into " + TABLENAME + "(url)" + " values" + "(?);");
        ps.setString(1, url.toString());
        ps.executeUpdate();
    }


    @Test
    public void insertUrlTest() throws SQLException, UrlDaoException, UrlExistsException {
        dao.insertUrl(expectedUrl);
        PreparedStatement ps = connection.prepareStatement("select * from " + TABLENAME + ";");
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.first()) {
            assertEquals(expectedUrl.toString(), resultSet.getString("url"));
        } else {
            fail();
        }
    }

    @Test
    public void insertUrlWhereAlreadyExistsTest() throws UrlDaoException, UrlExistsException {
        dao.insertUrl(expectedUrl);
        try {
            dao.insertUrl(expectedUrl);
            fail();
        } catch (UrlExistsException e) {
            //test passed
        }
    }

    @Test
    public void getUrls() throws MalformedURLException, SQLException, UrlDaoException {
        for (URL url: urls) {
            insert(url);
        }
        List urlList = dao.getUrls();
        System.out.println(urlList);
        Arrays.stream(urls).forEach(i -> assertTrue(urlList.contains(i)));
    }

    @Test
    public void deleteUrl() throws SQLException, UrlDaoException {
        for (URL url : urls) {
            insert(url);
        }
        URL deletedOne = urls[new SecureRandom().nextInt(urls.length)];
        dao.deleteUrl(deletedOne);
        PreparedStatement ps = connection.prepareStatement("select count(url) as cnt from " + TABLENAME + ";");
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.first()){
            assertEquals(urls.length - 1, resultSet.getInt("cnt"));
        }else{
            fail();
        }
        ps = connection.prepareStatement("select * from " + TABLENAME + " where url = ?;");
        ps.setString(1, deletedOne.toString());
        resultSet = ps.executeQuery();
        if(resultSet.first()) {
            fail();
        }
    }
}