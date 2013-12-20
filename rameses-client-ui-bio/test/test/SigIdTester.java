/*
 * SigIdTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.sigid.SigIdViewer;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class SigIdTester extends TestCase {
    
    public SigIdTester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        SigIdModel model = new SigIdModel(){
            public void onselect(byte[] data) {
                System.out.println("onselect-> " + data);
            }

            public void onclose() {
                System.out.println("onclose");
            }            
        };
        SigIdViewer.open(model); 
    }
}
