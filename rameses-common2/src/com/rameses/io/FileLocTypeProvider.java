/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

/**
 *
 * @author wflores 
 */
public interface FileLocTypeProvider {
    
    String getName(); 
    
    FileTransferSession createUploadSession(); 
    FileTransferSession createDownloadSession(); 
    
    void deleteFile( String name, String locationConfigId ); 
}
