import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.control.XActionBar;
import com.rameses.rcp.control.layout.ToolbarLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import junit.framework.*;
/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on April 28, 2013, 11:54 AM
 */

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
    
    private JToolBar createToolbar()
    {
        ToolbarLayout tbrlayout = new ToolbarLayout();
        tbrlayout.setOrientation(UIConstants.FLOW);
        tbrlayout.setAlignment(UIConstants.LEFT); 
        JToolBar tbr = new JToolBar();
        tbr.setLayout(tbrlayout);
        tbr.add(new JButton("JButton#1111111")); 
        tbr.add(new JButton("JButton#222222")); 
        tbr.add(new JButton("JButton#32222333")); 
        tbr.add(new JButton("JButton#4")); 
        tbr.add(new JButton("JButton#5")); 
        tbr.add(new JButton("JButton#6")); 
        tbr.add(new JButton("JButton#7")); 
        tbr.add(new JButton("JButton#8")); 
        tbr.add(new JButton("JButton#9")); 
        tbr.add(new JButton("JButton#10")); 
        tbr.add(new JButton("JButton#11")); 
        tbr.add(new JButton("JButton#12")); 
        tbr.setBorder(BorderFactory.createRaisedBevelBorder());
        return tbr;
    }
    
    private XActionBar createActionBar() 
    {
        XActionBar xab = new XActionBar();
        return xab; 
    }
    
    public void test1() throws Exception 
    {
//        JPanel pnl = new JPanel(new BorderLayout());
//        pnl.add(createToolbar(), BorderLayout.WEST);
//        pnl.add(createToolbar(), BorderLayout.EAST);
//        
//        JPanel body = new JPanel(new BorderLayout());
//        body.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
//        body.add(createActionBar(), BorderLayout.NORTH); 
//        
//        JDialog d = new JDialog();
//        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
//        d.setModal(true); 
//        d.setTitle("Test"); 
//        d.setContentPane(new TestPanel2());
//        d.setSize(500, 500);
//        d.setVisible(true); 
        
        BigDecimal bd = new BigDecimal("0.034789");
        System.out.println(bd);
        bd = bd.setScale(2, RoundingMode.HALF_UP);  
        System.out.println(bd); 
    }

    public void xtest2() throws Exception 
    {    
        TestFrame frame = new TestFrame();
        frame.setVisible(true); 
        Thread.sleep(10000);
    }
    
    public void xtest3() throws Exception 
    {
        Set<Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet();
        for (Entry entry : entries)
        {
            if (entry.getValue() instanceof Color)
                System.out.println(entry.getKey());
        }        
    }
}
