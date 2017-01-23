/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.io.AbstractChunkHandler;
import com.rameses.io.ChunkHandler;
import com.rameses.io.FileChunker;
import com.rameses.io.FileObject;
import com.rameses.util.BreakException;
import java.io.File;
import java.io.FileWriter;
import java.lang.Object;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author rameses1
 */
public class FileUploadItem implements Runnable {

    private File file;
    private File statusFile;
    private File contentFile;
    
    private Properties conf;
    private Handler handler;
    private boolean cancelled;
    private boolean completed;
    
    public FileUploadItem( File file, Properties conf ) {
        this.file = file; 
        this.conf = (conf == null? new Properties(): conf); 
    }
    
    public File getFile() { return file; } 
    public Properties getConf() { return conf; } 

    private File getStatusFile() { return statusFile; }
    public void setStatusFile( File statusFile ) {
        this.statusFile = statusFile; 
    }
    
    private File getContentFile() { return contentFile; }
    public void setContentFile( File contentFile ) {
        this.contentFile = contentFile;
    }
    
    public void setHandler( Handler handler ) {
        this.handler = handler; 
    } 

    public void run() { 
        System.out.println("processing "+ getFile().getName()); 
        while ( true ) {
            try {
                if ( completed || cancelled || handler==null ) break; 

                final FileUploadItem root = FileUploadItem.this;
                
                Number varchunk = getIntegerProperty("chunksize"); 
                if ( varchunk == null ) throw new InterruptedException("["+root.getFile().getName()+"] No chunksize property defined");
                
                Number varcount = getIntegerProperty("count"); 
                if ( varcount == null ) throw new InterruptedException("["+root.getFile().getName()+"] No count property defined");
                
                Number varpos = getIntegerProperty("pos"); 
                FileChunker fo = new FileChunker( getContentFile() ); 
                fo.setPos(varpos == null? 0 : varpos.intValue()); 
                fo.setChunkSize( varchunk.intValue() ); 
                fo.parse(new ChunkHandler() { 
                    public void start() {
                    }
                    public void end() {
                    }
                    public void handle(int indexno, byte[] bytes) {
                        if ( root.cancelled ) throw new BreakException(); 
                        
                        root.getConf().put("pos", indexno); 
                        root.handler.onupload( root, indexno, bytes );
                        root.saveConf(); 
                    }
                });
                
                handler.onend( this ); 
                completed = true; 

            } catch( BreakException be ) {
                //do nothing 
            } catch(Throwable t) {
                t.printStackTrace(); 
            }
        }
    }
        
    public void cancel() {
        this.cancelled = true; 
    }
    
    public static interface Handler {
        void onupload( FileUploadItem item, int pos, byte[] bytes );
        void onend( FileUploadItem item );
    }
    
    private Number getIntegerProperty( String name ) {
        try {
            return new Integer( getConf().getProperty(name).trim());  
        } catch(Throwable t) { 
            return null;  
        }
    }
    
    private void saveConf() {
        FileWriter fw = null; 
        try {
            StringBuilder sb = new StringBuilder();
            Set<Entry<Object,Object>> sets = getConf().entrySet(); 
            for ( Entry<Object,Object> entry : sets ) {
                String skey = (entry.getKey()==null? null: entry.getKey().toString()); 
                if ( skey != null && skey.trim().length() > 0 ) {
                    String sval = (entry.getValue()==null? "": entry.getValue().toString()); 
                    sb.append(skey).append("=").append(sval).append("\n"); 
                } 
            } 
            
            fw = new FileWriter( getStatusFile() ); 
            fw.write( sb.toString() ); 
            fw.flush();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { fw.close(); }catch(Throwable t){;} 
        }
    }
}
