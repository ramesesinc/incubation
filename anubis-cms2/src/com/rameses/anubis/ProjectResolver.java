/*
 * ProjectResolver.java
 *
 * Created on July 16, 2012, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

/**
 *
 * @author Elmo
 */
public class ProjectResolver {
    
    private final static Object LOCKED = new Object();
    
    private Project project; 
        
    public Project removeProject() {
        synchronized( LOCKED ) {
            Project old = this.project; 
            this.project = null; 
            return old; 
        }
    }  
    
    public Project getProject() {
        synchronized( LOCKED ) {
            if ( project == null ) {
                String webrootpath = System.getProperty("osiris.base.dir") +"/webroot"; 
                try {
                    java.io.File f = new java.io.File( webrootpath ); 
                    project =  new Project("webroot", f.toURI().toURL().toString()); 
                } catch(RuntimeException re) {
                    throw re; 
                } catch(Throwable t) {
                    throw new RuntimeException(t); 
                }
            }
            return project; 
        }
    }
}
