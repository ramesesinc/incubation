/*
 * IOStream.java
 *
 * Created on July 9, 2013, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class IOStream 
{
    private final static int DEFAULT_BUFFER_SIZE = 1024*4;
    
    public IOStream() {
    }
    
    public void write(byte[] bytes, File file) { 
        try { 
            write(bytes, new FileOutputStream(file)); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    public void write(byte[] bytes, OutputStream output) {
        write(bytes, output, DEFAULT_BUFFER_SIZE); 
    } 
    
    public void write(byte[] bytes, OutputStream output, int bufferSize) {
        write(new ByteArrayInputStream(bytes), output, bufferSize); 
    } 
    
    public static void write(InputStream input, OutputStream output) {
        write(input, output, DEFAULT_BUFFER_SIZE);
    }
    
    public static void write(InputStream input, OutputStream output, int bufferSize) 
    {
        try 
        {
            byte[] buffer = new byte[bufferSize]; 
            int EOF = -1;
            int len = 0;
            while ((len = input.read(buffer)) != EOF) {
                output.write(buffer, 0, len);
            }  
            output.flush();
        }
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        finally 
        {
            try { output.close(); }  catch(Exception ign){;}
            try { input.close(); } catch(Exception ign){;}            
        }
    }
    
    public static byte[] toByteArray(File file) { 
        try { 
            return toByteArray(new FileInputStream(file), DEFAULT_BUFFER_SIZE); 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    } 
    
    public static byte[] toByteArray(InputStream input) { 
        return toByteArray(input, DEFAULT_BUFFER_SIZE); 
    } 
    
    public static byte[] toByteArray(InputStream input, int bufferSize) 
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output, bufferSize);
        return output.toByteArray();
    } 
    
    public static byte[] toByteArray(Object value) { 
        if (value == null) return null;
        
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream(); 
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);             
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { oos.close(); }catch(Throwable t){;} 
            try { baos.close(); }catch(Throwable t){;} 
        }
    }
    
    public static List<byte[]> chunk(File file, int size) {
        try {
          return chunk(new FileInputStream(file), size);   
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    public static List<byte[]> chunk(InputStream inp, int size) {
        BufferedInputStream bis = null;
        try {
            List<byte[]> list = new ArrayList(); 
            bis = new BufferedInputStream(inp); 
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
    
    public static void chunk(File file, int size, ChunkHandler handler) {
        try {
            chunk(new FileInputStream(file), size, handler); 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    public static void chunk(InputStream inp, int size, ChunkHandler handler) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(inp); 
            int counter=1, read=-1; 
            byte[] chunks = new byte[size]; 
            while ((read=bis.read(chunks)) != -1) {
                if (read < chunks.length) {
                    byte[] dest = new byte[read];
                    System.arraycopy(chunks, 0, dest, 0, read); 
                    handler.handle(counter, dest); 
                } else {
                    handler.handle(counter, chunks); 
                }
                chunks = new byte[size]; 
                counter += 1;
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { bis.close(); } catch(Throwable ign){;}
        }        
    }    
} 
