package com.rameses.seti2.models;
 
import com.rameses.osiris2.reports.ReportModel;

import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;

public class SimpleFormReportModel extends ReportModel {
    
    String title;
    def reportHandler;
    def _reportData;
    
    public Object getReportData() {
        if( !_reportData ) {
            _reportData = reportHandler?.getData();
        }
        return _reportData;
    }
    
    public String getReportName() {
        return reportHandler?.getReportName();    
    }
    
    public SubReport[] getSubReports() { 
        return reportHandler?.getSubReports(); 
    }
    
    final def getModel() { 
        return this; 
    }
    
    //this is called if you want to display the report directly
    void view() { 
        viewReport(); 
    } 
    
    void refresh() {
        _reportData = null;
    }
    
}
