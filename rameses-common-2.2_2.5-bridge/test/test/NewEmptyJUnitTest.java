/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on April 7, 2014, 1:57 PM
 */

package test;

import com.rameses.bridge.service.InvokerProxy;
import com.rameses.service.ServiceProxy;
import junit.framework.*;

/**
 *
 * @author wflores 
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testMain() throws Exception {
        System.setProperty("market.host", "localhost:8070");
        System.setProperty("market.cluster", "osiris3");
        System.setProperty("market.context", "test");
        ServiceProxy proxy = InvokerProxy.create("DateService", "market");
        System.out.println(proxy.invoke("getServerDate"));
    }
}
