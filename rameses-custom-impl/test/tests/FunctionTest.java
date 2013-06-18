/*
 * FunctionTest.java
 * JUnit based test
 *
 * Created on June 18, 2013, 1:27 PM
 */

package tests;

import com.rameses.common.FunctionResolver;
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
    public void testHello() {
        System.out.println("func->"+FunctionResolver.getInstance().findStringFunction("iif"));
    }

}
