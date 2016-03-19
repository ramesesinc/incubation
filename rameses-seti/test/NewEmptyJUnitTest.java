/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rameses.common.ExpressionResolver;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author dell
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
        Map m = new HashMap();
        m.put("name", "elmo");
        System.out.println("Msg0->"+ExpressionResolver.getInstance().evalString(" Hello #{name}", m));
    }
}
