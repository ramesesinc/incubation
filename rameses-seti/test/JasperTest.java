/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rameses.osiris2.client.InvokerOpener;
import com.rameses.osiris2.report.ReportColumn;
import com.rameses.osiris2.report.SimpleTableReport;
import com.rameses.osiris2.report.SimpleTableReportBuilder;
import com.rameses.rcp.common.MsgBox;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author dell
 */
public class JasperTest extends TestCase {
    
    public JasperTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
        
    public static class MyDataSource implements JRDataSource {
        private List list = new ArrayList();
        private Map map;
        private Iterator iterator;
        
        public MyDataSource( List list ) {
            this.list = list;
            this.iterator = list.iterator();
        }
        
        public boolean next() throws JRException {
            boolean b = iterator.hasNext();
            if( b ) {
                map =(Map)iterator.next();
            }
            return b;
        }

        public Object getFieldValue(JRField jrf) throws JRException {
            return map.get(jrf.getName());
        }
    }
    
    public static Map createRecord(String lastname, String firstname, int age, double sal) {
        Map m = new HashMap();
        m.put( "lastname", lastname );
        m.put( "firstname", firstname );
        m.put( "age", age);
        m.put("salary", new BigDecimal(sal));
        return m;
    }

    
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        List list = new ArrayList();
        list.add(  createRecord("Nazareno","The Quick brown fox jumped over the lazy dog over and over again",46,25000)  );
        list.add(  createRecord("Flores","Worgie",35,35256.78)  );
        MyDataSource md = new MyDataSource(list);
        
        SimpleTableReport tb = new SimpleTableReport();
        tb.setTitle("My First Report");
        tb.setName("my-first-report");
        ReportColumn rc = tb.addColumn("Last Name", "lastname", String.class,100);
        rc = tb.addColumn("First Name", "firstname", String.class,100);
        rc = tb.addColumn("Age", "age", Integer.class,20);
        rc = tb.addColumn("Salary", "salary", BigDecimal.class,40);
        
        JasperReport jreport = SimpleTableReportBuilder.buildReport(tb);
        JasperPrint jp = JasperFillManager.fillReport(jreport, new HashMap(), md);
        JasperViewer jv = new JasperViewer(jp);
        jv.viewReport(jp);
        
        MsgBox.alert("hold");
        
        /*
        JasperReport jreport = JasperCompileManager.compileReport(jasperDesign);
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = (Connection)DriverManager.getConnection("jdbc:mysql://localhost/charlesdb","root","1234");
        HashMap m = new HashMap();
        m.put("ReportTitle", "My First Report");
        JasperPrint jp = JasperFillManager.fillReport(jreport, m, conn);
        JasperViewer jv = new JasperViewer(jp);
        jv.viewReport(jp);
        */ 

    }
}
