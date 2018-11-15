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
import java.rmi.server.UID;

/***
* modes
*    view-report = directly calls report. There is no back button
*    query = shows query page before the report
*    preview = called after query. There is a back page  
*/
public class FormReportModel extends ReportModel {
    
    @Caller
    def caller;
    
    @Controller
    def workunit;
    
    @Binding
    def binding;
    
    @Service("FormReportService")
    def reportService;
        
    int _captionWidth;
    
    String reportId;
    def query = [:];
    def data;
    int status;
    Map headers;
    def task;
    def txnid;
    
    def printmode = "preview";  //preview or printer
    def mode = "query";
    boolean aborted = false;
    
    //this determins what was first clicked for the back button to decide
    //where it will go back to. if true = query, else _close;
    boolean queryView = false;
    
    private static def scheduler = java.util.concurrent.Executors.newScheduledThreadPool(100);
    
    public Map getParameters() {
        return headers;
    }
    
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
        if(data?.data) 
            return data.data;
        else
            throw new Exception("No available report data");
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
    
    public String getReportId() {
        String s = invoker.properties.reportId;
        if( s!=null ) {
            return s;
        }
        s = workunit?.info?.workunit_properties?.reportId;
        if( s != null ) return s;
        throw new Exception("Please specify a report id");
    }

    final def getModel() { 
        return this; 
    }
    
    def formControls = [];
    
    public void initControl( def o ) {
        //do nothing
    }

    def getConfInfo() {
        return [reportid :getReportId(), txnid: txnid];    
    }
    
    def query() {
        queryView = true;
        mode = "query";
        //build control parameters
        def m = getConfInfo();
        formControls.clear();
        def p = reportService.getParameters(m);
        if( !p.parameters ) {
            throw new Exception("No parameters specified! Please specify at least one parameter");
        }
        for( x in p.parameters ) {
            def i = FormControlUtil.createControl( x, query, "query" );
            if(i==null) continue;
            if(_captionWidth!=null) {
                i.captionWidth = _captionWidth;
            }
            initControl(i);
            formControls << i;
        }
        return mode;
    }
    
    

    void mergeData(def newData) {
        if(newData.data==null) return;
        if(data.data==null) {
            data.data = newData.data;
        }
        else if( data.data instanceof Map ) {
            data.data.putAll( newData.data);
        }    
        else if( data.data instanceof List ) {
            data.data.addAll( newData.data);
        }
    }

    //status is intended for long running reports that needs to requery.
    boolean processReport() {
        mode = "processing";
        def m =  getConfInfo();
        m.parameters = query;
        if(status) m.status = status;
        def newData = reportService.getData(m);
        if(newData.status!=null) status = newData.status;
        if( status !=0 ) {
            mergeData( newData );
            return false;
        }
        else {
            mergeData(newData);
            if(newData.headers) headers = newData.headers;
            return true;
        }
    }
    
    def processor = { 
        if(aborted) {
            def m =  getConfInfo();
            aborted = false;
            def z = reportService.abort(m);
            MsgBox.alert('process aborted');
            if( queryView == true ) {
                mode = "query";
            }
            else {
                mode = "_close";
            }
        }
        else {
            boolean t = processReport();
            if( !t ) {
                mode = "processing";
                scheduler.schedule( processor, 2, java.util.concurrent.TimeUnit.SECONDS );
            } 
            else {   
                viewReport(); 
                if(printmode == 'preview') {
                    mode = "preview";
                }
                else {
                    ReportUtil.print( report, true ); 
                    mode = "query";
                }
                
            }
        }
        if(binding==null) {
            return mode;
        }
        else {
            binding.fireNavigation(mode);
            return mode; 
        }
    } as Runnable;
       
    void abort() {
        aborted = true;
    }
    
    def preview() { 
        mode = "preview";
        queryView = false;
        printmode = "preview";
        txnid = "QRPT"+new UID();
        data = [:];
        return processor();
    } 
    
    def sendToPrint() { 
        printmode = "printer";
        txnid = "QRPT"+new UID();
        data = [:];
        return processor();
    } 
    
    def back() { 
        mode = "query";
        return mode;
    } 
}

     