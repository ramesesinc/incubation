/*
 * TestPhoto.java
 * JUnit based test
 *
 * Created on April 3, 2014, 1:06 PM
 */

package test.control;

import com.rameses.rcp.control.XPhoto;
import javax.swing.JOptionPane;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestPhoto extends TestCase {
    
    public TestPhoto(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testPhoto() throws Exception 
    {
        XPhoto x = new XPhoto();
        JOptionPane.showMessageDialog(null, x);
    }

}
