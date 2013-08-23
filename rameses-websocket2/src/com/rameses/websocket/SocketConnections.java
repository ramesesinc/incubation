/*
 * SocketConnections.java
 *
 * Created on December 28, 2012, 10:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class SocketConnections 
{
    public final static int SHUTDOWN = 10;
    
    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap());
    
    public boolean addChannel(Channel channel) 
    {
        String name = channel.getName();
        if (channels.containsKey(name)) 
        {
            System.out.println("channel " + name + " already exists!");
            return false;
        } 
        else 
        {
            channels.put( name, channel );
            return true;
        }
    }
    
    public void removeChannel(String name) {
        channels.remove( name );
    }
    
    
    public synchronized Channel getChannel(String name) throws ChannelNotFoundException  {
        if( !channels.containsKey(name) ) {
            throw new ChannelNotFoundException(name);
        }
        return channels.get( name );
    }
    
    public void shutdown() {
        for(Channel s: channels.values() ) {
            s.close(SHUTDOWN,"server shutdown");
        }
        channels.clear();
    }
    
    public boolean isChannelExist(String name) {
        return channels.containsKey(name);
    }
}
