package com.rameses.seti2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;

public class ReportViewModel {
    
    def report; 
    def title = "Report"; 
    
    void view() {
        report.viewReport(); 
    }
    
    def getModel() { 
        return report; 
    }
}

     