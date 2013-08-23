/*
 * AnubisPollServlet.java
 *
 * Created on March 29, 2013, 8:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.JsonUtil;
import com.rameses.util.SealedMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 *
 * to poll example (get)
 *
 * http://host/poll/channel1/token1
 *
 * to send message (post)
 *
 * http://host/poll/channel1?data=hello
 */
public class AnubisPollServlet extends HttpServlet {
    
    private static Map<String, Channel> channels = new Hashtable();
    private static ExecutorService thread = Executors.newCachedThreadPool();
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        ObjectInputStream in = null;
        try 
        {
            in = new ObjectInputStream(req.getInputStream());
            
            Object o = in.readObject();
            if (o instanceof SealedMessage) 
                o = ((SealedMessage)o).getMessage(); 

            Collection collection = null;
            if (o instanceof Collection)
                collection = (Collection) o; 
            else if (o instanceof Object[]) 
                collection = Arrays.asList((Object[]) o);
            
            if (collection == null) return;

            String channelName = req.getPathInfo().substring(1);
            Channel channel = getChannel(channelName);
            
            Iterator itr = collection.iterator(); 
            while (itr.hasNext()) 
            {
                Map data = (Map) itr.next();
                if (data == null) continue;
                
                channel.send(data);
            }
        } 
        catch(IOException ioe) { throw ioe; }
        catch(Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
        finally {
            try {in.close();} catch(Exception ign){;}
        }        
    }
    
    private void writeResponse(String msg, HttpServletResponse res)  throws ServletException, IOException{
        res.setContentType("text/json");
        PrintWriter pw = res.getWriter();
        if(msg==null) msg = "#NULL";
        pw.write( msg );
        pw.close();
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PollWorker worker = (PollWorker)req.getAttribute(PollWorker.class.getName());
        if(worker==null) {
            String path = req.getPathInfo().substring(1);
            String[] arr = path.split("/");
            String channelName = arr[0];
            String tokenid = arr[1];
            Channel channel = getChannel(channelName);
            Continuation cont = ContinuationSupport.getContinuation(req);
            MessageToken mt = channel.createToken(tokenid);
            worker = new PollWorker(mt, cont);
            req.setAttribute(PollWorker.class.getName(), worker);
            
            //set timeout at 30 seconds
            cont.setTimeout( 30000 );
            cont.suspend();
            
            Future future = thread.submit( worker );
            worker.future = future;
        } else {
            //timed out, no messages available
            if(worker.continuation.isExpired()) {
                writeResponse("{status: 'timeout'}", resp);
            } else {
                
                String result = worker.result;
                //System.out.println("token "+worker.token.tokenId + " ->"+result);
                writeResponse(result, resp);
            }
            worker.destroy();
        }
    }
    
    private class PollWorker implements Runnable {
        
        private MessageToken token;
        private Continuation continuation;
        private String result;
        private Future future;
        
        public PollWorker( MessageToken mt, Continuation c ) {
            continuation = c;
            token = mt;
        }
        
        public void run() {
            Map map = token.read();
            if(map!=null) {
                map.put("status", "OK");
                result = JsonUtil.toString( map );
            } else {
                result = null;
            }
            continuation.resume();
        }
        
        public void destroy() {
            future.cancel(true);
            token = null;
            continuation = null;
            result = null;
            future = null;
        }
        
    }
    
    
    
    private class Channel {
        
        private CopyOnWriteArrayList<MessageToken> handlers = new CopyOnWriteArrayList();
        private String name;
        
        public Channel(String name) {
            this.name = name;
        }
        public void send(Map map) {
            for(MessageToken mh: handlers) {
                mh.handle( map );
            }
        }
        public synchronized MessageToken createToken(String token) {
            for(MessageToken mh: handlers) {
                if(mh.tokenId.equals(token)) {
                    return mh;
                }
            }
            MessageToken mt = new MessageToken(token);
            handlers.add( mt );
            return mt;
        }
    }
    
    public class MessageToken {
        private String tokenId;
        private LinkedBlockingQueue<Map> queue = new LinkedBlockingQueue();
        public MessageToken(String tokenid) {
            this.tokenId = tokenid;
        }
        public void handle(Map map) {
            queue.add( map );
        }
        public Map read() {
            try {
                return (Map) queue.poll( 30, TimeUnit.SECONDS );
            } catch (InterruptedException ex) {
                return null;
            }
        }
        
    }
    
    public synchronized Channel getChannel(String name) {
        return getChannel(name, true);
    }
    
    public synchronized Channel getChannel(String name, boolean autoCreate) {
        if( !channels.containsKey(name) ) {
            if(!autoCreate)
                throw new RuntimeException("Channel " + name + " does not exist!");
            Channel channel = new Channel(name);
            channels.put(name, channel);
        }
        return channels.get( name );
    }
    
    
}
