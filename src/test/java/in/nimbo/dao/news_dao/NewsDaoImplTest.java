package in.nimbo.dao.news_dao;

import in.nimbo.ConfigurationLoader;
import in.nimbo.SearchFilters;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewsDaoImplTest {


    @BeforeClass
    public static void beforeClass(){
        ConfigurationLoader configuration = new ConfigurationLoader();
        SearchFilters searchFilters = new SearchFilters();
//        HikariDataSource pool = new HikariDataSource(configuration);
//        NewsDao newsDao = new NewsDaoImpl( configuration.get("newsTable.tableName"), pool);
    }

    @Test
    public void simpleTest(){
        Assert.assertTrue(true);
    }
//    @Mock
//    private static HikariDataSource ds;
//    @Mock
//    private static Connection connection;
//    @Mock
//    private static NewsDao newsDao;
//    @Mock
//    private PreparedStatement ps;
//    @Mock
//    private Configuration configuration;
//    @Mock
//    private SearchFilters search;
//    @Mock
//    HikariDataSource pool;
//    @Spy
//    NewsDaoImpl dao;
//
//
//
//
//    @BeforeClass
//    public static void init() throws SQLException {
//        connection = DriverManager.getConnection("jdbc:h2:mem:test", "", "");
////        newsDao = newsDao(new NewsDaoImpl(configuration, ds));
//    }
//
//    @Before
//    public void before() throws NewsDaoException, SQLException {
//        Mockito.when(configuration.getDbConfig().getString("newsTable.tableName")).thenReturn("news_table");
//        dao = new NewsDaoImpl(configuration.getDbConfig().getString("newsTable.tableName"), pool, search);
//        Mockito.when(dao.getConnection()).thenReturn(connection);
//        doReturn(connection).when(dao).getConnection();
//    }
//
//    @Test
//    public void search() {
//
//    }
//
//    @Test
//    public void getNews() throws NewsDaoException {
//        dao.getNews();
//    }
//
//    @Test
//    public void insert() {
//
//    }
//
//    @Test
//    public void newsExist() {
//    }
}