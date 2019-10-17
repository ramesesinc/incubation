package com.rameses.gdx.coordinator;

import com.rameses.annotations.Context;
import com.rameses.custom.impl.JsonUtil;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import io.socket.client.Ack;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URI;
import java.util.Map;
import org.json.JSONObject;

public class GdxCoordinator extends MessageConnection {
    private final String EVENT_INVOKE = "invoke";
    private final String EVENT_REGISTER_SERVICE = "register_service";
    private String name;
    private AbstractContext context;     
    private Map conf; 
    private boolean enabled = false;
    private boolean started = false;
    private Manager manager;
    private Socket socket;
    private Config config;
    
    public GdxCoordinator(String name, AbstractContext context, Map conf) {
        this.name = name;
        this.context = context;
        this.conf = conf;
        config = new Config(conf);
    }
    
    public final boolean isEnabled() {
        return this.enabled; 
    }
    
    @Override
    public Map getConf() { 
        return config.getConf();
    }
    
    public final String getChannel() {
        return config.getChannel();
    }
    
    @Override
    public void start() { 
        if ( started ) {
            return;
        } 
        
        try {
            String uri = config.getUri();
            manager = new Manager(new URI(uri));
            socket = manager.socket("/" + config.getChannel());
            socket.connect();
            socket.on(EVENT_INVOKE, new InvokerHandler());
            started = true;
            System.out.println( name + " (" + config.getChannel()  +") coordinator connected at " + uri);
        } catch(Throwable ex) {
            System.out.println( name + "(" + config.getChannel() + ") coordinator connection not started caused by "+ ex.getMessage());
            ex.printStackTrace();  
        }
    } 
    
    @Override
    public void stop() {
        System.out.println( name +" : Stopping coordinator connection" );
        if (started) {
            socket.disconnect();
        }
        super.stop();
    }
    
    /**************************************************************************
    * This is used for handling direct or P2P responses. The queue to create
    * will be a temporary queue.
    ***************************************************************************/ 
    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception{
             
    }

    @Override
    public void send(Object data) {
        send(data, getChannel());
    }

    @Override
    public void sendText(String data) {
    }

    @Override
    public void send(Object data, String channel) {
        if (!started) {
            return;
        }        
        socket.emit(EVENT_REGISTER_SERVICE, data);
    }

    class InvokerHandler implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            Ack callback = (Ack) args[1];
            Map params = (Map) getData(args);
            GdxServiceExecutor executor = new GdxServiceExecutor(context, conf);
            Object result = executor.process(params);
            callback.call(result);
        }
    }
    
//    class Callback implements Emitter.Listener {
//        @Override
//        public void call(Object... args) {
//            System.out.println("data => " + args[0] );;
//        }
//    }
    
    protected Object getData(Object[] args) {
        return getData(args, null);
    }
    
    protected Object getData(Object[] args, String key) {
        if (args.length == 0) {
            return null;
        }
        
        if (args[0] instanceof JSONObject) {
            try {
                JSONObject json = (JSONObject) args[0];
                String str = json.toString();
                Map data = (Map) JsonUtil.toMap(str);
                if (key == null) {
                    return data;
                }
                return data.get(key);
            } catch (Exception ex) {
                System.out.println("Error extracting value for " + key);
                return null;
            }
        } else {
            return args[0];
        }
    }
    
    interface SocketIoExecutorService {
        Object process(Map params);
    }    
}

