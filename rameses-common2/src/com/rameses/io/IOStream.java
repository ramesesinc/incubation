/*
 * IOStream.java
 *
 * Created on July 9, 2013, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author wflores
 */
public class IOStream 
{
    private final static int DEFAULT_BUFFER_SIZE = 1024*4;
    
    public IOStream() {
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

    public static byte[] toByteArray(InputStream input) { 
        return toByteArray(input, DEFAULT_BUFFER_SIZE); 
    } 
    
    public static byte[] toByteArray(InputStream input, int bufferSize) 
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output, bufferSize);
        return output.toByteArray();
    } 
} 
