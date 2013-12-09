/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on December 5, 2013, 10:11 AM
 */

package test;

import com.rameses.rcp.fingerprint.FingerPrintViewer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
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
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map options = new HashMap();
        //WebcamViewer.open(options); 
        FingerPrintViewer.open(options); 
    }

}
