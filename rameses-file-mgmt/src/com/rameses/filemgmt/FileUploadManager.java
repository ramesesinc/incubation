/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author wflores 
 */
public class FileUploadManager {
    
    public final static HelperImpl Helper = new HelperImpl();
    
    private Map<String, FileUploadItemProc> cache = new Hashtable();
    private ExecutorService scheduler = Executors.newFixedThreadPool(100);
    
    public void start() { 
        File tempdir = Helper.getTempDir(); 
        File[] files = tempdir.listFiles( new ValidFileFilter());  
        for ( File file : files ) { 
            FileUploadItem item = new FileUploadItem( file ); 
            item.setHandler( new ItemHandler( cache)); 
            item.open(); 
            
            FileUploadItemProc proc = item.createProcessHandler(); 
            if ( proc != null && !cache.containsKey( item.getName())) {
                cache.put( item.getName(), proc ); 
                scheduler.submit( proc ); 
                
            } else if ( item.isModeCompleted() ) { 
                item.remove(); 
            }
        } 
    } 

    public void stop() { 
        for ( FileUploadItemProc proc : cache.values()) { 
            try { 
                proc.cancel(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
            } 
        } 
        cache.clear(); 
        scheduler.shutdown(); 
    } 
    
    private class ValidFileFilter implements FileFilter { 
        
        FileUploadManager root = FileUploadManager.this; 
        
        public boolean accept(File file) { 
            if ( !file.isDirectory() ) return false; 
            if ( file.getName().endsWith("~")) return false; 
            if ( root.cache.containsKey(file.getName())) return false; 

            File child = new File( file, ".immediate");
            if ( child.exists()) return false; 

            child = new File( file, ".conf"); 
            if ( !child.exists()) return false; 
            
            child = new File( file, "content.index");
            return child.exists(); 
        } 
    } 
    
    private static class ItemHandler implements FileUploadItem.Handler {

        private Map cache; 
        
        ItemHandler( Map cache ) {
            this.cache = cache; 
        }
        
        public void oncompleted( FileUploadItem item ) { 
            if ( item == null ) return; 
            
            if ( cache != null ) { 
                String skey = item.getName(); 
                cache.remove( skey );
            } 

            Map data = item.getConfigFile().copyData(); 
            item.removeTempFile( ".immediate" ); 
            if ( item.isModeCompleted() ) {
                stream_handler.oncomplete( data ); 
            }
        } 

        public void ontransfer(FileUploadItem item, long filesize, long bytesprocessed) {
            Map data = item.getConfigFile().copyData(); 
            stream_handler.ontransfer( data, bytesprocessed ); 
        } 
    }
        
    private final static StreamHandlerProxy stream_handler = new StreamHandlerProxy();  
    private final static ExecutorService thread_pool = Executors.newSingleThreadExecutor(); 
    
    public static synchronized void schedule( FileUploadItem item ) { 
        item.setHandler( new ItemHandler( null )); 
        item.open(); 
        
        FileUploadItemProc proc = item.createProcessHandler(); 
        if ( proc != null ) { 
            thread_pool.submit( proc ); 
            
        } else if ( item.isModeCompleted() ) {
            item.remove(); 
        } 
    } 

    public static void removeHandlers() {
        synchronized( stream_handler ) {
            stream_handler.clear(); 
        }
    }
    public static void removeHandler( FileStreamHandler handler ) {
        synchronized( stream_handler ) {
            stream_handler.remove( handler ); 
        }
    }
    public static void addHandler( FileStreamHandler handler ) {
        synchronized( stream_handler ) {
            stream_handler.add( handler ); 
        }
    } 
    
    private static class StreamHandlerProxy implements FileStreamHandler { 
        
        private List<FileStreamHandler> list = new ArrayList(); 
        
        void clear() { 
            list.clear(); 
        } 
        void remove( FileStreamHandler handler ) {
            if ( handler != null ) list.remove( handler );
        }
        void add( FileStreamHandler handler ) {
            if ( handler == null ) return; 
            
            if ( !list.contains( handler )) {
                list.add( handler );
            }
        }
        
        public void ontransfer( Map data, long bytesTransferred ) { 
            FileStreamHandler[] items = list.toArray(new FileStreamHandler[]{}); 
            thread_pool.submit( new StreamHandlerOnTransferProc( items, data, bytesTransferred)); 
        }

        public void oncomplete(Map data) { 
            FileStreamHandler[] items = list.toArray(new FileStreamHandler[]{}); 
            thread_pool.submit( new StreamHandlerOnCompleteProc( items, data)); 
        }
    } 
    private static class StreamHandlerOnTransferProc implements Runnable {

        private FileStreamHandler[] items; 
        private long bytesTransferred; 
        private Map data; 
        
        StreamHandlerOnTransferProc( FileStreamHandler[] items, Map data, long bytesTransferred ) {
            this.bytesTransferred = bytesTransferred; 
            this.items = items;
            this.data = data; 
        }
        
        public void run() {
            if ( items == null || items.length==0 ) return; 

            for ( FileStreamHandler item : items ) { 
                try { 
                    item.ontransfer( data, bytesTransferred ); 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                }
            }
        } 
    }
    private static class StreamHandlerOnCompleteProc implements Runnable {

        private FileStreamHandler[] items; 
        private Map data; 
        
        StreamHandlerOnCompleteProc( FileStreamHandler[] items, Map data ) {
            this.items = items; 
            this.data = data; 
        }
        
        public void run() {
            if ( items == null || items.length==0 ) return; 

            for ( FileStreamHandler item : items ) { 
                try { 
                    item.oncomplete( data ); 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                }
            } 
        }     
    }    
    
    private final static Object HELPER_LOCKED = new Object();    
    public static class HelperImpl {
        
        private File customTempDir;

        public void setTempDir( File tempdir ) {
            this.customTempDir = tempdir; 
        }
        public File getTempDir() { 
            File tempdir = customTempDir; 
            if ( tempdir == null ) { 
                tempdir = new File(System.getProperty("java.io.tmpdir"));
            }         
            File basedir = new File( tempdir, "rameses/fileupload");         
            try {
                if ( !basedir.exists()) { 
                    basedir.mkdir(); 
                } 
                return basedir; 

            } catch (RuntimeException re) {
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            } 
        } 
        
        public long getFileSize( File file ) throws Exception { 
            RandomAccessFile raf = null; 
            FileChannel fc = null; 
            try { 
                raf = new RandomAccessFile( file, "r" );
                fc = raf.getChannel(); 
                return fc.size(); 
            } finally {
                try { fc.close(); }catch(Throwable t){;}
                try { raf.close(); }catch(Throwable t){;}
            }
        } 
    }
}
