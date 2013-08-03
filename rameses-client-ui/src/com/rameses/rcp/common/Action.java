/*
 * AbstractAction.java
 *
 * Created on October 19, 2009, 7:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.util.ValueUtil;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Action implements Comparable<Action> 
{    
    private String name;
    private String caption;
    private String icon;
    private char mnemonic;
    private String permission;

    private String tooltip;
    private String visibleWhen;
    private String category;
    
    //index is used for sorting
    private int index;
    private boolean immediate;
    private boolean update;
    
    private String role;
    private String domain;
    private boolean showCaption;

    private ActionHandler actionHandler;
    private Action parent;    
    private Map properties = new Hashtable();    
    private Map parameters = new HashMap();
    
    public Action() {        
    }
    
    public Action(String name) {
        this.name = name;
    }

    public Action(String name, String caption, String icon ) {
        this.name = name;
        this.caption = caption;
        this.icon = icon;
    }
    
    public Action(String name, String caption, String icon, char mnemonic ) {
        this.name = name;
        this.caption = caption;
        this.icon = icon;
        if(mnemonic!=' ') this.mnemonic = mnemonic;
    }
    
    public Action(String name, String caption, String icon, char mnemonic, String perm ) {
        this.name = name;
        this.caption = caption;
        this.icon = icon;
        this.mnemonic = mnemonic;
        this.permission = perm;
    }
    
    public int compareTo(Action a) {
        return index - a.index;
    }

    //overridable
    public Object execute() { return null; }
    
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  "> 
    
    public String getCaption() 
    {
        if ( ValueUtil.isEmpty(caption) && !ValueUtil.isEmpty(properties.get("caption")))
            return properties.get("caption")+"";
        
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getIcon() {
        if( ValueUtil.isEmpty(icon) && !ValueUtil.isEmpty(properties.get("icon")))
            return properties.get("icon")+"";
        
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Action getParent() { return parent; }    
    public void setParent(Action parent) {
        this.parent = parent;
    }
        
    public String getName() { return name; }    
    public void setName(String name) { this.name = name; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
    
    public String getCategory() { return category; }    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPath() {
        //path will be the name of the category plus its name.
        return category + "/" + name;
    }
    
    public Map getProperties() { return properties; }    
    
    public char getMnemonic() 
    {
        if ( mnemonic == '\u0000' && !ValueUtil.isEmpty(properties.get("mnemonic")))
            return (properties.get("mnemonic")+"").charAt(0);
        
        return mnemonic;
    }
    
    public void setMnemonic(char mnemonic) {
        this.mnemonic = mnemonic;
    }
    
    public boolean isImmediate() { return immediate; }    
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
    
    public String getPermission() { return permission; }    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    @Deprecated
    public Map getParameters() { return parameters; }
    
    @Deprecated
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
    
    public Map getParams() { return parameters; }    
    public void setParams(Map params) { 
        this.parameters = params; 
    }
    
    public boolean isUpdate() { return update; }    
    public void setUpdate(boolean update) {
        this.update = update;
    }
    
    public String getTooltip() {
        if(tooltip==null) {
            if ( !ValueUtil.isEmpty(properties.get("tooltip")) )
                return properties.get("tooltip")+"";
            else
                return getCaption();
        }
        
        return tooltip;
    }
    
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
    
    public String getVisibleWhen() {
        if ( ValueUtil.isEmpty(visibleWhen) && !ValueUtil.isEmpty(properties.get("visibleWhen")) ) {
            return properties.get("visibleWhen")+"";
        }
        return visibleWhen;
    }
    
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }
    
    public String getRole() { return role; }
    public void setRole(String role) {
        this.role = role;
    }

    public String getDomain() { return domain; }
    public void setDomain(String domain) {
        this.domain = domain;
    }

    //force the showing of captions if icon is null 
    public boolean isShowCaption() 
    {
        if (icon == null) return true;
        
        return showCaption;
    }

    public void setShowCaption(boolean showCaption) {
        this.showCaption = showCaption;
    }     
    
    public void setActionHandler(ActionHandler actionHandler) {
        this.actionHandler = actionHandler; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" clone utility "> 
    
    public Action clone() 
    {
        Action newAction = new Action(); 
        newAction.setName(getName());
        newAction.setCaption(getCaption());
        newAction.setIcon(getIcon());
        newAction.setMnemonic(getMnemonic()); 
        newAction.setPermission(getPermission());
        newAction.setTooltip(getTooltip());
        newAction.setVisibleWhen(getVisibleWhen());
        newAction.setCategory(getCategory()); 
        newAction.setIndex(getIndex()); 
        newAction.setImmediate(isImmediate());
        newAction.setUpdate(isUpdate()); 
        newAction.setRole(getRole());
        newAction.setDomain(getDomain()); 
        newAction.setShowCaption(isShowCaption());
        newAction.setParent(getParent());
        newAction.getProperties().putAll(getProperties()); 
        newAction.getParams().putAll(getParams());
        return newAction; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" toMap utility "> 
    
    public Map toMap() {
        Map map = new HashMap();
        map.putAll(getProperties());
        map.put("name", getName());
        map.put("caption", getCaption());
        map.put("icon", getIcon());
        map.put("mnemonic", getMnemonic());
        map.put("permission", getPermission());
        map.put("tooltip", getTooltip());
        map.put("visibleWhen", getVisibleWhen());
        map.put("category", getCategory());
        map.put("index", getIndex());
        map.put("immediate", isImmediate());
        map.put("update", isUpdate());
        map.put("role", getRole());
        map.put("domain", getDomain());
        map.put("showCaption", isShowCaption());
        map.put("parent", getParent());
        return map;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionHandler interface "> 
    
    public static interface ActionHandler {
        Object execute(Action action); 
    }
    
    // </editor-fold>
}
