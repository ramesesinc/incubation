import com.rameses.common.NodeMask;
import junit.framework.*;
/*
 * TestChildren.java
 * JUnit based test
 *
 * Created on August 1, 2013, 1:18 PM
 */

/**
 *
 * @author Elmo
 */
public class TestNodeMask extends TestCase implements NodeMask {
    
    public TestNodeMask(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        int test = 9;
        
        System.out.println( (test & CHILD) > 0   );
    }

}
