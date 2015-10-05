/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.util.concurrent.Callable;

/**
 *
 * @author rameses
 */
public final class AsyncUtil {
    
    public final static void process(final Callable handler) {
        new Thread(new Runnable(){

            public void run() {
                try { 
                    handler.call();
                } catch (Throwable ex) {
                    ex.printStackTrace(); 
                }
            }
            
        }).start(); 
    }
    
}
