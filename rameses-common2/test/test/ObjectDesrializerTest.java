/*
 * ObjectDesrializerTest.java
 * JUnit based test
 *
 * Created on January 1, 2014, 8:48 PM
 */

package test;

import com.rameses.util.ObjectDeserializer;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class ObjectDesrializerTest extends TestCase {
    
    public ObjectDesrializerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        System.out.println("[tax:0,regfee:1000.00,othercharge:500.00,total:1500.00]".length());
        Map m = (Map)ObjectDeserializer.getInstance().read( "[tax:0,regfee:1000.00,othercharge:500.00,total:1500.00]" );
        System.out.println("map is " + m);
        
    }

}
