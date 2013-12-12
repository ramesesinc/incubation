/*
 * MyTest1.java
 * JUnit based test
 *
 * Created on October 3, 2013, 11:52 AM
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
public class MyTest1 extends TestCase {
    
    public MyTest1(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Map m = new HashMap();
        m.put("VAL1", null );
        m.put("VAL2", 3.0 );
        String s = " @IFEMPTY( VAL1, VAL2 ) * 2.0";
        System.out.println("->"+ExpressionResolver.getInstance().evalDouble( s, m ));
    }

}
