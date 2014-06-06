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
import com.rameses.util.Base64CoderImpl;
import com.rameses.util.MessageObject;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.rmi.server.UID;
import java.util.HashMap;
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
    
    private String name;
    private AbstractContext context;
    private Map conf;
    private String connectionid;

    private WebSocketClientFactory factory;     
    private WebSocket.Connection connection; 
    private WebSocketClient wsclient; 
    private String protocol; 
    private String group; 
    private String host; 
    private long maxConnection;
    
    private String acctname;
    private String apikey;
    
    public WebsocketConnection(String name, AbstractContext context, Map conf) {
        this.connectionid = "WS"+ new UID();
        this.name = name;
        this.context = context;
        this.conf = conf;
        this.protocol = (String) conf.get("ws.protocol");

        maxConnection = DEFAULT_MAX_CONNECTION;
        if (conf.containsKey("ws.maxConnection")) {
            maxConnection = Long.parseLong(conf.get("ws.maxConnection")+"");
        }
        host = (String)conf.get("ws.host");
        if (!host.startsWith("ws")) {
            host = "ws://"+host;
        }
        group = (String)conf.get("ws.group");   
        if (group == null) group = protocol;

        acctname = (String)conf.get("acctname");
        apikey = (String)conf.get("apikey");
    }
    
    public Map getConf() {
        return conf;
    }
    
    public void start() {
        try {
            Map headers = new HashMap();
            headers.put("connectionid", connectionid);
            headers.put("acctname", acctname);  
            headers.put("apikey", apikey);
            headers.put("group", group);
            char[] chars = new Base64CoderImpl().encode(headers); 
            
            factory = new WebSocketClientFactory();
            factory.start();
            wsclient = factory.newWebSocketClient();
            wsclient.setProtocol(protocol + ";" + new String(chars));
            open();
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
    } 
    
    private void open() throws Exception {
        try {
            wsclient.open( new URI(host), this, maxConnection, TimeUnit.MILLISECONDS );
        } catch( InterruptedException e) {
            System.out.println("[WebsocketConnection, "+ protocol +"] " + "error " + e.getClass() + " message:" + e.getMessage());
        } catch(Exception ce) {
            System.out.println("[WebsocketConnection, "+ protocol +"] " + ce.getClass() + " " + ce.getMessage() );
            try {
                Thread.sleep( RECONNECT_DELAY );
                open();
            } catch(InterruptedException ie){;} 
        } 
    } 
    
    public void send(Object data) { 
        if (connection == null) {
            System.out.println("[WebsocketConnection] connection is not set");
            return;
        } 
        
        try {
            System.out.println("[WebsocketConnection] send " + data);
            MessageObject mo = new MessageObject();
            mo.setConnectionId(connectionid);
            mo.setGroupId(group);
            mo.setData(data); 
            byte[] bytes = mo.encrypt();            
            connection.sendMessage( bytes, 0, bytes.length );        
        } catch (Throwable ex) {
            ex.printStackTrace();
        }        
    }
    
    public void sendText(String data) {
        send( data );
    }
            
    public void onOpen(WebSocket.Connection connection) {
        this.connection = connection;
        connection.setMaxIdleTime( MAX_IDLE_TIME );
    }
    
    public void onClose(int i, String msg) {
        if(connection != null) {
            this.connection.close();
            this.connection = null;
            if (i == 1006) {
                try { factory.stop();  } catch(Exception ign){;}
                try { start(); } catch(Exception e) {e.printStackTrace();}
            }
            else if (i == 1002) {
                System.out.println("[WebsocketConnection, "+ protocol +"] " + msg);
            }
            //reconnect if max idle time reached
            else if (i == 1000) { 
                try { 
                    open(); 
                } catch(Throwable e) {
                    e.printStackTrace(); 
                }
            }
        }
    }
    
    public void onMessage(String stringData) {
        super.notifyHandlers( stringData );
    }
    
    public void onMessage(byte[] bytes, int offset, int length) {
        try {
            MessageObject mo = new MessageObject().decrypt(bytes, offset, length); 
            //if the sender and receiver uses the same connection, do not process
            if (connectionid.equals(mo.getConnectionId())) return; 

            String msggroup = mo.getGroupId();
            if ((msggroup == null && group == null) || (group != null && group.equals(msggroup))) { 
                super.notifyHandlers( mo.getData() );    
            } 
        } catch(Exception e) {
            System.out.println("[WebsocketConnection, "+ protocol +"] " + "onMessage failed caused by " + e.getMessage());
            e.printStackTrace();
        } 
    } 
} 
