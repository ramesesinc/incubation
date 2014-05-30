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
import com.rameses.util.CipherUtil;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.rmi.server.UID;
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
            StringBuffer req = new StringBuffer();
            req.append("connectionid=").append(connectionid).append(";");
            req.append("group=").append(group).append(";");
            req.append("acctname=").append(acctname).append(";");
            req.append("apikey=").append(apikey).append(";");
            char[] chars = new Base64CoderImpl().encode(req.toString());
            
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
        if (connection == null) return; 
        
        ByteArrayOutputStream bos = null; 
        ObjectOutputStream oos = null; 
        try { 
            Object encdata = encryptMessage(data); 
            bos = new ByteArrayOutputStream(); 
            oos = new ObjectOutputStream(bos); 
            oos.writeObject( encdata ); 
            byte[] bytes = bos.toByteArray(); 
            connection.sendMessage( bytes, 0, bytes.length ); 
        } catch(Exception e) { 
            e.printStackTrace(); 
        } finally { 
            try { bos.close(); } catch(Throwable e){;} 
            try { oos.close(); } catch(Throwable e){;} 
        } 
    }
    
    public void sendText(String data) {
        send( data );
    }
    
    private Object encryptMessage( Object data ) {
        Object[] datas = new Object[]{ connectionid, group, data };
        return new CipherUtil().encodeObject(datas);        
    }
    
    private Object[] decryptMessage( Object data ) {
        if (data == null) return null;
        
        Object o = new CipherUtil().decodeObject((Serializable) data); 
        if (o instanceof Object[]) {
            Object[] datas = (Object[]) o;  
            if (datas.length == 3) return datas; 
        } 
        
        throw new IllegalStateException("failed to decode message caused by invalid headers"); 
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
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes, offset, length);
            ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            Object data = decryptMessage(o);
            if (data instanceof Object[]) {
                Object[] datas = (Object[]) data;

                //if the sender and receiver uses the same connection, do not process
                if (connectionid.equals(datas[0])) return;
                
                Object msggroup = datas[1];
                if ((msggroup == null && group == null) || (group != null && group.equals(msggroup))) { 
                    super.notifyHandlers( datas[2] );    
                }
            } else {
                throw new IllegalStateException("Invalid message header format");
            }
        } catch(Exception e) {
            System.out.println("[WebsocketConnection, "+ protocol +"] " + "onMessage failed caused by " + e.getMessage());
            e.printStackTrace();
        } finally { 
            try { bis.close(); } catch(Exception e){;}
            try { ois.close(); } catch(Exception e){;}
        } 
    } 
    
    
} 
