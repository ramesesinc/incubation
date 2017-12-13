/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.io.FileLocType;
import com.rameses.io.FileLocTypeProvider;
import com.rameses.io.FileTransferSession;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores 
 */
public class FileUploadManager { 
    
    public final static HelperImpl Helper = new HelperImpl();
    
    private Map<String, FileUploadItemProc> cache = new Hashtable();
    private ExecutorService scheduler = Executors.newFixedThreadPool(100);
    
    private static FileUploadProvider provider = null; 
    
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
    
    public static FileUploadProvider getProvider() { return provider; } 
    public static void setProvider( FileUploadProvider fp ) {
        provider = fp;
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
            return getTempDir( "fileupload" ); 
        } 
        
        File getTempDir( String group ) {
            File tempdir = customTempDir; 
            if ( tempdir == null ) { 
                tempdir = new File(System.getProperty("java.io.tmpdir"));
            }         
            File basedir = new File( tempdir, "rameses/"+ group );         
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
        
        public File download( final String filelocid, final String remoteName, String filetype  ) {
            File file = getDownloadFile( remoteName ); 
            String status = getDownloadStatus( remoteName ); 
            if ( "completed".equals(status)) return file;
            
            FileConf fileloc = FileConf.get( filelocid ); 
            FileLocTypeProvider provider = FileLocType.getProvider( fileloc.getType()); 
            FileTransferSession sess = provider.createDownloadSession();
            sess.setLocationConfigId( filelocid ); 
            sess.setTargetName( remoteName+"."+filetype ); 
            sess.setFile( file ); 
            sess.setHandler(new FileTransferSession.Handler() {
                public void ontransfer(long filesize, long bytesprocessed) {
                }
                public void oncomplete() { 
                    try {
                        removeDownloadStatus( remoteName, "processing" ); 
                        createDownloadStatus( remoteName, "completed" ); 
                    } catch (Throwable t) { 
                        t.printStackTrace();  
                    } 
                }
            });
            sess.run(); 
            return null; 
        }
        
        public String getDownloadStatus( String name ) {
            File file = new File(getTempDir("filedownload"), name); 
            if ( !file.exists()) return null; 
            
            if ( new File(file, ".processing").exists()) {
                return "processing"; 
            } else if ( new File(file, ".completed").exists()) {
                return "completed"; 
            } else { 
                return null; 
            } 
        }
        public File createDownloadStatus( String name, String status ) throws Exception {
            File file = new File(getTempDir("filedownload"), name+"/."+status); 
            if ( !file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile(); 
            } 
            return file; 
        }
        public void removeDownloadStatus( String name, String status ) throws Exception {
            File file = new File(getTempDir("filedownload"), name+"/."+status); 
            if ( file.exists()) file.delete(); 
        }
        public File createDownloadFile( String name ) throws Exception { 
            createDownloadStatus( name, "processing" ); 
            File file = new File(getTempDir("filedownload"), name+"/content"); 
            if ( !file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile(); 
            } 
            return file; 
        }
        public File getDownloadFile( String name ) {
            return new File(getTempDir("filedownload"), name+"/content");             
        }
        
        void write( File file, String data ) {
            OutputStream out = null; 
            try {
                out = new FileOutputStream( file );   
                out.write( data.getBytes()  );  
                out.flush(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally { 
                try { out.close(); }catch(Throwable t){;} 
            } 
        } 
        String read( File file ) {
            FileInputStream inp = null; 
            try {
                inp = new FileInputStream( file ); 
                StringBuilder sb = new StringBuilder(); 
                byte[] bytes = new byte[1024]; 
                int read = -1;
                while ((read=inp.read(bytes)) != -1) {
                    sb.append(new String(bytes, 0, read)); 
                }
                return sb.toString(); 
            } catch(Throwable t) {
                return null; 
            } finally { 
                try { inp.close(); }catch(Throwable t){;} 
            } 
        } 
        
        public ImageIcon getDownloadImage( String name ) throws Exception { 
            File file = getDownloadFile( name ); 
            return new javax.swing.ImageIcon( file.toURI().toURL() );
        }
    }
}
