/*
 * FunctionTest.java
 * JUnit based test
 *
 * Created on June 18, 2013, 1:27 PM
 */

package tests;

import com.rameses.common.ExpressionResolver;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class FunctionTest extends TestCase {
    
    public FunctionTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        //System.out.println("func->"+FunctionResolver.getInstance().findStringFunction("iif"));
        /*
        List list = FunctionResolver.getInstance().getFunctionsByGroup("logical");
        for(Object o: list) {
            System.out.println(o);
        }
         */
        Date d1 = java.sql.Date.valueOf( "2013-01-01");
        Date d2 = java.sql.Date.valueOf( "2014-01-07");
        Map m = new HashMap();
        m.put( "BEGINDATE", d1 );
        m.put( "BILLDATE", d2 );
        double d = ExpressionResolver.getInstance().evalDouble( "@MONTHDIFF( BEGINDATE, BILLDATE ) * 1.0", m );
        System.out.println(d);
    }
    
}
