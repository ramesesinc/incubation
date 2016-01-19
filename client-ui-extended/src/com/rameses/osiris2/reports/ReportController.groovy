package com.rameses.osiris2.reports;

import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;

public abstract class ReportController { 
    
    @Binding 
    def binding; 

    @Service('ReportParameterService') 
    def reportParamSvc; 
    
    def mode = 'init';
    def entity = [:];

    def _params = [:];
    def _reportdata;
    
    public abstract def getReportData();
    public abstract String getReportName();
        
    SubReport[] getSubReports() { return null; }
    
    def getFormControl() { return null; } 

    Map getParameters() { return [:]; }
    
    def initReport() { return 'default'; }
    
    def init() { 
        mode = 'init'; 
        return initReport(); 
    } 
    
    def preview() { 
        buildReport(); 
        mode = 'view'; 
        return 'report'; 
    } 
    
    void print() { 
        buildReport(); 
        ReportUtil.print( report.report, true ); 
    } 
    
    void buildReport() { 
        _params = [:]; 
        _reportdata = getReportData(); 

        def appenv = com.rameses.rcp.framework.ClientContext.getCurrentContext().getAppEnv(); 
        appenv?.each{ k,v-> 
            def skey = k.toString().toUpperCase(); 
            _params.put( skey, v ); 
        } 

        def globalparams = reportParamSvc.getStandardParameter(); 
        if ( globalparams ) _params.putAll( globalparams );  

        def localparams = getParameters(); 
        if ( localparams ) _params.putAll( localparams ); 

        new com.rameses.osiris2.common.ReportParameterLoader().load([ params: _params ]); 
        report.viewReport(); 
    } 
        
    def report = [
        getReportName : { return getReportName() }, 
        getSubReports : { return getSubReports() }, 
        getReportData : { return _reportdata }, 
        getParameters : { return  _params } 
    ] as ReportModel 
    
    def back() { 
        mode = 'init'; 
        return 'default'; 
    } 
} 
