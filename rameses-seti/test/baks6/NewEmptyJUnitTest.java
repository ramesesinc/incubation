/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package baks6;

import com.rameses.util.MapVersionControl;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author Elmo Nazareno
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Map m1 = new HashMap();
        m1.put("firstname", "elmo");
        
        Map m2 = new HashMap();
        m2.put("firstname", "jack");

        Map m3 = MapVersionControl.getInstance().diff(m1, m2);
        System.out.println(m3);
    }
}
