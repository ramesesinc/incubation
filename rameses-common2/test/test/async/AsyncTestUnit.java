/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.async;

import com.rameses.common.AbstractAsyncHandler;
import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import junit.framework.TestCase;

/**
 *
 * @author rameses
 */
public class AsyncTestUnit extends TestCase {
    
    private ScriptServiceContext ctx; 
    private IService svc; 
    
    public AsyncTestUnit(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        Map appenv = new HashMap();
        appenv.put("app.host", "107.21.113.74:8170");
        appenv.put("app.cluster", "osiris3");
        appenv.put("app.context", "notification");        
        ctx = new ScriptServiceContext(appenv);    
        svc = ctx.create("TestCloudNotificationService3", IService.class);
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map data = new HashMap();
        data.put("channel", "etracsilocosnortetest" );
        data.put("origin", "cebu" );
        data.put("destchannel", "003" );
        Object result = svc.testImmediateMessage( data, new AsyncHandlerImpl() ); 
        System.out.println( result );
        JOptionPane.showMessageDialog(null, "waiting...");
    }
    
    public interface IService {
        Object testImmediateMessage( Object params, AbstractAsyncHandler handler ); 
    }
    
    public class AsyncHandlerImpl extends AbstractAsyncHandler {

        public void onError(Exception e) {
            System.out.println("onerror-> " + e.getMessage()); 
            this.retry();
        }

        public void onMessage(Object o) { 
            System.out.println("onmessage-> " + o);
        } 
    }
}
