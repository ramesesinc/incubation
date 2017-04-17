package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
        
class RuleMgmtModel extends DefaultListController {
           
    @Service("RuleMgmtService")
    def service;

    @Script("Template")
    def template;

    @Invoker
    def invoker;
    
    def ruleHtml;
    def rulegroups;
    def rulestates;
    String entityName = "rulemgmt";
    String formName = 'rulemgmt';
    def ruleset;

    @PropertyChangeListener
    def listener = [
        "query.(state|title)": { 
            reload();
        },
        "query.rulegroup": { 
            load();
        }
    ]

    public String getRuleHtml() {
        if(!selectedEntity) return  "";
        def o = selectedEntity;
        if( !o.ruleHtml ) {
            def z = service.open( [objid: o.objid] );
            o.ruleHtml = template.render( "html/rule_html", [rule: z, editable:false] );
        }
        return o.ruleHtml;
    }

    void init() {
       ruleset = tag;
       if(invoker.properties.tag) {
           ruleset = invoker.properties.tag;
       }
       query.ruleset = ruleset;
       rulegroups = service.getRulegroups([ruleset:query.ruleset]);
       rulestates = [ "DRAFT", "DEPLOYED", "APPROVED", "UPGRADE"];
       if( rulegroups.size() > 0 ) {
            query.rulegroup = rulegroups[0].name;
       }
    }

    void search() {
        reload();
    }

    boolean getDebug() {
        def d =  OsirisContext.clientContext.appEnv['app.debug'];
        if(!d) return false;
        return d;
    }

    def showTools() {
        def popupMenu = new PopupMenuOpener();
        def list1 = InvokerUtil.lookupOpeners( "rulemgmt:tools", [ruleset:ruleset] );
        def list2 = [];
        try {
            list2 = InvokerUtil.lookupOpeners( ruleset + ":tools", [:] );
        }
        catch(ign){;}
        def list = list1+list2;
        list.each{
            popupMenu.add( it );
        }
        return popupMenu;
    }
            
}