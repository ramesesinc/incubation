package com.rameses.osiris2.report;
 
import com.rameses.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import net.sf.jasperreports.engine.*;
import com.rameses.osiris2.reports.ReportDataSource;
import com.rameses.osiris2.reports.BasicReportModel
        
public class DynamicReportFormModel {
    
    @FormTitle
    def title;
    
    def reportModel;
    def reportData;
    
    def mainReport;
    def reportHandler = [
        getReportData: { return reportData; }, 
        getMainReport: { return mainReport; },
        isAllowSave: { return true; }, 
        isAllowEdit: { return false; } 
    ] as BasicReportModel;
    
    public def getReport() {
        title = reportModel.title; 
        if ( mainReport == null ) { 
            buildReport(); 
        } 
        reportHandler.viewReport(); 
        return reportHandler; 
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
            mainReport = SimpleTableReportBuilder.buildReport( tbl );
        } catch(RuntimeException re) {
            throw re; 
        } catch(Throwable e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
}     