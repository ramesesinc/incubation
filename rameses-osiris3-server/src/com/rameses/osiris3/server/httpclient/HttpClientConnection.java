/*
 * HttpClientConnection.java
 *
 * Created on June 17, 2013, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.httpclient;

import com.rameses.http.HttpClient;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.XConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wflores
 */
public class HttpClientConnection extends XConnection 
{
    private String name;
    private AbstractContext context; 
    private Map conf;
    private LinkedBlockingQueue queue = new LinkedBlockingQueue(); 
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public HttpClientConnection(String name, AbstractContext context, Map conf) 
    {
        this.name = name;
        this.context = context;
        this.conf = (conf == null? new HashMap(): conf);
    }

    public Map getConf() { return conf; }
    
    public void start() 
    {
        
        executor.submit(new Runnable() 
        {
            public void run() 
            {
                try {
                    execute();
                } catch(Exception ex) { 
                    System.out.println("HttpClientConnection.execute [ERROR] " + ex.getMessage());
                }
            }
        });
    }

    public void stop() { 
        executor.shutdown(); 
    } 

    public void send(Object message) {
        if (message == null) return; 
        
        queue.add(message); 
    }
    
    private void execute() throws Exception 
    {
        while (true)
        {
            Object result = queue.poll(1, TimeUnit.SECONDS); 
            if (result == null) continue;

            List list = new ArrayList();
            list.add(result);

            int tries = 1;
            int batchSize = 10;
            try {  
                batchSize = Integer.parseInt(conf.get("batchSize").toString()); 
            } catch(Exception ign){;} 

            while ((result = queue.poll()) != null) 
            {
                list.add(result);
                tries++;                    
                
                if (tries >= batchSize) break;
            }

            String host = (String) conf.get("http.host");
            String action = (String) conf.get("http.action");
            try {
                createHttpClient(host).post(action, list);
            } catch(Exception ex) { 
                System.out.println("HttpClientConnection.execute: error in posting data to " + host + " caused by " + ex.getMessage());
            } 
        } 
    }
    
    private HttpClient createHttpClient(String host) 
    {
        HttpClient httpc = new HttpClient(host, true);
        try {
            int value = Integer.parseInt(conf.get("http.connectionTimeout").toString()); 
            httpc.setConnectionTimeout(value);
        } catch(Exception ign){;} 
        
        try {
            int value = Integer.parseInt(conf.get("http.readTimeout").toString()); 
            httpc.setReadTimeout(value);
        } catch(Exception ign){;}         
        
        try {
            httpc.setProtocol(conf.get("http.protocol").toString());
        } catch(Exception ign){;} 
        
        try {
            boolean b = "true".equals(conf.get("http.encrypted")+"");  
            httpc.setEncrypted(true);
        } catch(Exception ign){
            httpc.setEncrypted(false); 
        }   
        
        return httpc;
    }
}
