/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
    
    public FileChunker( File file ) { 
        parser = new FileParser( file ); 
    }

    public FileChunker( byte[] bytes, String name, String type ) { 
        parser = new ByteParser( bytes, null, null ); 
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
    
    public void parse( ChunkHandler handler ) {
        parser.parse( handler ); 
    } 
    
    private class ChunkHandlerImpl extends AbstractChunkHandler {
        
        List<byte[]> results = new ArrayList(); 
                        
        public void start() {
            results.clear(); 
        }

        public void end() {
        }

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
            FileInputStream fis = null;
            FileChannel fc = null; 
            byte[] bytes = null;
            try {
                fis = new FileInputStream( file ); 
                fc = fis.getChannel();
                return fc.size(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) { 
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { fc.close(); } catch(Throwable t) {;} 
                try { fis.close(); } catch(Throwable t) {;} 
            }
        } 
        
        public void parse( ChunkHandler handler ) { 
            FileInputStream fis = null;
            FileChannel fc = null; 
            try {
                fis = new FileInputStream( file ); 
                fc = fis.getChannel(); 

                handler.start(); 

                int counter=1, bytesRead=-1;    
                ByteBuffer buffer = ByteBuffer.allocate( getChunkSize() ); 
                while ((bytesRead=fc.read(buffer)) != -1) {
                    buffer.flip();
                    if (buffer.hasRemaining()) {
                        byte[] chunks = buffer.array(); 
                        if (bytesRead < chunks.length) {
                            byte[] dest = new byte[bytesRead];
                            System.arraycopy(chunks, 0, dest, 0, bytesRead); 
                            handler.handle( counter, dest ); 
                        } else {
                            handler.handle( counter, chunks ); 
                        }
                        counter += 1;
                    }
                    buffer.clear(); 
                }
                handler.end(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { fc.close(); } catch(Throwable ign){;}
                try { fis.close(); } catch(Throwable ign){;}
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
            ByteArrayInputStream bais = null; 
            BufferedInputStream bis = null;
            try {
                bais = new ByteArrayInputStream( bytes ); 
                bis = new BufferedInputStream( bais ); 
                
                handler.start(); 

                int counter=1, read=-1;             
                byte[] chunks = new byte[ getChunkSize() ]; 
                while ((read=bis.read(chunks)) != -1) {
                    if (read < chunks.length) {
                        byte[] dest = new byte[read];
                        System.arraycopy(chunks, 0, dest, 0, read); 
                        handler.handle( counter, dest ); 
                    } else { 
                        handler.handle( counter, chunks ); 
                    } 
                    chunks = new byte[ getChunkSize() ]; 
                    counter += 1;
                }
                handler.end(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { bis.close(); } catch(Throwable ign){;}
                try { bais.close(); } catch(Throwable ign){;}
            } 
        }
    }
    
}
