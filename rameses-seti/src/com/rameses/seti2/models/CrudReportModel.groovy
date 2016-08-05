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


/***
* modes
*    view-report = directly calls report. There is no back button
*    query = shows query page before the report
*    preview = called after query. There is a back page  
*/
public class CrudReportModel extends ReportModel {
    
    @Caller
    def caller;
    
    @Controller
    def workunit;
    
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
        s = workunit?.info?.workunit_properties?.reportName;
        if( s != null ) return s;
        throw new Exception("Please specify a report name");
    }
    
    final def getModel() { 
        return this; 
    }
    
    //this is called if you want to display the report directly
    void view() { 
        viewReport(); 
    } 
    
}
        