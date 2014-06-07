/*
 * FileObject.java
 *
 * Created on May 16, 2014, 4:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class FileObject 
{
    public final static int CHUNK_SIZE = 65000;
    public final static int MIN_CHUNK_SIZE = 32000;  
    
    private String id; 
    private File file; 
    private Map info;  
    private int chunkSize;
    private List<byte[]> chunks; 
    
    public FileObject(File file) { 
        this.id = "F" + new UID();  
        this.file = file;
        this.chunkSize = CHUNK_SIZE;
    }
    
    public int getChunkSize() { return chunkSize; } 
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize; 
    }
    
    public void read() {
        chunks = chunk(getChunkSize()); 
        info = new HashMap();
        init(info); 
        
        int filesize = 0;
        for (byte[] bytes: chunks) {
            filesize += bytes.length; 
        }
        info.put("filesize", filesize); 
        info.put("chunkcount", chunks.size());
    }
    
    public Map getInfo() { 
        if (info == null) {
            throw new IllegalStateException("invoke the read method first before getting the header info");
        }
        
        Map map = new HashMap();
        map.putAll(info); 
        return map; 
    }
    
    public List<byte[]> getChunks() { 
        if (chunks == null) {
            throw new IllegalStateException("invoke the read method first before getting the chunks");
        }
        
        return chunks; 
    }
    
    private void init(Map map) {
        try {
            URLConnection urlconn = file.toURL().openConnection();
            String filetype = urlconn.getContentType();
            
            map.put("objid", id); 
            map.put("filename", file.getName()); 
            map.put("filetype", filetype);
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    } 

    private List<byte[]> chunk(int size) {
        if (size < MIN_CHUNK_SIZE) 
            throw new IllegalStateException("The minimum chunk size is 32kb");
        
        BufferedInputStream bis = null;
        try {
            List<byte[]> list = new ArrayList(); 
            bis = new BufferedInputStream(new FileInputStream(file)); 
            int counter=1, read=-1; 
            byte[] chunks = new byte[size]; 
            while ((read=bis.read(chunks)) != -1) {
                if (read < chunks.length) {
                    byte[] dest = new byte[read];
                    System.arraycopy(chunks, 0, dest, 0, read); 
                    list.add(dest); 
                } else { 
                    list.add(chunks); 
                } 
                chunks = new byte[size]; 
                counter += 1;
            }
            return list;
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { bis.close(); } catch(Throwable ign){;}
        } 
    }     
} 
