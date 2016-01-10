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
import java.net.URLConnection;
import java.rmi.server.UID;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class FileObject 
{
    public final static int CHUNK_SIZE = 65000;
    public final static int MIN_CHUNK_SIZE = 32000;  
    
    private File file; 
    private Map info;  
    private int chunkSize;
    
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

        BufferedInputStream bis = null; 
        Base64Cipher cipher = new Base64Cipher();        
        try {
            bis = new BufferedInputStream(new FileInputStream(file)); 

            int read = -1, indexno = 0;             
            byte[] chunks = new byte[size]; 
            while ((read=bis.read(chunks)) != -1) {
                if (read < chunks.length) {
                    byte[] dest = new byte[read];
                    System.arraycopy(chunks, 0, dest, 0, read); 
                    chunks = dest; 
                } 
                
                indexno += 1; 
                if ( bypassHandler || !meta.autoComputeTotals ) {
                    meta.fileSize += chunks.length; 
                    meta.chunkCount = indexno; 
                } else { 
                    handler.handle( indexno, chunks ); 
                    if ( handler.isCancelled() ) { break; }
                } 
                chunks = new byte[size];  
            } 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { bis.close(); } catch(Throwable ign){;}
        } 
    }     
    
    public class MetaInfo {
        
        private boolean autoComputeTotals;
        
        private File file;         
        private String id; 
        private String fileName; 
        private String fileType;
        private int fileSize; 
        private int chunkCount; 
                
        public String getId() { return id; } 
        public File getFile() { return file; } 
        public String getFileName() { return fileName; } 
        public String getFileType() { return fileType; } 
        public int getFileSize() { return fileSize; } 
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
