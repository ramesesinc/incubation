/*
 * ActionManager.java
 *
 * Created on July 10, 2012, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import com.rameses.io.StreamUtil;
import com.rameses.util.ConfigProperties;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ActionManager {
    
    private Map<String, ActionCommand> actions = new Hashtable();
    private static String ACTION_DIR = "actions";
    
    private List<MappingEntry> mappings = new ArrayList();
    private Map<String, List> cachedActions = new HashMap();
    
    public void init(ConfigProperties conf) {
        Map masters = conf.getProperties( "page-action-mapping" );
        if(masters!=null) {
            //load master template mapping
            for(Object o: masters.entrySet()) {
                Map.Entry me = (Map.Entry)o;
                mappings.add( new MappingEntry(me.getKey()+"", me.getValue()+"" ));
            }
        }
    }
    
    public void fireActions( String path, Map params ) throws Exception {
        List<String> list = cachedActions.get(path);
        if(list==null) {
            list = new ArrayList();
            for( MappingEntry me: mappings ) {
                if(me.matches(path)) {
                    String[] actions = me.getTemplates();
                    for(String s: actions) {
                        list.add( s );
                    }
                }
            }
            cachedActions.put(path, list);
        }
        for(String s: list) {
            getActionCommand(s).execute( params );
        }
    }
    
    
    public ActionCommand getActionCommand(String name) throws Exception {
        if(name.startsWith("/")) name = name.substring(1);
        if( !actions.containsKey(name)) {
            ActionCommand c = createActionCommand(name);
            actions.put(name, c);
        }
        return actions.get(name);
    }
    
    //check if name has a prefix and it is a module. otherwise it is located in project
    protected ActionCommand createActionCommand(final String name) throws Exception {
        AnubisContext ctx = AnubisContext.getCurrentContext();
        Project project = ctx.getProject();

        
        String moduleName = null;
        String action = name;
        
        if(action.indexOf("/")>0) {
            String[] arr = ProjectUtils.getModuleNameFromFile( action, project );
            if(arr != null ) {
                moduleName = arr[0];
                action = arr[1];
            }
        }
        
        List<String> urls = new ArrayList();
        
        InputStream is = null;
        try {
            if(moduleName!=null) {
                Module mod =project.getModules().get(moduleName);
                urls.add( ContentUtil.correctUrlPath(  mod.getUrl(), ACTION_DIR, action) );
                urls.add( ContentUtil.correctUrlPath( mod.getProvider() , ACTION_DIR , action) );
            } else {
                urls.add( ContentUtil.correctUrlPath(project.getUrl(), ACTION_DIR , action) );
                urls.add( ContentUtil.correctUrlPath(ctx.getSystemUrl(), ACTION_DIR, action) );
            }
            is = ContentUtil.getResources( (String[]) urls.toArray(new String[]{}), action );
            return new GroovyActionCommand(is);
        } catch(Exception e) {
            throw e;
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }
    
    private class GroovyActionCommand implements ActionCommand {
        
        private Script script;
        
        public GroovyActionCommand(InputStream is) throws Exception {
            GroovyShell sh = new GroovyShell();
            script = sh.parse( StreamUtil.toString(is) );
        }
        
        public Object execute(Map params) throws Exception {
            AnubisContext ctx = AnubisContext.getCurrentContext();
            Project project = ctx.getProject();
            
            if( params == null ) params = new HashMap();
            Script sc = script.getClass().newInstance();
            sc.setProperty("ENV", ctx.getEnv());
            sc.setProperty("PARAMS", ctx.getParams() );
            sc.setProperty("PROJECT", project );
            sc.setProperty("SERVICE", project.getServiceManager() );
            sc.setProperty( "REQUEST", ctx.getRequest() );
            sc.setProperty( "RESPONSE", ctx.getResponse() );
            sc.setProperty("SESSION", ctx.getSession() );
            return sc.run();
        }
    }
    
}
