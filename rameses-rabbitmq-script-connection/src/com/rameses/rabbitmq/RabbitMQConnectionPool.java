/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Toshiba
 */
public class RabbitMQConnectionPool extends MessageConnection
{
    private Map conf;
    private Map appConf;
    private AbstractContext context;
    private String name;
    private String queueName;
    private boolean started;
    
    private List<RabbitMQConnection> pool = new ArrayList<RabbitMQConnection>();

    public RabbitMQConnectionPool(Map conf, AbstractContext context, String name){
        this.started = false;
        this.conf = conf;
        this.context = context;
        this.name = name;
        this.queueName = (String)conf.get("queue");
        
        appConf = new HashMap();
        appConf.putAll(conf);
    }
            
    @Override
    public void start() {
        if (started) return;
        started = true;
        
        int poolSize = 1;
        try{
            poolSize = Integer.parseInt(conf.get("poolSize").toString());
        }
        catch(Exception e){
            //ignore 
        }
        for(int i = 0; i<poolSize; i++){
            String sname = (i == 0 ? name : name+i);
            RabbitMQConnection rabbit = new RabbitMQConnection(sname, context, conf ); 
            
            if (appConf.get("app.host") != null){
                ScriptInvokerHandler handler = new ScriptInvokerHandler(appConf, rabbit);
                rabbit.addHandler(handler);
            }
            rabbit.start();
            pool.add(rabbit);
        }
    }

    @Override
    public void stop() {
        for(RabbitMQConnection c : pool){
            try{
                c.stop();
            }
            catch(Exception e){
                //
            }
        }
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
        if (pool.size() > 0){
            RabbitMQConnection conn = pool.get(0);
            conn.send(data, queueName);
        }
    }

    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {
        for(RabbitMQConnection mc : pool){
            mc.addResponseHandler(tokenid, handler);
        }
    }
    
    public void addQueue(String queueName, String exchange) throws Exception {
        if(exchange==null) {
            exchange = (String)conf.get("exchange");
        }
        for(RabbitMQConnection mc : pool){
            mc.addQueue(queueName, exchange);
        }
    }    
    
    public void removeQueue(String queueName, String exchange)  {
        if(exchange==null) {
            exchange = (String)conf.get("exchange");
        }
        for(RabbitMQConnection mc : pool){
            mc.removeQueue(queueName, exchange);
        }
    }    
    
}
