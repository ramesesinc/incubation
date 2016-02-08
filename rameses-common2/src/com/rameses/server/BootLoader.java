package com.rameses.server;

import com.rameses.util.ConfigProperties;
import com.rameses.util.Service;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
        removePids();
        initProviders();
        
        String userdir = System.getProperty("user.dir");
        String basedir = System.getProperty("osiris.base.dir", userdir);
        String rundir = System.getProperty("osiris.run.dir", userdir);
        System.getProperties().put("osiris.base.dir", basedir);
        System.getProperties().put("osiris.run.dir", rundir);
        
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
                onshutdown(); 
                shutdownAgent.cancel(); 
            } 
        }; 
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
        //start the shutdown agent
        new Thread(shutdownAgent).start(); 
    }
    
    private void onshutdown() {
        try {
            thread.shutdownNow(); 
        } catch(Throwable ign){;}  
    }
    
    private boolean removePids() {
        String userdir  = System.getProperty("user.dir"); 
        String rundir   = System.getProperty("osiris.run.dir", userdir); 
        File file = new File(rundir + "/.shutdown_pid"); 
        if (!file.exists()) return false; 

        //remove the file
        try { file.delete(); } catch(Throwable t) {;}
        return true; 
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
    
    private class ShutdownAgent implements Runnable 
    {
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
                if (!root.removePids()) continue; 
                
                cancelled = true;              
                root.onshutdown(); 
                try { 
                    System.exit(1); 
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                
                break; 
            } 
        }        
    }
}
