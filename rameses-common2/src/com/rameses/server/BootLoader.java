package com.rameses.server;

import com.rameses.util.ConfigProperties;
import com.rameses.util.Service;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
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
            Map groupConf = (Map)entry.getValue();
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
        } catch(Exception ign){;}  
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
                    System.exit(0); 
                } catch(Throwable t) {;} 
                
                break; 
            } 
        }        
    }
}
