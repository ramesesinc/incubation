package com.rameses.filemgmt.models;

import com.rameses.seti2.models.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

public class FileViewModel extends CrudFormModel {
    
    def fileChooser = new javax.swing.JFileChooser(); 
    
    String getTitle() {
        return entity.title;
    }

    def open() { 
        return super.open(); 
    }
    
    void afterOpen() { 
        //com.rameses.filemgmt.FileUploadManager.addHandler( streamHandler );
    }

    void attachFile() {
        // check temporary folder for privileges 
        def tempdir = getTempDir(); 
        // select files...  
        def filter = new FileNameExtensionFilter(entity.filetype, entity.filetype);
        fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY); 
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter( filter );
        int opt = fileChooser.showOpenDialog(null); 
        if (opt != fileChooser.APPROVE_OPTION) { 
            // do nothing, user cancels the open dialog 
            return; 
        } 

        try { 
            fileChooser.getSelectedFiles().each { 
                uploadFile( tempdir, it ); 
            }
        } catch(Throwable t) { 
            MsgBox.err( t ); 
        } 
        
        def processlist = queryService.getList([ 
            _schemaname:'sys_fileitem', 
            findBy:[ parentid: entity.objid ], 
            where:[ " state in ('PENDING','PROCESSING') " ]  
        ]);  
        processlist.each{
            println '>> '+ it; 
        }
    }
    
    def uploadFile( def tempdir, def file ) {
        def skey = entity.objid +'-'+ new java.rmi.server.UID();
        def encstr = com.rameses.util.Encoder.MD5.encode( skey ); 

        def m = [ _schemaname: 'sys_fileitem' ];
        m.filesize = getFileSize( file ); 
        m.filetype = entity.filetype;
        m.parentid = entity.objid;
        m.caption = file.getName();
        m.filelocid = 'default';
        m.state = 'PENDING';
        m.bytestransferred = 0;
        m.objid = encstr; 
        
        def folder = new java.io.File( tempdir, encstr ); 
        def fui = new com.rameses.filemgmt.FileUploadItem( folder ); 
//        fui.create([ 
//            source    : file.absolutePath, 
//            filelocid : m.filelocid,
//            filetype  : m.filetype, 
//            filesize  : m.filesize,
//            fileid    : m.objid 
//        ], true ); 
        
        persistenceService.create( m ); 
//        com.rameses.filemgmt.FileUploadManager.schedule( fui );  
    } 
    
    def getTempDir() {
        def tempdir = new java.io.File( System.getProperty("java.io.tmpdir")); 
        def stmpdir = ClientContext.getCurrentContext().getAppEnv().get("filemgmt.tmpdir"); 
        if ( stmpdir ) tempdir = new java.io.File( stmpdir ); 
        
        def basedir = new java.io.File( tempdir, "rameses/fileupload"); 
        if ( !basedir.exists() ) basedir.mkdir(); 
        if ( basedir.exists()) return basedir; 
        
        throw new Exception('failed to create temporary: '+ basedir.absolutePath); 
    }
    
    long getFileSize( def file ) { 
        def raf = null; 
        def fc = null; 
        try { 
            raf = new java.io.RandomAccessFile( file, "r" );
            fc = raf.getChannel(); 
            return fc.size(); 
        } finally {
            try { fc.close(); }catch(Throwable t){;}
            try { raf.close(); }catch(Throwable t){;}
        }
    } 
    
    
    def streamHandler = [ 
        ontransfer: { info, bytesTransferred -> 
        }, 
        
        oncomplete: { info-> 
        } 
    ] as com.rameses.filemgmt.DefaultFileStreamHandler 

}