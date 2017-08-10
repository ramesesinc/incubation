/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.osiris2.report.CrosstabReport;
import com.rameses.osiris2.report.CrosstabReportBuilder;
import com.rameses.osiris2.report.SimpleTableReport;
import com.rameses.osiris2.reports.ReportDataSource;
import com.rameses.service.ScriptServiceContext;
import java.util.ArrayList;
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
        datalist.add(createData(2017, "A1", 100));
        datalist.add(createData(2017, "A2", 150));
        datalist.add(createData(2016, "A3", 150));
        datalist.add(createData(2016, "A2", 250));
        datalist.add(createData(2017, "A4", 250));
        
        CrosstabReport tbl = new CrosstabReport();
        tbl.addColumn("Year", "year", java.lang.Number.class); 
        tbl.addColumn("AcctCode", "acctcode", java.lang.String.class); 
        tbl.addColumn("Amount", "amount", java.lang.Number.class); 
        
        tbl.setRowGroup("year"); 
        tbl.setColumnGroup("acctcode"); 
        tbl.setMeasure("amount"); 
        
        //tbl.getFieldProperty("amount").setAlignment("right"); 
        
        CrosstabReportBuilder b = new CrosstabReportBuilder();
        JasperReport jrpt = b.buildReport( tbl );
        ReportDataSource ds = new ReportDataSource( datalist );
        JasperPrint jprint = JasperFillManager.fillReport( jrpt, new HashMap(), ds );
        JasperViewer viewer = new JasperViewer( jprint );
        viewer.setVisible(true); 
        
        new LinkedBlockingQueue().poll(2, TimeUnit.MINUTES); 
    }
    
    private Map createData( int year, String acctcode, Number amount ) {
        Map map = new HashMap(); 
        map.put("year", year);
        map.put("acctcode", acctcode); 
        map.put("amount", amount);
        return map;
    }
    
}
