/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

import com.rameses.util.BreakException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores 
 */
public class FileChunker {
    
    private int chunkSize = 32000; 
    private Parser parser = null; 
    
    private int pos;
    
    public FileChunker( File file ) { 
        parser = new FileParser( file ); 
    }

    public FileChunker( byte[] bytes, String name, String type ) { 
        parser = new ByteParser( bytes, null, null ); 
    } 
    
    public void setPos( int pos ) {
        this.pos = pos; 
    }
    
    public int getChunkSize() { return chunkSize; } 
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize; 
    }
    
    public String getName() { 
        return parser.getName(); 
    }
    public String getType() {
        return parser.getType(); 
    }
    public long getLength() {
        return parser.getLength(); 
    }
    public int getCount() {
        long len = getLength(); 
        long count = len / getChunkSize(); 
        if ((len % getChunkSize()) > 0) {
            count += 1; 
        }
        return (int) count; 
    }
    
    public List<byte[]> getChunks() {
        ChunkHandlerImpl handler = new ChunkHandlerImpl();
        parse( handler ); 
        return handler.results; 
    }
    
    public void parse() {
        parse( new SelfChunkHandler());
    }
    public void parse( ChunkHandler handler ) { 
        parser.parse( handler ); 
    } 
    
    
    protected void onstart() {}
    protected void onend() {}
    protected void onhandle( int indexno, byte[] bytes) {}
    protected void onerror( Throwable error ) {
        error.printStackTrace(); 
    }
    
    private class SelfChunkHandler extends AbstractChunkHandler {
        public void start() {
        }
        public void end() {
        }
        public void handle(int indexno, byte[] bytes) {
        }
    }
    private class ChunkHandlerImpl extends AbstractChunkHandler {
        
        List<byte[]> results = new ArrayList(); 
                        
        public void start() {
            results.clear(); 
        }

        public void end() {}

        public void handle(int indexno, byte[] bytes) {
            results.add( bytes ); 
        }
    }
    
    
    private interface Parser {
        String getName();
        String getType();
        long getLength();
        void parse( ChunkHandler handler ); 
    }
    
    private class FileParser implements Parser {
        
        File file; 
        
        FileParser( File file ) {
            this.file = file; 
        }
 
        public String getName() {
            return file.getName(); 
        }

        public String getType() {
            try {
                URLConnection urlconn = file.toURL().openConnection();
                return urlconn.getContentType(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) { 
                throw new RuntimeException(e.getMessage(), e); 
            } 
        }

        public long getLength() {
            RandomAccessFile raf = null; 
            FileChannel fc = null; 
            byte[] bytes = null;
            try {
                raf = new RandomAccessFile(file, "r"); 
                fc = raf.getChannel(); 
                return fc.size(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) { 
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { fc.close(); } catch(Throwable t) {;} 
                try { raf.close(); } catch(Throwable t) {;} 
            }
        } 
        
        public void parse( ChunkHandler handler ) { 
            FileChunker root = FileChunker.this;
            int chunkPos = root.pos; 
            if ( chunkPos <= 0 ) chunkPos = 1; 

            boolean started = false;
            Throwable error = null; 
            RandomAccessFile raf = null; 
            FileChannel fc = null; 
            try {
                raf = new RandomAccessFile(file, "r"); 
                raf.seek((chunkPos-1) * getChunkSize()); 
                fc = raf.getChannel(); 

                root.onstart(); 
                handler.start(); 
                started = true; 

                int counter=chunkPos, bytesRead=-1;
                ByteBuffer buffer = ByteBuffer.allocate( getChunkSize() ); 
                while ((bytesRead=fc.read(buffer)) != -1) {
                    buffer.flip();
                    if (buffer.hasRemaining()) {
                        byte[] chunks = buffer.array(); 
                        if (bytesRead < chunks.length) {
                            byte[] dest = new byte[bytesRead];
                            System.arraycopy(chunks, 0, dest, 0, bytesRead); 
                            handler.handle( counter, dest ); 
                            root.onhandle( counter, dest);
                        } else {
                            handler.handle( counter, chunks ); 
                            root.onhandle( counter, chunks);
                        } 
                        counter += 1;
                    }
                    buffer.clear(); 
                } 
            } catch(Throwable t) {
                error = t; 
            } finally { 
                try { fc.close(); } catch(Throwable ign){;}
                try { raf.close(); } catch(Throwable ign){;}
            } 
            
            if ( error != null ) { 
                root.onerror( error ); 
                
            } else if ( started ) { 
                handler.end(); 
                root.onend(); 
            } 
        } 
    }
    
    private class ByteParser implements Parser {
        byte[] bytes; 
        String name; 
        String type; 
        
        ByteParser( byte[] bytes, String name, String type ) {
            this.bytes = bytes; 
            this.name = name; 
            this.type = type; 
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public long getLength() { return bytes.length; }
        
        public void parse( ChunkHandler handler ) { 
            boolean started = false;
            Throwable error = null;             
            FileChunker root = FileChunker.this;
            ByteArrayInputStream bais = null; 
            BufferedInputStream bis = null;
            try {
                bais = new ByteArrayInputStream( bytes ); 
                bis = new BufferedInputStream( bais ); 
                
                root.onstart();                 
                handler.start(); 
                started = true; 

                int counter=1, read=-1;             
                byte[] chunks = new byte[ getChunkSize() ]; 
                while ((read=bis.read(chunks)) != -1) {
                    if (read < chunks.length) {
                        byte[] dest = new byte[read];
                        System.arraycopy(chunks, 0, dest, 0, read); 
                        handler.handle( counter, dest ); 
                        root.onhandle( counter, dest);
                    } else { 
                        handler.handle( counter, chunks ); 
                        root.onhandle( counter, chunks);
                    } 
                    chunks = new byte[ getChunkSize() ]; 
                    counter += 1;
                }                
            } catch(Throwable t) {
                error = t; 
            } finally {
                try { bis.close(); } catch(Throwable ign){;}
                try { bais.close(); } catch(Throwable ign){;}
            } 
            
            if ( error != null ) { 
                root.onerror( error ); 
                
            } else if ( started ) { 
                handler.end(); 
                root.onend(); 
            } 
        }
    }
    
}
