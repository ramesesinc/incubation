/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author dell
 */
public class ChannelSet {

    private Connection connection;
    private Map<String, Channel> channels = new Hashtable<String, Channel>();

    public ChannelSet(Connection conn) {
        this.connection = conn;
    }

    public Channel getChannel(String name) throws Exception {
        Channel channel = channels.get(name);
        if (channel == null) {
            synchronized (channels) {
                try {
                    channel = connection.createChannel();
                } catch (Exception ex) {
                    throw new Exception("Channel not found!");
                }
            }
        }
        return channel;
    }
    
    public void close() {
        for(Channel c: channels.values()) {
            try {
            c.close();
            }
            catch(Exception ex){;}
        }
        channels.clear();
    }
}
