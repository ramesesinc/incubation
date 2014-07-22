/*
 * TestConf.java
 * JUnit based test
 *
 * Created on July 18, 2014, 2:56 PM
 */

package test;

import com.rameses.util.Conf;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestConf extends TestCase {
    
    public TestConf(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        File file = new File("C:\\Temp\\test.conf");
        Map props = Conf.Loader.load(new FileInputStream(file));
        System.out.println(props);
    }

}
