package in.nimbo.news_dao;

import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.Configuration;
import in.nimbo.Search;
import in.nimbo.exception.NewsDaoException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class NewsDaoImplTest {
    @Mock
    private static HikariDataSource ds;
    @Mock
    private static Connection connection;
    @Mock
    private static NewsDao newsDao;
    @Mock
    private PreparedStatement ps;
    @Mock
    private Configuration configuration;
    @Mock
    private Search search;
    @Spy
    NewsDaoImpl dao;




    @BeforeClass
    public static void init() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test", "", "");
//        newsDao = newsDao(new NewsDaoImpl(configuration, ds));
    }

    @Before
    public void before() throws NewsDaoException, SQLException {
        Mockito.when(configuration.getDbConfig().getString("newsTable.tableName")).thenReturn("news_table");
        dao = new NewsDaoImpl(configuration, ds, search);
        Mockito.when(dao.getConnection()).thenReturn(connection);
        doReturn(connection).when(dao).getConnection();
    }

    @Test
    public void search() {

    }

    @Test
    public void getNews() throws NewsDaoException {
        dao.getNews();
    }

    @Test
    public void insertCandidate() {

    }

    @Test
    public void candidateExists() {
    }
}