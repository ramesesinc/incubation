/*
 * StreamUtil.java
 *
 * Created on May 21, 2009, 9:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @author elmo
 */
public final class StreamUtil 
{
    private static IOStream io = new IOStream();
    
    public static String toString( String filePath ) {
        ClassLoader loader = StreamUtil.class.getClassLoader();
        return toString(loader.getResourceAsStream( filePath ));
    }
    
    public static String toString( InputStream is ) {
        try {
            StringBuilder out = new StringBuilder();
            int i = -1;
            while( (i=is.read())!=-1) {
                out.append((char)i);
            }
            return  out.toString();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try { is.close(); } catch(Exception ign){;}
        }
    }
    
    public static void write( InputStream is, StringBuilder out ) {
        try {
            int i = -1;
            while( (i=is.read())!=-1) {
                out.append((char)i);
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try { is.close(); } catch(Exception ign){;}
        }
    }
    
    public static void write(byte[] bytes, OutputStream output) {
        write(bytes, output, 1024); 
    } 
    
    public static void write(byte[] bytes, OutputStream output, int bufferSize) {
        write(new ByteArrayInputStream(bytes), output, bufferSize); 
    } 
    
    public static void write(InputStream input, OutputStream output) {
        write(input, output, 1024);
    }
    
    public static void write(InputStream input, OutputStream output, int bufferSize) {
        io.write(input, output, bufferSize); 
    }

    public static byte[] toByteArray(InputStream input) { 
        return toByteArray(input, 1024*4); 
    } 
    
    public static byte[] toByteArray(InputStream input, int bufferSize) 
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output, bufferSize);
        return output.toByteArray();
    }
    
    
    /**
     * The following is for reading URL input streams via the URL.
     */
    
    public static interface InputStreamHandler {
        void handle( InputStream is ) throws Exception;
    }
    
    public static void readURLStream( String urlPath, InputStreamHandler handler)  {
         readURLStream( urlPath, handler, true );
    }
     
    public static void readURLStream( String urlPath, InputStreamHandler handler, boolean ignoreFileNotFound ) {
        InputStream is = null;
        try {
            URL u = new URL( urlPath );
            is = u.openStream();
            handler.handle( is );
        } 
        catch(Exception ex) 
        {
            if( (ex instanceof FileNotFoundException) && ignoreFileNotFound ) {
                //do nothing
            }
            else {
                throw new RuntimeException(ex.getMessage(), ex);    
            }    
        } 
        finally {
            try {is.close();}catch(Exception ign){;}
        }
    }    
}
