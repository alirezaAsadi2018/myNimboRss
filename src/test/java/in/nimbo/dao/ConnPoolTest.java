package in.nimbo.dao;

import in.nimbo.ConfigurationLoader;
import org.junit.Test;


public class ConnPoolTest {

    @Test
    public void testConnPool() {
        ConnPool connPool = new ConnPool(new ConfigurationLoader());
    }
}