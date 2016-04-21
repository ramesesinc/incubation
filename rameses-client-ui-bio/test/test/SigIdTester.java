/*
 * SigIdTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.common.SigIdResult;
import com.rameses.rcp.sigid.SigIdViewer;
import javax.swing.UIManager;
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
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        SigIdModel model = new SigIdModel(){
            public void onselect( Object data ) { 
                SigIdResult result = (SigIdResult) data; 
                System.out.println("onselect-> " + result);
                result.dump();
            }

            public void onclose() {
                System.out.println("onclose");
            }            
        };
        SigIdViewer.open(model); 
    }
}
