package com.rameses.filemgmt.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.filemgmt.*;

public class FileUploadLoader extends ScheduledTask {  
    
    @Service('QueryService') 
    def qrySvc; 

    @Service('PersistenceService') 
    def persistSvc; 
    
    def uploadMgr = new FileUploadManager(); 
    
    def streamHandler = [ 
        ontransfer: { info, bytesTransferred -> 
            boolean processing = ( bytesTransferred < info.filesize ); 
            persistSvc.update([ 
               _schemaname : 'sys_fileitem', 
               findBy : [ objid: info.fileid ], 
               bytestransferred : bytesTransferred, 
               state  : (processing ? 'PROCESSING' : 'COMPLETED')  
            ]); 
        }, 
        
        oncomplete: { info-> 
            persistSvc.update([ 
               _schemaname : 'sys_fileitem', 
               findBy : [ objid: info.fileid ], 
               state  : 'COMPLETED' 
            ]); 
        }
    ] as DefaultFileStreamHandler 


    
    public long getInterval() { 
        return 5000; 
    }

    public void execute() { 
        loadLocations(); 
        uploadMgr.start(); 
    } 
    
    public void setCancelled( boolean cancelled ) {
        super.setCancelled( cancelled ); 
        if ( cancelled ) doStop(); 
    }
    
    void doStart() { 
        def stmpdir = com.rameses.rcp.framework.ClientContext.currentContext.getAppEnv().get("filemgmt.tmpdir"); 
        if ( stmpdir ) uploadMgr.Helper.setTempDir( new java.io.File( stmpdir ));  

        FileUploadManager.removeHandlers(); 
        FileUploadManager.addHandler( streamHandler ); 
        com.rameses.rcp.framework.ClientContext.currentContext.taskManager.addTask( this ); 
    } 
    void doStop() { 
        uploadMgr.stop();    
    } 
    
    private void loadLocations() { 
        def params = [ _schemaname: 'sys_fileloc', where:[' 1=1 ']]; 
        qrySvc.getList( params ).each{ o-> 
            try { 
                def conf = FileConf.add( o.objid, "1".equals(o.defaultloc.toString())); 
                conf.type = o.loctype; 
                conf.readPath = o.url; 
                conf.writePath = o.filepath;
                conf.user = o.user?.name;
                conf.password = o.user?.pwd; 
                
                if ( "ftp".equalsIgnoreCase( o.loctype.toString())) {
                    conf = com.rameses.ftp.FtpLocationConf.add( o.objid );  
                    conf.host = o.url; 
                    conf.user = o.user?.name; 
                    conf.password = o.user?.pwd; 
                } 
            } catch(Throwable t) {;} 
        } 
    } 
} 