/*
 * MyTTTTest.java
 * JUnit based test
 *
 * Created on May 22, 2013, 9:12 AM
 */

package test;

import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class MyTTTTest extends TestCase {
    
    public MyTTTTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        String text = "hello master #{1+1} and #{2+2} and #{3+3}";
        System.out.println(text.replaceAll("#\\{", "\\$\\{"));
    }

}
