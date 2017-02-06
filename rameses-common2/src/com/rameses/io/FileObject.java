/*
 * FileObject.java
 *
 * Created on May 16, 2014, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import com.rameses.util.Base64Cipher;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.rmi.server.UID;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class FileObject 
{
    public final static int CHUNK_SIZE     = (64 * 1024);
    public final static int MIN_CHUNK_SIZE = 32000;  
        
    private File file; 
    private Map info;  
    private int chunkSize;
    private int pos;
    
    public FileObject(File file) { 
        this( file, CHUNK_SIZE ); 
    }

    public FileObject(File file, int chunkSize) { 
        this.file = file;
        this.chunkSize = chunkSize; 
    }
    
    public File getFile() { return file; } 
    
    public int getChunkSize() { return chunkSize; } 
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize; 
    }
    
    public void setPos( int pos ) {
        this.pos = pos; 
    }
        
    public void read( ChunkHandler handler ) {
        if ( handler == null ) {
            throw new NullPointerException("Please specify a ChunkHandler before reading the file ");
        }
        
        AbstractChunkHandler proxy = null; 
        if ( handler instanceof AbstractChunkHandler ) {
            proxy = ( AbstractChunkHandler ) handler; 
        } else { 
            proxy = new ChunkHandlerProxy( handler ); 
        } 
        
        MetaInfo meta = new MetaInfo();
        meta.id = "FO" + new UID();  
        meta.file = this.file; 
        
        try {
            URLConnection urlconn = meta.file.toURL().openConnection();
            meta.fileType = urlconn.getContentType();
            meta.fileName = file.getName(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 

        if ( proxy.isAutoComputeTotals() ) { 
            meta.autoComputeTotals = true; 
            chunk( proxy, meta, true ); 
        }
        proxy.setMeta( meta ); 
        proxy.start(); 
        if ( !proxy.isCancelled() ) { 
            chunk( proxy, meta, false ); 
        } 
        proxy.end(); 
    } 
    
    private void chunk( AbstractChunkHandler handler, MetaInfo meta, boolean bypassHandler ) {
        int size = getChunkSize(); 
        if (size < MIN_CHUNK_SIZE) { 
            throw new IllegalStateException("The minimum chunk size is 32kb");
        } 

        ByteBuffer buf = ByteBuffer.allocate( size ); 
        RandomAccessFile raf = null; 
        FileChannel channel = null; 
        try {
            raf = new RandomAccessFile(file, "r"); 
            channel = raf.getChannel(); 
            
            if ( bypassHandler ) {
                meta.fileSize = channel.size(); 
                long num = (meta.fileSize / size);
                if ((meta.fileSize % size) > 0) { 
                    num += 1;
                } 
                meta.chunkCount = (int) num; 
                return; 
            }
            
            byte[] bytes = null; 
            int read = 0, indexno = 0; 
            int startPos = (this.pos > 0 ? this.pos : 1);
            while ((read=channel.read(buf)) > 0) { 
                buf.flip();
                bytes = new byte[read]; 
                System.arraycopy(buf.array(), 0, bytes, 0, read);
                buf.clear(); 
                
                indexno += 1;
                if ( !meta.autoComputeTotals ) { 
                    meta.chunkCount = indexno; 
                    meta.fileSize += read; 
                } 
                if ( indexno >= startPos ) {
                    handler.handle( indexno, bytes );                     
                }
                if ( handler.isCancelled() ) { break; }
            } 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { buf.clear(); } catch(Throwable t){;} 
            try { channel.close(); } catch(Throwable t){;} 
            try { raf.close(); }catch(Throwable t){;}  
        } 
    }     
    
    public class MetaInfo {
        
        private boolean autoComputeTotals;
        
        private File file;         
        private String id; 
        private String fileName; 
        private String fileType;
        private long fileSize; 
        private int chunkCount; 
                
        public String getId() { return id; } 
        public File getFile() { return file; } 
        public String getFileName() { return fileName; } 
        public String getFileType() { return fileType; } 
        public long getFileSize() { return fileSize; } 
        public int getChunkCount() { return chunkCount; }   
    }
    
    private class ChunkHandlerProxy extends AbstractChunkHandler {
        
        private ChunkHandler handler; 
        
        ChunkHandlerProxy( ChunkHandler handler ) { 
            this.handler = handler; 
        } 
        
        public void start() {
            handler.start(); 
        }

        public void end() {
            handler.end(); 
        }

        public void handle(int indexno, byte[] bytes) {
            handler.handle( indexno, bytes );
        } 
    } 
} 
