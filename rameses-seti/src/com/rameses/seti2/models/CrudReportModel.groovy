package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import com.rameses.util.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;

public class CrudReportModel extends ReportModel {
    
    def mode = "view-report";
    def entity;
    
    public String getTitle() {
        String s = invoker.caption;
        if( s!=null ) {
            return s;
        }
        s = workunit.title;
        if( s != null ) return s;
        return "";
    }
    
    public Object getReportData() {
        return entity;
    }
    
    public String getReportName() {
        String s = invoker.properties.reportName;
        if( s!=null ) {
            return s;
        }
        s = workunit.info.workunit_properties.reportName;
        if( s != null ) return s;
        throw new Exception("Please specify a report name");
    }
    
    def preview() { 
        viewReport(); 
        mode = 'preview'; 
        return mode; 
    } 
    
    //this is called if you want to display the report directly
    def view() { 
        viewReport(); 
        mode = 'view-report'; 
        return mode; 
    } 
    
    void print() { 
        viewReport(); 
        ReportUtil.print( report, true ); 
    } 
            
    def back() { 
        mode = 'query'; 
        return 'default'; 
    } 
    
}
        