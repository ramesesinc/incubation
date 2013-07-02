/*
 * MediaFile.java
 *
 * Created on July 2, 2013, 11:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author Elmo
 */
public class MediaFile {
    
    private String contentType;
    private long lastModified;
    private long contentLength;
    private byte[] content;
    private StreamHandler handler;
    
    
    /** Creates a new instance of MediaFile */
    public MediaFile() {
       
    }

    public void setFile(File f) {
        if(f!=null) {
            handler = new FileStreamHandler(f);
        }
        else {
            handler = null;    
        }
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    public InputStream getInputStream() {
        if( handler !=null) {
            return handler.getInputStream();
        }
        
        return new ByteArrayInputStream(content);
    }
    
    

    public StreamHandler getHandler() {
        return handler;
    }

    public void setHandler(StreamHandler handler) {
        this.handler = handler;
    }
    
    public static interface StreamHandler {
        InputStream getInputStream();
    }
    
    public static class FileStreamHandler implements StreamHandler {
        private File file;
        public FileStreamHandler(File f) {
            file = f;
        }
        public InputStream getInputStream() {
            try {
                return new FileInputStream(file);
            }
            catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
    }
    
}
