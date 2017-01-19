/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.util.Base64Cipher;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testMain() throws Exception { 
        
        Map params = new HashMap();
        params.put("billno", "12345"); 
        
        Map map = new HashMap();
        map.put("requestId", new UID().toString()); 
        map.put("serviceName", "RptBillingService");
        map.put("methodName", "getBilling");
        map.put("args", params);
        
        System.out.println( new Base64Cipher().encode(map));
        
//        Map appenv = new HashMap(); 
//        appenv.put("app.cluster", "osiris3"); 
//        appenv.put("app.context", "etracs25");
//        appenv.put("app.host", "localhost:8570");
//        ScriptServiceContext ssc = new ScriptServiceContext(appenv);
//
//        ServiceProxy proxy = ssc.create("DateService");
//        proxy.invoke("getServerDate", new Object[]{}); 
    }
}
