/*
 * Shutdown.java
 *
 * Created on January 8, 2014, 7:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.server;

import java.io.File;

/**
 *
 * @author wflores
 */
public final class Shutdown 
{
    public static void main(String[] args) {
        String userdir = System.getProperty("user.dir"); 
        String rundir  = System.getProperty("osiris.run.dir", userdir); 
        File file = new File(rundir + "/.shutdown_pid"); 
        if (file.exists()) return;
        
        try { 
            file.createNewFile(); 
            System.out.println("Shutdown message has been posted to the server.");
            System.out.println("Server shutdown may take a while - check logfiles for completion");
        } catch(Throwable t) { 
            System.out.println("Failed to create a shutdown pid file"); 
            t.printStackTrace(); 
        } 
    } 
    
    private Shutdown() {
    }
}
