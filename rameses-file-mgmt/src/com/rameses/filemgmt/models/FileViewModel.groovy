package com.rameses.filemgmt.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.framework.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.seti2.models.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

public class FileViewModel extends CrudFormModel { 
    
    @Script('FileType') 
    def fileTypeUtil;
    
    def fileType;

    def thumbnails = [];

    final def base64 = new com.rameses.util.Base64Cipher();
    
    String getTitle() {
        return entity.title;
    }
    
    void afterOpen() { 
        fileType = fileTypeUtil.getType( entity.filetype );
        
        thumbnails.clear();
        entity.items.each{
            thumbnails << [ 
                objid     : it.objid,
                caption   : it.caption, 
                filelocid : it.filelocid, 
                image     : decodeImage( it.thumbnail) 
            ]; 
        }
    }
    
    def decodeImage( o ) {
        if ( o instanceof String ) {
            if ( base64.isEncoded( o)) {
                return base64.decode( o ); 
            }
        }
        return o; 
    }
    
    def uploadHelper = com.rameses.filemgmt.FileUploadManager.Helper; 
    
    def selectedItem;
    def itemHandler = [
        fetchList: {
            return thumbnails; 
        }, 
        onselect: { o-> 
            def stat = uploadHelper.getDownloadStatus( o.objid ); 
            if ( stat == null ) {
                o.message = 'downloading in progress...'; 
                uploadHelper.download( o.filelocid, o.objid, entity.filetype ); 
            } else if ( stat == 'processing' ) {
                o.message = 'downloading in progress...'; 
            } else if ( stat == 'completed' ) {
                o.message = null; 
                o.actualimage = uploadHelper.getDownloadImage( o.objid ); 
                binding.refresh('selectedItem.actualimage'); 
            }
        }
    ] as ImageGalleryModel;
    
    def getCardname() {
        if ( selectedItem.actualimage ) {
            return 'image'; 
        } else {
            return 'noimage'; 
        }
    }
}