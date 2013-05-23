/*
 * ServerConf.java
 *
 * Created on January 15, 2013, 8:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.common;

/**
 *
 * @author Elmo
 */
public class ServerConf {
    
    /************************************************************************
     * CONSTANTS
     *************************************************************************/
    private int taskPoolSize = 100;
    private long blockingTimeout = 20000;
    
    /**************************************************************************/
    private static ServerConf instance;
    
    public static ServerConf getInstance() {
        if(instance ==null) {
            instance = new ServerConf();
        }
        return instance;
    }
    
    
    
    public ServerConf() {
    }

    public int getTaskPoolSize() {
        return taskPoolSize;
    }

    public long getBlockingTimeout() {
        return blockingTimeout;
    }
    
}
