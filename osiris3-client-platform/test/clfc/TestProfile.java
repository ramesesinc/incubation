/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on October 23, 2013, 4:41 PM
 */

package clfc;

import com.rameses.osiris3.platform.CipherUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestProfile extends TestCase 
{
    
    public TestProfile(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        ImageIcon icon = getImageIcon("icon.gif");
        ImageIcon splash = getImageIcon("splash.png");
        Map map = new HashMap(); 
        map.put("icon", icon);
        map.put("splash", splash);
        writeToFile(".identity", map); 
    }

    private ImageIcon getImageIcon(String name) throws Exception {
        URL url = getClass().getResource(name);
        return new ImageIcon(url); 
    } 
    
    private void writeToFile(String filename, Map data) throws Exception {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File f = new File(filename);
            Object o = CipherUtil.encode((Serializable) data);
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
        } catch(Exception e) {
            throw e;
        } finally {
            try { oos.close(); } catch(Exception ign){;}
            try { fos.close(); } catch(Exception ign){;}
        } 
    }
}
