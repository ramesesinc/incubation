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
                objid   : it.objid,
                caption : it.caption, 
                image   : decodeImage( it.thumbnail) 
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
    
    
    def selectedItem;
    def itemHandler = [
        fetchList: {
            return thumbnails; 
        }, 
        onselect: { o-> 
            
        }
    ] as ImageGalleryModel;
}