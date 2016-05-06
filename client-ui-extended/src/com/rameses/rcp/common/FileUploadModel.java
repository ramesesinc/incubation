/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public class FileUploadModel {
    
    public void onstart() { 
        // to be called when the upload has started 
    }
    public void oncomplete( Object data ) {
        // to be called when the upload has ended 
    } 
    public void onerror( Throwable error ) { 
        // to be call when an error occured during the upload process 
    } 

    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">  
    
    private Provider provider;
    
    public void setProvider( Provider provider ) {
        this.provider = provider; 
    }
    
    public static interface Provider {
        String getFileName(); 
        String getFileType(); 
        long getFileSize(); 
        int getChunkCount(); 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Proxying Provider ">  
    
    public String getFileName() { 
        return (provider == null? null: provider.getFileName()); 
    } 
    public String getFileType() { 
        return (provider == null? null: provider.getFileType()); 
    } 
    public long getFileSize() { 
        return (provider == null? null: provider.getFileSize()); 
    } 
    public int getChunkCount() { 
        return (provider == null? null: provider.getChunkCount()); 
    } 
    
    // </editor-fold>    
}
