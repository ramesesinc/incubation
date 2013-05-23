/*
 * MyTest.java
 * JUnit based test
 *
 * Created on May 23, 2013, 9:27 AM
 */

package tests;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.util.HashMap;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class MyTest extends TestCase {
    
    public MyTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public class MyMap extends HashMap {
        public Object get(Object key) {
            System.out.println("key is->"+key);
            if(key.equals("out")) {
                return super.get("out");
            }
            return new HashMap();
        }
    }
    
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        Template t = ste.createTemplate( "${ value == null }" );
        System.out.println("->"+t.make(new MyMap()).toString());
    }
    
}
