/*
 * AbstractServer.java
 *
 * Created on January 19, 2013, 12:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.common;

/**
 *
 * @author Elmo
 */
public abstract class AbstractServer implements Runnable {
    
    protected int port;
    
    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    
    public int getPort() {
        return port;
    }
    

    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new ServerShutdown());
            start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    class ServerShutdown extends Thread {
        public void run() {
            try {
                AbstractServer.this.stop();
            } catch(Exception e){
                System.out.println("shutdown error. " +e.getMessage());
            }
        }
        
    }
}
