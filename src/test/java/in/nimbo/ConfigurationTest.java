package in.nimbo;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void testConfiguration(){
        ConfigurationLoader configuration = new ConfigurationLoader();
        Assert.assertEquals("testValue", configuration.get("test"));
    }
}
