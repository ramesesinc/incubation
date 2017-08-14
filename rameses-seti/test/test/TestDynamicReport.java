/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.osiris2.report.CrosstabReport;
import com.rameses.osiris2.report.CrosstabReportBuilder;
import com.rameses.osiris2.reports.ReportDataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author rameses
 */
public class TestDynamicReport extends TestCase {
    
    public TestDynamicReport(String testName) {
        super(testName);
    }
    
    public void test0() throws Exception {
        //List datalist = getData(); 
        List datalist = new ArrayList();
        datalist.add(createData("5100001", "2017-08-01", "A1", 100.0));
        datalist.add(createData("5100001", "2017-08-01", "A2", 150.0));
        datalist.add(createData("5100002", "2017-08-02", "A2", 250.0));
        datalist.add(createData("5100002", "2017-08-02", "A3", 350.0));
        
        CrosstabReport tbl = new CrosstabReport();
        tbl.addColumn("Receipt No.", "receiptno", java.lang.String.class); 
        tbl.addColumn("Receipt Date", "receiptdate", java.util.Date.class); 
        tbl.addColumn("Amount", "amount", java.lang.Number.class); 
        tbl.addColumn("AcctCode", "acctcode", java.lang.String.class); 
        
        tbl.addRowGroup("receiptno");
        tbl.addColumnGroup("acctcode");
        tbl.addMeasureGroup("amount");
        
        //tbl.getFieldProperty("amount").setAlignment("right"); 
        
        CrosstabReportBuilder b = new CrosstabReportBuilder();
        JasperReport jrpt = b.buildReport( tbl );
        ReportDataSource ds = new ReportDataSource( datalist );
        JasperPrint jprint = JasperFillManager.fillReport( jrpt, new HashMap(), ds );
        JasperViewer viewer = new JasperViewer( jprint );
        viewer.setVisible(true); 
        
        new LinkedBlockingQueue().poll(2, TimeUnit.MINUTES); 
    }
    
    private Map createData( String receiptno, String receiptdate, String acctcode, Number amount ) {
        Map map = new HashMap(); 
        map.put("receiptno", receiptno);
        map.put("receiptdate", java.sql.Date.valueOf(receiptdate));
        map.put("acctcode", acctcode); 
        map.put("amount", amount);
        return map;
    }
    
}
