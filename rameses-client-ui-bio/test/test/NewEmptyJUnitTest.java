/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on December 5, 2013, 10:11 AM
 */

package test;

import com.rameses.rcp.camera.WebcamViewer;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map options = new HashMap();
        WebcamViewer.open(options); 
    }

}
