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
    String tmpDir;
    
    String getTitle() {
        return entity.title;
    }
    
    void afterOpen() {
        tmpDir = ClientContext.getCurrentContext().getAppEnv().get("filemgmt.tmp");
        if(!tmpDir) tmpDir = System.getProperty("java.io.tmpdir");
    }

    def uploadFile(def file) {
        def m = [_schemaname: 'sys_fileitem' ];
        m.parentid = entity.objid;
        m.caption = file.getName();
        m.state = 'PROCESSING';
        m.filelocid = 'default';
        persistenceService.create(m);
    }
    
    def attachFile() {
        fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY); 
        def filter = new FileNameExtensionFilter(entity.filetype, entity.filetype);
        fileChooser.setFileFilter( filter );
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(true);
        int opt = fileChooser.showSaveDialog(null); 
        if (opt == fileChooser.APPROVE_OPTION) { 
            def files = fileChooser.getSelectedFiles(); 
            files.each {
                uploadFile(it);
            };
        } 
    }
    
}