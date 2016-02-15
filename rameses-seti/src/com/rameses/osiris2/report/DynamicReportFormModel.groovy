package com.rameses.osiris2.report;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import net.sf.jasperreports.engine.*;
import com.rameses.osiris2.reports.ReportDataSource;
        
public class DynamicReportFormModel {
    
    @FormTitle
    def title;
    
    def reportModel;
    def reportData;
    def report;
    
    public JasperPrint getReport() {
        title = reportModel.title;
        if ( report == null ) {
            buildReport();
        }
        return report;
    }
    
    public def preview() {
        return "report";
    }
    
    public void buildReport() {
        try {
            def tbl = new SimpleTableReport();
            tbl.setTitle(reportModel.title);
            reportModel.columns.each { o->
                tbl.addColumn(o.caption, o.name, o.datatypeClass, 100 );
            }
            JasperReport jr = SimpleTableReportBuilder.buildReport( tbl );
            ReportDataSource ds = new ReportDataSource( reportData );
            report = JasperFillManager.fillReport( jr, new HashMap(), ds );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
        
}