/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

import com.rameses.io.FileObject.MetaInfo;

/**
 *
 * @author wflores
 */
public abstract class AbstractChunkHandler implements ChunkHandler {
    
    private MetaInfo meta; 
    private boolean cancelled; 
    
    public boolean isCancelled() { return cancelled; }
    public void cancel() { cancelled = true; } 

    public boolean isAutoComputeTotals() { return true; } 
    
    public MetaInfo getMeta() { return meta; } 
    void setMeta( MetaInfo meta ) { 
        this.meta = meta; 
    }    

    public void start() {
    }

    public void end() {
    }
} 
