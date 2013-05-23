/*
 * ServerLoaderProvider.java
 *
 * Created on March 27, 2013, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.server;

/**
 *
 * @author Elmo
 * This is used for loading servers. used by anubis, osiris3-server, websockets, etc.
 */
public interface ServerLoaderProvider {
    
    public abstract String getName();
    public abstract ServerLoader createServer(String name);
    
}
