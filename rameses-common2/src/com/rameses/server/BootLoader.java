package com.rameses.server;

import com.rameses.util.ConfigProperties;
import com.rameses.util.Service;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BootLoader 
{
    private ExecutorService thread;
    private Map<String, ServerLoaderProvider> providers = new Hashtable();
    private Map<String, ServerLoader> servers = new Hashtable();
    
    private void initProviders() {
        providers.clear();
        Iterator<ServerLoaderProvider> iter = Service.providers(ServerLoaderProvider.class, BootLoader.class.getClassLoader());
        while(iter.hasNext()) {
            ServerLoaderProvider p = iter.next();
            providers.put( p.getName(), p );
        }
    }
    
    public static void main(String[] args) throws Throwable {
        try {
            BootLoader main = new BootLoader();
            main.start();
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    
    public void start() throws Exception {
        String userdir = System.getProperty("user.dir");
        String basedir = System.getProperty("osiris.base.dir", userdir);
        String rundir = System.getProperty("osiris.run.dir", userdir);
        System.getProperties().put("osiris.base.dir", basedir);
        System.getProperties().put("osiris.run.dir", rundir);

        Shutdown.removePID();
        createInstancePID(); 
        initProviders();
        
        String baseURL = "file:///" + basedir;
        ConfigProperties conf = new ConfigProperties(rundir + "/server.conf");
        
        Iterator entries = conf.getGroups().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String groupName = entry.getKey().toString();
            Map groupConf = resolveConf((Map) entry.getValue());
            String providerName = (String) groupConf.get("provider");
            
            if(providerName !=null) {
                ServerLoaderProvider sp = providers.get(providerName);
                if( sp == null ) {
                    System.out.println("[WARN] '"+providerName+"' provider name not found");
                    continue;
                }
                
                ServerLoader loader = sp.createServer( groupName );
                loader.init( baseURL, groupConf );
                servers.put( groupName, loader );
                ServerPID.add( groupName ); 
            }
        }
        
        //load all servers
        thread = Executors.newFixedThreadPool(servers.size());  
        
        System.out.println("starting servers");        
        for(final ServerLoader loader: servers.values() ) {
            thread.submit( new Runnable(){
                public void run() {
                    try {
                        loader.start();
                    } 
                    catch(InterruptedException ie) {}
                    catch(Exception e) {
                        System.out.println("failed to start caused by " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        try { loader.stop(); } catch(Exception ign){;} 
                    }
                }
            });
        }
        
        final ShutdownAgent shutdownAgent = new ShutdownAgent();
        final Runnable shutdownHook = new Runnable() { 
            public void run() { 
                shutdownAgent.cancel(); 
                onshutdown(); 
            } 
        }; 
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
        //start the shutdown agent
        new Thread(shutdownAgent).start(); 
    }
        
    private void onshutdown() {
        for (ServerLoader svr : servers.values()) {
            try {
                System.out.println("Stopping Server: " + svr.getClass().getSimpleName() + "...");
                svr.stop(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
            }
        }
        
        //try { 
        //    thread.shutdownNow(); 
        //} catch(Throwable ign){;}  
        
        removeInstancePID(); 
        Shutdown.removePID(); 
    }
    
    private File getPID() {
        String rundir  = System.getProperty("osiris.run.dir"); 
        return new File(rundir + "/.osiris_pid"); 
    }    
    private void createInstancePID() {
        File file = getPID(); 
        if ( file.exists() ) {
            throw new RuntimeException("Cannot start the server because there is already a process-ID instance."); 
        } 
        
        try {
            file.createNewFile(); 
        } catch (IOException e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    private void removeInstancePID() { 
        File file = getPID(); 
        if ( file.exists() ) {
            try { 
                file.delete(); 
            } catch(Throwable t) {
                //do nothing 
            } 
        } 
    } 
    
    private Map resolveConf(Map conf) {
        Map newconf = new LinkedHashMap();
        if (conf == null) return newconf;
        
        Iterator keys = conf.keySet().iterator(); 
        while (keys.hasNext()) {
            Object key = keys.next(); 
            Object val = resolveValue(conf.get(key), newconf); 
            newconf.put(key, val); 
        } 
        return newconf;
    }
    
    private Object resolveValue(Object value, Map conf) { 
        if (value == null) return null;
                 
        int startidx = 0; 
        boolean has_expression = false; 
        String str = value.toString();         
        StringBuilder builder = new StringBuilder(); 
        while (true) {
            int idx0 = str.indexOf("${", startidx);
            if (idx0 < 0) break;
            
            int idx1 = str.indexOf("}", idx0); 
            if (idx1 < 0) break;
            
            has_expression = true; 
            String skey = str.substring(idx0+2, idx1); 
            builder.append(str.substring(startidx, idx0)); 
            
            Object objval = conf.get(skey); 
            if (objval == null) objval = System.getProperty(skey); 
            
            if (objval == null) { 
                builder.append(str.substring(idx0, idx1+1)); 
            } else { 
                builder.append(objval); 
            } 
            startidx = idx1+1; 
        } 
        
        if (has_expression) {
            builder.append(str.substring(startidx));  
            return builder.toString(); 
        } else {
            return value; 
        }
    }     
    
    // <editor-fold defaultstate="collapsed" desc=" ShutdownAgent ">  
    
    private class ShutdownAgent implements Runnable {
        
        BootLoader root = BootLoader.this;
        
        private boolean cancelled;
        
        void cancel() { 
            this.cancelled = true; 
        } 
        
        public void run() {
            while(true) {
                if (cancelled) break;
                
                try { 
                    Thread.sleep(2000); 
                } catch(Throwable t) {;} 
                
                if (cancelled) break; 
                if (Shutdown.hasPID()) {
                    processShutdown(); 
                    break; 
                } 
                
                File file = root.getPID();
                if ( !file.exists() ) { 
                    processShutdown(); 
                    break; 
                } 
            } 
        }     
        
        void processShutdown() { 
            cancelled = true; 
            try { 
                root.onshutdown();  
            } catch(Throwable t) {
                t.printStackTrace(); 
            }
            try { 
                System.out.println("Stopping JVM...");
                System.exit(1); 
            } catch(Throwable t) {
                t.printStackTrace();
            } 
        }
    }
    
    // </editor-fold>     
}
