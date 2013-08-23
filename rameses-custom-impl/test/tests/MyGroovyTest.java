/*
 * MyGroovyTest.java
 * JUnit based test
 *
 * Created on May 23, 2013, 2:44 PM
 */

package tests;

import com.rameses.common.ExpressionResolver;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class MyGroovyTest extends TestCase {
    
    public MyGroovyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testHello1() throws Exception {
        Map address = new HashMap();
        address.put( "name", "capitol" );
        
        Map map = new HashMap();
        map.put("address",address);
        
        Map app = new HashMap();
        app.put("application", map );
        app.put("value", 250 );
        //String s1 = ExpressionResolver.getInstance().evalString("#{application.address.name}",app);
        //System.out.println("value->"+s1);
        
        
        boolean result = ExpressionResolver.getInstance().evalBoolean("value",app);
        System.out.println("result is " + result);
        /*
        ScriptTemplate t = new ScriptTemplate(s);
        System.out.println("->"+t.execute( app ));
        
        ScriptTemplate t2 = new ScriptTemplate("application.address.name");
        System.out.println("->"+t2.execute( app ));
        */
    }
    
   
    

}
