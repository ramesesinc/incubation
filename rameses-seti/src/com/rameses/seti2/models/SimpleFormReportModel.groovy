package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;
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
        