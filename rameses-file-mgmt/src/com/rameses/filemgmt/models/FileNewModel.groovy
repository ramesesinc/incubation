package com.rameses.filemgmt.models;

import com.rameses.seti2.models.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.Base64Cipher;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileNewModel  {
        
    @Service('FileUploadService') 
    def fileUploadSvc;
    
    @Script('FileType') 
    def fileType;
    
    def info = [:];
    
    def handler;    
    def base64 = new Base64Cipher();
    def _entity = [ objid:'FILE'+ new java.rmi.server.UID(), info: [:]]; 
    
    def getFileTypes() {
        return fileType.getTypes(); 
    } 
    
    def getEntity() { 
        return _entity; 
    }
    
    def doOk() {
        def helper = com.rameses.filemgmt.FileUploadManager.Helper; 
        def fileitems = [];
        
        entity.items = []; 
        attachments.each{
            def skey = entity.objid +'-'+ new java.rmi.server.UID();
            def encstr = com.rameses.util.Encoder.MD5.encode( skey ); 
            def encparentid = com.rameses.util.Encoder.MD5.encode( entity.objid ); 

            def m = [:];
            m.filesize = helper.getFileSize( it.file ); 
            m.filetype = entity.filetype;
            m.parentid = entity.objid;
            m.filelocid = 'default';
            m.caption = it.file.getName();
            m.state = 'PENDING';
            m.bytestransferred = 0;
            m.objid = encstr; 
            m.thumbnail = base64.encode((Object) it.image); 
            entity.items << m; 
            
            fileitems << [
                source    : it.file.absolutePath, 
                filelocid : m.filelocid,
                filetype  : m.filetype, 
                filesize  : m.filesize,
                fileid    : m.objid 
            ]
        } 
        entity.info = info;
        
        def fup = com.rameses.filemgmt.FileUploadManager.provider; 
        _entity = fup.save( entity ); 
        //_entity = fileUploadSvc.upload( entity ); 
        
        def tempdir = helper.getTempDir(); 
        fileitems.each{
            def folder = new java.io.File( tempdir, it.fileid ); 
            def fui = new com.rameses.filemgmt.FileUploadItem( folder ); 
            fui.create( it, true ); 
            com.rameses.filemgmt.FileUploadManager.schedule( fui ); 
        } 
        
        try { 
            if ( handler ) handler( entity ); 
        } finally {
            return "_close";
        }
    }

    def doCancel() {
        return "_close";
    }    
    
    def attachments = [];
    def listHandler = [
        fetchList: {
            return attachments; 
        }
    ] as ImageGalleryModel;
    
    def getCardViewName() { 
        def filetype = entity?.filetype;
        if ( filetype.toString().matches('jpg|png')) {
            return 'image'; 
        } else {
            return 'empty'; 
        }
    }
    
    def fileChooser; 
    def selectedAttachment;
    
    void attachFile() { 
        def filetype = fileTypes.find{ it.objid==entity.filetype }
        if ( !filetype ) throw new Exception('file type not supported');

        if ( fileChooser == null ) {
            fileChooser = new JFileChooser(); 
            fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
            fileChooser.setAcceptAllFileFilterUsed( false );
        }
        
        fileChooser.setMultiSelectionEnabled( filetype.multiselect ); 
        if ( entity.filetype == 'jpg' ) { 
            fileChooser.setFileFilter( new FileNameExtensionFilter( filetype.title, "jpg", "jpeg"));
        } else if ( entity.filetype == 'png' ) {
            fileChooser.setFileFilter( new FileNameExtensionFilter( filetype.title, "png"));
        } else { 
            fileChooser.setFileFilter( new FileNameExtensionFilter( filetype.title, filetype.objid ));
        } 
        
        def scaler = new ImageScaler();
        int opt = fileChooser.showOpenDialog( listHandler.binding?.owner ); 
        if ( opt == JFileChooser.APPROVE_OPTION ) { 
            def newlist = []; 
            def files = fileChooser.getSelectedFiles(); 
            files.each{ 
                def item = [:];
                item.file = it; 
                item.caption = it.name; 
                
                def image = scaler.createThumbnail( it );  
                item.image = scaler.getBytes( image ); 
                newlist << item; 
            }
            attachments.addAll( newlist); 
            listHandler.reload();
        } 
    }
    
    void removeAttachment() {
        if ( !selectedAttachment ) return; 
        
        int idx = attachments.indexOf( selectedAttachment ); 
        if ( idx >= 0 ) { 
            attachments.remove( idx ); 
            listHandler.remove( idx ); 
        } 
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
}