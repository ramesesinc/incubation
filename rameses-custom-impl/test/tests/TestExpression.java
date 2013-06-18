/*
 * TestExpression.java
 * JUnit based test
 *
 * Created on May 23, 2013, 8:16 AM
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
public class TestExpression extends TestCase {
    
    public TestExpression(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    
    public void xtestHello1() {
        ExpressionResolver er = ExpressionResolver.getInstance();
        Map person = new HashMap();
        person.put("lastname", "flores");
        
        Map m = new HashMap();
        m.put( "application1", person );
        String s = er.evalString( "#{ application }", m );
        System.out.println(".."+s+"..");
    }
    
    
  
    
    public void testDouble() {
        ExpressionResolver er = ExpressionResolver.getInstance();
        Map m = new HashMap();
        m.put( "value", 200.0 );
        String d = er.evalString( "#{ @iif( (value * 2)>200,'nope','yep') }", m );
        System.out.println(".."+d+"..");
    }
    
    /*
    public void testBigDecimal() {
        ExpressionResolver er = ExpressionResolver.getInstance();
        Map m = new HashMap();
        m.put( "value", 200.0 );
        BigDecimal d = er.evalDecimal( "(value / 2) * 2.5", m );
        System.out.println(".."+d+"..");
    }
    */
    
    public void testBoolean() {
        /*
        ExpressionResolver er = ExpressionResolver.getInstance();
        Map m = new HashMap();
        m.put( "value", 320.0 );
        boolean d = er.evalBoolean( "'sample'", m );
        System.out.println(".."+d+"..");
         */
        //System.out.println(Boolean.parseBoolean("1"));
    }
    
}
