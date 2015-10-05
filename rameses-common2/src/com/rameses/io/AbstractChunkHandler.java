/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

/**
 *
 * @author wflores
 */
public abstract class AbstractChunkHandler implements ChunkHandler {
    
    private long size; 
    private int count; 
    
    public long getSize() { return size; } 
    public int getCount() { return count; } 
    
    public void start() {
        size = 0; 
        count = 0; 
    } 

    public void end() {
        //do nothing 
    }
    
    public abstract void process( int indexno, byte[] bytes ); 
    
    public final void handle(int indexno, byte[] bytes) {
        size += bytes.length; 
        count = indexno; 
        process( indexno, bytes ); 
    }
}
