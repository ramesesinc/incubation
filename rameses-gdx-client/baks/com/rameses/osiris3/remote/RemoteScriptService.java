/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.remote;

import com.rameses.osiris3.core.ContextService;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.script.ScriptService;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.util.URLDirectory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Elmo
 * This is run by the client.
 */
public class RemoteScriptService extends ContextService {

    private boolean started;
    
    public int getRunLevel() {
        return 40;
    }
    
    public Class getProviderClass() {
        return RemoteScriptService.class;
    }

    private void fetchResults(Enumeration<URL> e, final Set<String> list) {
        if(e==null) return;
        while(e.hasMoreElements()) {
            final URL parent = e.nextElement();
            URLDirectory dir = new URLDirectory(parent);
            dir.list( new URLDirectory.URLFilter() {
                public boolean accept(URL u, String filter) {
                    list.add( "remote/"+ filter.substring(filter.lastIndexOf("/")+1)  );
                    return false;
                }
            });
        }
    }
    
    private Set<String> rescanScripts()  {
        final Set<String> list = new HashSet();
        //scan from shared first
        try {
            fetchResults( context.getClassLoader().getResources("scripts/remote"), list );
        }
        catch(Exception e) {
            System.out.println("eror rescan scripts. " + e.getMessage());
        }
        return list;
    }

    /***
     * This returns true if there are remote services found.
     * 
     **/
    private boolean registerToServer() throws Exception {
        //get the script service
        ScriptService svc = context.getService( ScriptService.class );
        Set<String> list =  rescanScripts();
        
        //do not continue if there are no scanned remote services
        if(list.size()==0) return false;
        
        Map defs = new HashMap();
        for(String s: list) {
            ScriptInfo info = svc.findScriptInfo(s);
            Map metaInfo = info.getMetaInfo(  context );
            
            Map newInfo = new HashMap();
            String kname = info.getName().substring("remote/".length() );
            defs.put( kname , newInfo);
            
            //define the class
            List methods = new ArrayList();
            newInfo.put("name", kname);
            newInfo.put("serviceName", kname);
            newInfo.put("methods", methods);
            
            //place methods and parameters
            Map metaMethods = (Map)metaInfo.get("methods");
            for(Object o : metaMethods.entrySet()) {
                Map.Entry me = (Map.Entry)o;
                Map mval = (Map)me.getValue();
                Map method = new HashMap();
                method.put("name", mval.get("name"));
                method.put("parameters", mval.get("parameters") );
                method.put("returnValue", mval.get("returnValue"));
                methods.add( method ); 
            }
         }
            
         XConnection xconn = context.getResource( XConnection.class, "remote-script" );
         if(xconn !=null) {
             RemoteScriptConnection rs = (RemoteScriptConnection)xconn;
             try {
                rs.register(defs);
                return true;
             }
             catch(Exception ex) {
                 System.out.println("ERROR IN LOADING RemoteScriptRegistryService");
             }
         }
         else {
             System.out.println("RemoteScriptService not started successfully bec. there is no remote-script connection defined");
         }
         return false;
    }
    
    public void start() throws Exception {
       started = registerToServer();
        if(started){
            System.out.println("STARTING REMOTE SCRIPT SERVICE");
        }
    }

    public void stop() throws Exception {
        if(started) {
            System.out.println("STOPPING REMOTE SCRIPT SERVICE");
        }
    }

    
}
