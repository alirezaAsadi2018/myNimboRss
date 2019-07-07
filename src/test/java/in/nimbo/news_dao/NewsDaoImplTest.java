package in.nimbo.news_dao;

import in.nimbo.exception.NewsDaoException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class NewsDaoImplTest {
    private static Connection connection;
    private static NewsDao spy;

    @BeforeClass
    public static void init() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test", "", "");
        spy = spy(NewsDaoImpl.class);
    }

    @Before
    public void before() throws NewsDaoException, SQLException {
        doReturn(connection).when(spy).getConnection();
    }

    @Test
    public void search() {

    }

    @Test
    public void getNews() {
    }

    @Test
    public void insertCandidate() {
    }

    @Test
    public void candidateExists() {
    }
}