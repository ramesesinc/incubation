/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.ftp.FtpManager;
import com.rameses.ftp.FtpSession;
import com.rameses.io.FileLocTypeProvider;
import com.rameses.io.FileTransferSession;

/**
 *
 * @author wflores 
 */
public class FTPLocTypeProvider implements FileLocTypeProvider { 

    private final static String PROVIDER_NAME = "ftp";  
    
    public String getName() { 
        return PROVIDER_NAME; 
    }
    
    public FileTransferSession createUploadSession() { 
        return new UploadSession(); 
    }
    public FileTransferSession createDownloadSession() { 
        return new DownloadSession(); 
    }    

    
    private class DownloadSession extends FileTransferSession implements FtpSession.Handler {
        private FtpSession sess; 

        public void cancel() {
            super.cancel(); 
            if ( isCancelled() ) {
                disconnect(); 
            } 
        } 
        
        public void run() { 
            if ( isCancelled()) {
                disconnect(); 
                return; 
            }
            
            sess = FtpManager.createSession( getLocationConfigId() ); 
            sess.setBufferSize( 100 * 1024 ); 
            sess.setHandler(this);
            sess.download( getTargetName(), getFile() ); 
            disconnect(); 
        } 
        
        private void disconnect() {
            try {
                sess.disconnect(); 
            } catch(Throwable t){
                // do nothing 
            } finally {
                sess = null; 
            }
        } 
        
        public void onupload( long filesize, long bytesprocessed ) { 
        } 

        public void oncompleted() { 
            Handler handler = getHandler(); 
            if ( handler == null ) return; 

            handler.oncomplete(); 
        } 
    }
    
    private class UploadSession extends FileTransferSession implements FtpSession.Handler {
        
        private FtpSession sess; 

        public void cancel() {
            super.cancel(); 
            if ( isCancelled() ) {
                disconnect(); 
            } 
        } 
        
        public void run() { 
            if ( isCancelled()) {
                disconnect(); 
                return; 
            }
            
            sess = FtpManager.createSession( getLocationConfigId() ); 
            sess.setBufferSize( 100 * 1024 ); 
            sess.setHandler( this ); 
            sess.upload( getTargetName(), getFile(), getOffset()); 
            disconnect(); 
        } 
        
        private void disconnect() {
            try {
                sess.disconnect(); 
            } catch(Throwable t){
                // do nothing 
            } finally {
                sess = null; 
            }
        } 
        
        public void onupload( long filesize, long bytesprocessed ) { 
            Handler handler = getHandler(); 
            if ( handler == null ) return; 
            
            handler.ontransfer( filesize, bytesprocessed ); 
        } 

        public void oncompleted() { 
            Handler handler = getHandler(); 
            if ( handler == null ) return; 

            handler.oncomplete(); 
        }
    }
}
