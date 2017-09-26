package com.rameses.seti2.models;
 
import com.rameses.osiris2.reports.ReportModel;

public class SimpleFormReportModel extends ReportModel {
    
    String title;
    def reportHandler;
    
    public Object getReportData() {
        return reportHandler.getData();
    }
    
    public String getReportName() {
        return reportHandler.getReportName();    
    }
    
    final def getModel() { 
        return this; 
    }
    
    //this is called if you want to display the report directly
    void view() { 
        viewReport(); 
    } 
}
