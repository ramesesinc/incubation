/*
 * AlphaNumRandomTest.java
 * JUnit based test
 *
 * Created on December 17, 2013, 12:59 PM
 */

package test;

import com.rameses.util.NumberToWords;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class Test1 extends TestCase {
    
    public Test1(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        double t = 125698.77;
        System.out.println(NumberToWords.getInstance().convert(t));
    }

}
