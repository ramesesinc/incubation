/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.tools.json.JSONUtil;
import com.rameses.http.HttpClient;
import com.rameses.http.HttpClient;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.script.messaging.ScriptInvokerHandler;
import com.rameses.osiris3.script.messaging.ScriptResponseHandler;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.util.Base64Cipher;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONSerializer;

/**
 *
 * @author Toshiba
 */
public class RabbitMQGoConnectionPool extends MessageConnection
{
    private Map conf;
    private Map appConf;
    private AbstractContext context;
    private String name;
    private String queueName;
    private boolean started;

    private API api;
    private RabbitMQConnection rabbit;

    public RabbitMQGoConnectionPool(Map conf, AbstractContext context, String name){
        this.started = false;
        this.name = name;
        this.conf = conf;
        this.context = context;
        this.queueName = getProperty("queue");
        
        appConf = new HashMap();
        appConf.putAll(conf);
        
        api = new API(); 
        api.setUsername(getProperty("user"));
        api.setPassword(getProperty("pwd")); 
        api.setHost(getProperty("host")); 
        try {
            api.setPort(Integer.parseInt(getProperty("port"))); 
        } catch(Throwable t) {;}
        
        Map map = api.getExchange(getProperty("exchange")); 
        conf.put("exchange.auto_delete", getProperty("auto_delete", map)); 
        conf.put("exchange.durable", getProperty("durable", map)); 
        
        map = api.getQueue( this.queueName ); 
        conf.put("queue.auto_delete", getProperty("auto_delete", map)); 
        conf.put("queue.durable", getProperty("durable", map)); 
    }
            
    @Override
    public void start() {
        if ( started ) return;
        started = true;
        
        Object apphost = appConf.get("app.host");
        
        System.out.println("Initializing RabbitMQ Connection Factory (v2.0)...");
        ConnectionFactory factory = createConnectionFactory(); 
        
        rabbit = new RabbitMQConnection(name, context, conf); 
        if ( apphost == null ) {
            rabbit.addHandler( new MessageHandlerProxy());
        } else { 
            GoResponseHandler rrh = new GoResponseHandler( );
            ScriptInvokerHandler handler = new ScriptInvokerHandler(appConf, rrh);
            rabbit.addHandler(handler);
        } 

        rabbit.setAPI(api);
        rabbit.setFactory(factory); 
        rabbit.start();
    }

    private ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory(); 
        factory.setHost( getProperty("host") ); 
        factory.setUsername( getProperty("user") ); 
        factory.setPassword( getProperty("pwd") ); 
        factory.setAutomaticRecoveryEnabled( true ); 

        int heartbeat = 30; 
        try { 
            heartbeat = Integer.parseInt(getProperty("heartbeat")); 
        } catch(Throwable t) {;} 

        int networkRecoveryInterval = 10000; 
        try { 
            networkRecoveryInterval = Integer.parseInt(getProperty("networkRecoveryInterval")); 
        } catch(Throwable t) {;} 

        factory.setRequestedHeartbeat( heartbeat );
        factory.setNetworkRecoveryInterval( networkRecoveryInterval ); 
        return factory; 
    }

    @Override
    public void stop() {
        try {
            rabbit.stop();
        } catch(Throwable e){;}
    }

    @Override
    public Map getConf() {
        return conf;
    }

    @Override
    public void send(Object data) {
        send(data, queueName);
    }

    @Override
    public void sendText(String data) {
        send(data, queueName);
    }

    @Override
    public void send(Object data, String queueName) {
        rabbit.send(data, queueName);
    }

    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {
        rabbit.addResponseHandler(tokenid, handler);
    }
    
    public void addQueue(String queueName, String exchange) throws Exception {
        if(exchange==null) {
            exchange = (String)conf.get("exchange");
        }
        rabbit.addQueue(queueName, exchange);
    }    
    
    public void removeQueue(String queueName, String exchange)  {
        if(exchange==null) {
            exchange = (String)conf.get("exchange");
        }
        rabbit.removeQueue(queueName, exchange);
    }    
    
    private class MessageHandlerProxy implements MessageHandler {

        public boolean accept(Object data) { 
            return true; 
        } 

        public void onMessage(Object data) { 
            RabbitMQGoConnectionPool.this.notifyHandlers( data ); 
        } 
    } 
    
    private String getProperty( String name ) {
        return getProperty(name, conf); 
    } 
    private String getProperty( String name, Map map ) {
        Object o = (map == null? null: map.get(name)); 
        return ( o == null ? null: o.toString()); 
    } 
    
    private class GoResponseHandler implements ScriptResponseHandler {
        GoResponseHandler(  ) { 
        }
        
        public void send(Map map) {
            try {
                Base64Cipher base64 = new Base64Cipher();
                Object result = map.get("result"); 
                result = base64.decode(result.toString()); 
                result = JSONSerializer.toJSON(result); 
                
                HttpClient c = new HttpClient("192.168.254.105:8082");
                System.out.println("posting to "+ map.get("tokenid"));
                c.post("gdx-notifier/publish/"+map.get("tokenid"), result.toString()); 
                System.out.println("after post -> " + result);
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
