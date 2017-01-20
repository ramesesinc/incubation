package com.rameses.filemgmt.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.filemgmt.*;
import java.util.concurrent.Callable;

public class FileUploadLoader extends ScheduledTask { 
        
    @Service('DateService') 
    def svc; 
    
    def uploadMgr = new FileUploadManager();
    
    def handler = { fileinfo, bytes-> 
        println 'fileinfo-> ' + fileinfo;
    }    

    
    public long getInterval() {
        return 5000; 
    }

    public void execute() { 
        println 'reading tempdir '+ uploadMgr.getTempDir(); 
        uploadMgr.start( handler ); 
    } 
    
    public void setCancelled( boolean cancelled ) {
        super.setCancelled( cancelled ); 
        if ( cancelled ) doStop(); 
    }
    
    void doStart() { 
        
        com.rameses.rcp.framework.ClientContext.currentContext.taskManager.addTask( this ); 
    }
    void doStop() {
        uploadMgr.stop();    
    }
}