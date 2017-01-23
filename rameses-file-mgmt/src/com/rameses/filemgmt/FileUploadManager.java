/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.rcp.common.CallbackHandlerProxy;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author rameses1
 */
public class FileUploadManager {
    
    private Map<String, FileUploadItem> cache = new Hashtable();
    private ExecutorService scheduler = Executors.newFixedThreadPool(100);

    public File getTempDir() {
        File basedir = new File(System.getProperty("java.io.tmpdir")); 
        File tempdir = new File( basedir, "rameses/fileupload"); 
        if ( !tempdir.exists()) {
            try {
                tempdir.mkdirs(); 
            } catch (RuntimeException re) {
                throw re; 
            } catch (Throwable t) { 
                throw new RuntimeException( t.getMessage(), t ); 
            } 
        } 
        return tempdir; 
    }
    
    public void start( Object handler ) {
        File tempdir = getTempDir(); 
        File[] files = tempdir.listFiles(); 
        for ( File file : files ) {
            if ( !file.isDirectory() ) continue; 
            if ( file.getName().endsWith("~completed~")) {
                try { 
                    removeFile( file ); 
                } catch(Throwable t){
                    //do nothing 
                } finally {
                    continue; 
                }
            }
            
            if ( file.getName().endsWith("~")) continue; 
            if ( cache.containsKey(file.getName())) continue; 
            
            File statusFile = new File( file, ".status");
            if ( !statusFile.exists() || statusFile.isDirectory() ) {
                try { 
                    removeFile( file ); 
                } catch(Throwable t){
                    //do nothing 
                } finally {
                    continue; 
                }
            } 
                        
            FileInputStream fis = null; 
            Properties props = new Properties();             
            try { 
                fis = new FileInputStream( statusFile );
                props.load( fis ); 
            } catch(Throwable t) {
                t.printStackTrace(); 
                continue; 
            } finally {
                try { fis.close(); }catch(Throwable t){;} 
            }
            
            FileUploadItem fui = new FileUploadItem( file, props ); 
            fui.setContentFile( new File( file, "content")); 
            fui.setStatusFile( statusFile );
                        
            FileItemHandler filehandler = new FileItemHandler();
            filehandler.uploadHandler = new CallbackHandlerProxy( handler );  
            fui.setHandler( filehandler ); 
            cache.put(file.getName(), fui); 
            scheduler.submit( fui );  
        } 
    }

    public void stop() { 
        for ( FileUploadItem fui : cache.values()) {
            fui.cancel(); 
        } 
        cache.clear(); 
    } 

    private void removeFile( File file ) { 
        if ( file==null || !file.exists()) return; 

        
        if ( file.isDirectory()) {
            File[] files = file.listFiles(); 
            for ( File child : files ) {
                removeFile( child ); 
            }
        } 

        try { 
            file.delete(); 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        }
    }    
    
    private class FileItemHandler implements FileUploadItem.Handler {
        
        CallbackHandlerProxy uploadHandler; 
        
        public void onupload( FileUploadItem item, int pos, byte[] bytes ) { 
            if ( uploadHandler == null ) return; 
            
            uploadHandler.call(new Object[]{ item.getConf(), bytes });  
        } 

        public void onend( FileUploadItem item ) { 
            File oldfile = item.getFile(); 
            File newfile = new File( oldfile.getParentFile(), oldfile.getName()+"~completed~"); 
            oldfile.renameTo( newfile ); 
            removeFile( newfile ); 
            cache.remove( oldfile.getName() ); 
        } 
        
        public void oncorrupted( FileUploadItem item ) {
            File file = item.getFile(); 
            File cfile = new File( file.getParentFile(), file.getName()+"~corrupted~"); 
            file.renameTo( cfile );  
            cache.remove( file.getName() ); 
        } 
    }
}
