/*
 * ContentMap.java
 *
 * Created on July 1, 2012, 7:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import java.util.HashMap;
import java.util.Map;



public class ContentMap extends HashMap {
    
    private boolean editable = false;
    
    public ContentMap() {
        this.editable = AnubisContext.getCurrentContext().getProject().isEditable();
    }
    
    public Object get(Object key) {
        AnubisContext ctx = AnubisContext.getCurrentContext();
        String skey = key.toString();
        if( skey.equals("_content")) {
            return super.get("content");
        } else if(skey.startsWith("_")) {
            skey = skey.substring(1);
            return getBlockContent(skey);
        } else if( skey.equals("PARAMS")) {
            return  ctx.getParams();
        } else if (skey.equals("PAGE")) {
            return ctx.getCurrentPage();
        } else if( skey.equals("ANUBIS")) {
            return this;
        } else if( skey.equals("VARS")) {
            return ctx.getCurrentPage().getVars();
        } else if( skey.equals("SERVICE")) {
            return  ctx.getProject().getServiceManager();
        } else if( skey.equals("MODULE")) {
            Module m = ctx.getModule();
            if(m==null) return new HashMap();
            return m;
        } else if( skey.equals("THEME")) {
            Theme t = ctx.getProject().getDefaultTheme();
            if(t!=null) return t;
            return new HashMap();
        } else if( skey.equals("PROJECT")) {
            return  ctx.getProject();
        } 
        else if( skey.equals("REQUEST")) {
            return  ctx.getRequest();
        }    
        else if( skey.equals("SESSION")) {
            return ctx.getSession();
        } else if(skey.equals("anubisContext")) {
            return ctx;
        } else {
            return super.get(key);
        }
    }
    
    
    public String getBlockContent(String skey) {
        try {
            Project project = AnubisContext.getCurrentContext().getProject();
            return project.getBlockManager().getBlockContent( skey );
        } catch(Exception ex) {
            return "<span class='element-error' title='" + ex.getMessage() + "'>[block:"+skey+"]</span>";
        }
    }
    
    public String getWidget( String name, Map options ) {
        Project project = AnubisContext.getCurrentContext().getProject();
        try {
            return project.getWidgetManager().getWidgetContent(name, options);
        } catch (Exception ex) {
            //ex.printStackTrace();
            return "<span class='element-error' title='" + ex.getMessage() + "'>[widget:"+name+"]</span>";
        }
    }
    
    public Object call(String action, Map params) throws Exception {
        Project project = AnubisContext.getCurrentContext().getProject();
        return project.getActionManager().getActionCommand(action).execute(params);
    }
    
    public Folder getFolder( String name ) {
        return getFolder(name, null); 
    }

    public Folder getFolder( String name, String moduleName ) 
    {
        try 
        {
            Project project = AnubisContext.getCurrentContext().getProject();
            return project.getFolderManager().getFolder(name, moduleName);
        } 
        catch(Exception e) { 
            return null; 
        } 
    }    
    
    public File getFile(String name) {
        try {
            Project project = AnubisContext.getCurrentContext().getProject();
            return project.getFileManager().getFile(name);
        } catch(Exception e){
            return null;
        }
    }
    
    public String translate(String key, String value) {
        if(AnubisContext.getCurrentContext()==null || AnubisContext.getCurrentContext().getCurrentLocale()==null) return null;
        return AnubisContext.getCurrentContext().getCurrentLocale().translate(key, value);
    }
    
    public String translate(String key, String value, String lang) {
        Project project = AnubisContext.getCurrentContext().getProject();
        LocaleSupport support = project.getLocaleSupport( lang );
        if(support==null) return null;
        return support.translate( key, value );
    }
    
    
    
    
}
