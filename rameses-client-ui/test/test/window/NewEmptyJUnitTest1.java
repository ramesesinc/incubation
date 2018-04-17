/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.window;

import javax.swing.JDialog;
import javax.swing.UIManager;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest1 extends TestCase {
    
    public NewEmptyJUnitTest1(String testName) {
        super(testName);
    }

    public void test1() throws Exception { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        
        JDialog d = new JDialog();
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Test"); 
        d.setModal(true); 
        d.setContentPane( new MainPanel()); 
        d.pack(); 
        d.setVisible(true);
        
    }
}
