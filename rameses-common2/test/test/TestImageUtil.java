/*
 * TestImageUtil.java
 * JUnit based test
 *
 * Created on April 21, 2014, 5:29 PM
 */

package test;

import com.rameses.util.ImageUtil;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestImageUtil extends TestCase {
    
    public TestImageUtil(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testMain() throws Exception {
        File file = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg");
        byte[] bytes = ImageUtil.getInstance().createThumbnail(file, 100, 80);
        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bytes)));
    }

}
