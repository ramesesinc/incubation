/*
 * TestHttp.java
 * JUnit based test
 *
 * Created on May 24, 2013, 9:10 AM
 */

package test;

import com.rameses.http.HttpClient;
import java.util.HashMap;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestHttp extends TestCase {
    
    public TestHttp(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void test1() throws Exception 
    {
//        HttpClient httpc = new HttpClient("reetr.ramesesdev.com:8080/res");
//        Object result = httpc.post("test.txt", new HashMap());
//        System.out.println(result);
        
        HttpClient httpc = new HttpClient("192.168.254.29:8080/misocc");
        Object result = httpc.post("DateService/getServerDate.json", new HashMap());
        System.out.println(result);
    }

}
