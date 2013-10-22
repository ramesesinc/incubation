/*
 * SessionContext.java
 *
 * Created on February 21, 2009, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * this is a package level class created only by
 * the AppContext
 */
public class SessionContext {
    
    /**
     * extended permission pattern
     * @description
     *    this expression matches permission name written in formats shown below:
     *    a. <module_name>:<workunit_name>.<action_name>
     *    b. <workunit_name>.<action_name>
     */
    public static final Pattern EXT_PERM_PATTERN = Pattern.compile("(?:(.*):)?[^\\.]*\\.[^\\.]*$");
    
    
    private AppContext context;
    private Map env = new EnvMap();
    private SecurityProvider securityProvider;
    protected Map folderIndex = new Hashtable();
    
    //this holds a map of categorized invokers
    protected Map invokers = new Hashtable();
    
    private Map properties = new Hashtable();
    
    
    protected SessionContext(AppContext ctx) {
        this.context = ctx;
        env = new EnvMap(ctx.getEnv());
        env.put("CLIENTTYPE", "desktop");
    }
    
    public Map getEnv() {
        return env;
    }
    
    public Module getModule(String name) {
        Module c = context.getModule(name);
        if( c == null )
            throw new IllegalStateException( "Module not found : " + name);
        return c;
    }
    
    public WorkUnit getWorkUnit( String name ) {
        return context.getWorkUnit(name);
    }
    
    //the default permission is allow true. The exception is false.
    //this is being called
    public boolean checkPermission(String domain, String role, String name) {
        if(role==null && name == null ) return true;
        return securityProvider.checkPermission(domain, role, name);
    }
    
    
    //if there is no type specified set this as folder
    public List getInvokers() {
        return getInvokers(null);
    }
    
    public List getInvokers( String type ) {
        return getInvokers(type, true);
    }
      
    //this is an overridable additional method to allow the application provider to
    //have additional chcking of the invoker before allowing it to be accessed
    public boolean checkInvoker( Invoker inv ) {
        return true;
    }
    
    public List getInvokers( String type, boolean applySecurity ) {
        if (type == null) type = "folder";
        if (!invokers.containsKey(type)) {
            List list = new ArrayList();
            
            Iterator iter = context.getInvokers().iterator();
            while (iter.hasNext()) {
                Invoker inv = (Invoker)iter.next();
                String itype = (inv.getType() == null) ? "folder" : inv.getType();
                if(itype.matches(type) ) {
                    boolean showIt = true;
                    String permission = inv.getPermission();
                    String role = inv.getRole();
                    String domain = inv.getDomain();
                    if(applySecurity && (role!=null || permission!=null)) {
                        showIt = checkPermission(domain, role, permission );
                    } 
                    if(showIt) showIt = checkInvoker(inv);
                    if (showIt) list.add(inv);
                    
                    //System.out.println("checkpermission: showit="+showIt + ", caption="+inv.getCaption() + ", domain="+domain + ", role="+role + ", permissino="+permission);
                }
            }
           
            Collections.sort(list);
            invokers.put(type, list);
            return list;
        } else {
            return (List)invokers.get(type);
        }
    }
    
    public final List<Invoker> getInvokersByWorkunitid(String id) {
        List<Invoker> list = new ArrayList();
        if (id == null || id.length() == 0) return list; 
        
        Iterator itr = context.getInvokers().iterator();
        while (itr.hasNext()) {
            Invoker inv = (Invoker) itr.next();
            if (id.equals(inv.getWorkunitid())) {
                list.add(inv); 
            } 
        } 
        Collections.sort(list); 
        return list; 
    }
    
    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }
    
    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }
    
    //returns a list of folders including the invokers
    public List getFolders(String name) {
        return getFolders(name, null);
    }
    
    public List getFolders(Folder parent) {
        return getFolders(parent, null);
    }
    
    public List getFolders(String name, InvokerSource invokerSrc) {
        if( !name.startsWith("/")) name = "/" + name;
        Folder folder = (Folder)context.getFolderManager().getFolders().get(name);
        if(folder==null)
            return null;
        else
            return getFolders(folder, invokerSrc);
    }
    
    public List getFolders(Folder parent, InvokerSource invokerSrc) {
        String fullId = parent.getFullId();
        List list = null;
        if( folderIndex.get( fullId )==null ) {
            list = new ArrayList();
            Iterator iter = parent.getFolders().iterator();
            while(iter.hasNext()) {
                list.add(iter.next());
            }
            List invokers = getInvokers("folder");
            
            //force add all the invokers
            if(invokerSrc!=null) {
                invokers.addAll( invokerSrc.getInvokers("folder") );
            }
            
            iter = invokers.iterator();
            while(iter.hasNext()) {
                Invoker inv = (Invoker)iter.next();
                String fid = (String)inv.getProperties().get("folderid");
                if(fid!=null) {
                    if( !fid.startsWith("/")) fid = "/" + fid;
                    if( fid.equals(parent.getFullId())) {
                        if( inv.getName()==null ) {
                            inv.setName(fid);
                        }
                        String fname = inv.getName();
                        if( fname != null) {
                            Folder f = new Folder( fname, inv.getCaption(), parent, inv);
                            list.add(f);
                        }
                    }
                }
            }
            
            Collections.sort(list);
            folderIndex.put(fullId, list);
        } else {
            list = (List)folderIndex.get(fullId);
        }
        return list;
    }
    
    public ClassLoader getClassLoader() {
        return context.getClassLoader();
    }
    
    public Map getProperties() {
        return properties;
    }
    
}
