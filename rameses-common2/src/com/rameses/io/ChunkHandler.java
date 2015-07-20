/*
 * ChunkHandler.java
 *
 * Created on April 9, 2014, 5:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

/**
 *
 * @author wflores 
 */
public interface ChunkHandler {
    
    void start();
    void end();
    
    void handle(int indexno, byte[] bytes); 
    
}
