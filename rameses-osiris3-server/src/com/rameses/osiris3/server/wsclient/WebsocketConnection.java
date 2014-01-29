/*
 * WebsocketConnection.java
 *
 * Created on February 9, 2013, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.wsclient;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 *
 * @author Elmo
 */
public class WebsocketConnection extends MessageConnection implements WebSocket.OnTextMessage, WebSocket.OnBinaryMessage 
{
    private final static int DEFAULT_MAX_CONNECTION = 35000;    
    private final static int MAX_IDLE_TIME          = 60000;
    private final static int RECONNECT_DELAY        = 2000;
    
    private Map conf;
    private WebSocket.Connection connection;
    private WebSocketClient wsclient;
    private String protocol;
    private String host;
    private long maxConnection;
    private WebSocketClientFactory factory;
    
    /** Creates a new instance of WebsocketConnection */
    public WebsocketConnection(String name, AbstractContext c, Map conf) {
        this.conf = conf;
        this.protocol = (String)conf.get("ws.protocol");
        //wsclient.setOrigin(id);
        maxConnection = DEFAULT_MAX_CONNECTION;
        if( conf.containsKey("ws.maxConnection")) {
            maxConnection = Long.parseLong(conf.get("ws.maxConnection")+"");
        }
        host = (String)conf.get("ws.host");
        if(!host.startsWith("ws")) {
            host = "ws://"+host;
        }
    }
    
    public void start() 
    {
        try 
        {
            factory = new WebSocketClientFactory();
            factory.start();
            wsclient = factory.newWebSocketClient();
            wsclient.setProtocol(protocol);
            open();
        } 
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private void open() throws Exception {
        try {
            wsclient.open( new URI(host), this, maxConnection, TimeUnit.MILLISECONDS );
        } catch( InterruptedException e) {
            System.out.println("error " + e.getClass() + " message:" + e.getMessage());
        } catch(Exception ce) {
            System.out.println( ce.getClass() + " " + ce.getMessage() );
            try {
                Thread.sleep( RECONNECT_DELAY );
                open();
            } catch(InterruptedException ie){;}
        }
    }
    
    
    
    
    public void send(Object data) {
        if(connection==null) return;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject( data );
            byte[] bytes = bos.toByteArray();
            connection.sendMessage( bytes, 0, bytes.length );
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {bos.close();} catch(Exception e){;}
            try {oos.close();} catch(Exception e){;}
        }
    }
    
    public void sendText(String data) {
        if(connection!=null) {
            try {
                connection.sendMessage( data );
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void onOpen(WebSocket.Connection connection) {
        this.connection = connection;
        connection.setMaxIdleTime( MAX_IDLE_TIME );
    }
    
    public void onClose(int i, String string) {
        if(connection!=null) {
            this.connection.close();
            this.connection = null;
            if(i==1006) {
                try { factory.stop();  } catch(Exception ign){;}
                try { start(); } catch(Exception e) {e.printStackTrace();}
            }
            //reconnect if max idle time reached
            else if(i==1000) {
                try { open(); } catch(Exception e){e.printStackTrace();}
            }
        }
    }
    
    public void onMessage(String stringData) {
        super.notifyHandlers( stringData );
    }
    
    public void onMessage(byte[] bytes, int offset, int length) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes,offset,length);
            ois = new ObjectInputStream(bis);
            Object data = ois.readObject();
            super.notifyHandlers( data );
        } catch(Exception e) {
            System.out.println("error onMessage in WebsocketChannel");
            e.printStackTrace();
        } finally {
            try { bis.close(); } catch(Exception e){;}
            try { ois.close(); } catch(Exception e){;}
        }
    }

    public Map getConf() {
        return conf;
    }
    
    
    
    
}
