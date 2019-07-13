package in.nimbo.dao.news_dao;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Filter;
import in.nimbo.News;
import in.nimbo.SearchFilter;
import in.nimbo.exception.NewsDaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NewsDaoImplTest {
    private static final String DBCONFIGNAME = "dbConfig";
    private static final String DBCONFIGDIR = "src/sql/news-table-schema.sql";
    private static HikariDataSource ds;
    private static Config dbConfig = ConfigFactory.load(DBCONFIGNAME);
    private static final String TABLENAME = dbConfig.getString("db.newsTableName");
    private static NewsDaoImpl dao;
    private static Connection connection;
    private static News expectedNews;

    @BeforeClass
    public static void init() throws SQLException, FileNotFoundException {
        connection = DriverManager.getConnection(dbConfig.getString("db.url"), "", "");
        ds = mock(HikariDataSource.class);
        when(ds.getConnection()).thenReturn(connection);
        dao = new NewsDaoImpl(TABLENAME, ds);
        Scanner scanner = new Scanner(new FileInputStream(DBCONFIGDIR));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            sb.append(line);
        }
        Statement statement = connection.createStatement();
        statement.execute(sb.toString());

        expectedNews = new News("a", "b", "c", "d", new Timestamp(new Date().getTime()));

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
        dao = new NewsDaoImpl(TABLENAME, ds);
    }

    private void insert(News news) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into " +
                TABLENAME +
                " (title, description, link, agency, date)" +
                " values" +
                " (?, ?, ?, ?, ?);"
        );
        ps = connection.prepareStatement("insert into " + TABLENAME + " (title, description, link, agency, date) values (?, ?, ?, ?, ?);");
        ps.setString(1, news.getTitle());
        ps.setString(2, news.getDescription());
        ps.setString(3, news.getLink());
        ps.setString(4, news.getAgency());
        ps.setTimestamp(5, news.getDate());
        ps.executeUpdate();
    }

    @Test
    public void insertTestWithGetConnectionException() throws SQLException {
        when(ds.getConnection()).thenThrow(new SQLException());
        try {
            dao.insertNews(expectedNews);
            fail();
        } catch (NewsDaoException e) {
            //passed test
        }
    }

    @Test
    public void insertTestEquals() throws NewsDaoException, SQLException {
        dao.insertNews(expectedNews);
        PreparedStatement ps = connection.prepareStatement("select * from " + TABLENAME + ";");
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.first()) {
            News actual = new News(resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("link"),
                    resultSet.getString("agency"),
                    resultSet.getTimestamp("date"));
            assertEquals(expectedNews, actual);
        } else {
            fail();
        }

    }

    @Test
    public void insertTestCountInscreased() throws SQLException, NewsDaoException {
        dao.insertNews(expectedNews);
        final int cntExpected = 1;
        PreparedStatement ps = connection.prepareStatement("select count(*) as cnt from " + TABLENAME + ";");
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.first()) {
            int cnt = resultSet.getInt("cnt");
            assertEquals(cntExpected, cnt);
        } else {
            fail();
        }
    }

    @Test
    public void newsExistTest() throws SQLException, NewsDaoException {
        insert(expectedNews);
        assertTrue(dao.newsExist(expectedNews));

        //test when one/more-than-one column is/are null
        News news = new News(null, "a", "b", "c", (Timestamp) null);
        insert(news);
        assertTrue(dao.newsExist(news));

    }

    @Test
    public void searchTestOtherThanDate() throws NewsDaoException, SQLException {
        News news = new News("abc", "aaasdwdcsl aaasdaaa", "www.asdwasd.casew",
                "salkdjw", new Timestamp(new Date().getTime())
        );
        insert(news);
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.addFilter("ab", Filter.title);
        searchFilter.addFilter("c", Filter.title);
        searchFilter.addFilter("aaa", Filter.description);
        assertTrue(dao.search(searchFilter).contains(news));
    }


    @Test
    public void getNewsTest() throws SQLException, NewsDaoException {
        PreparedStatement ps = connection.prepareStatement("insert into " +
                TABLENAME +
                " (title, description, link, agency, date)" +
                " values" +
                " (?, ?, ?, ?, ?);"
        );
        insert(expectedNews);
        assertTrue(dao.getNews().contains(expectedNews));
    }
}