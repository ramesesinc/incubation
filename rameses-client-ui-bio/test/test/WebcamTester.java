/*
 * WebcamTester.java
 * JUnit based test
 *
 * Created on December 20, 2013, 1:12 PM
 */

package test;

import com.rameses.rcp.camera.WebcamViewer;
import com.rameses.rcp.common.CameraModel;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class WebcamTester extends TestCase {
    
    public WebcamTester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        CameraModel model = new CameraModel(){
            public void onselect(byte[] data) {
                System.out.println("onselect-> " + data);
            }

            public void onclose() {
                System.out.println("onclose");
            }            
        };
        WebcamViewer.open(model); 
    }
}
