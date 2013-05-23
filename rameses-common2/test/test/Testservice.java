/*
 * TestConnect.java
 * JUnit based test
 *
 * Created on February 27, 2013, 9:37 AM
 */

package test;

import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class Testservice extends TestCase {
    
    public Testservice(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public interface EchoService {
        Object test(Object data);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Map map = new HashMap();
        map.put("app.host", "192.168.254.1:8070");
        map.put("app.cluster", "osiris3");
        map.put("app.context", "etracs3");
        ScriptServiceContext sc = new ScriptServiceContext(map);
        EchoService svc = sc.create( "EchoService", EchoService.class  );
        System.out.println("->"+svc.test( "Hello elmo"));
    }
    
}
