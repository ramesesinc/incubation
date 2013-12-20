/*
 * FingerPrintTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.common.FingerPrintModel;
import com.rameses.rcp.fingerprint.FingerPrintViewer;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class FingerPrintTester extends TestCase {
    
    public FingerPrintTester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        FingerPrintModel model = new FingerPrintModel(){
            public void onselect(Object info) {
                System.out.println("onselect-> " + info);
            }

            public void onclose() {
                System.out.println("onclose");
            }            
        };
        FingerPrintViewer.open(model); 
    }
}
