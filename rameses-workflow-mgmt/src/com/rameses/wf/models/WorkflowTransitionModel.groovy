package com.rameses.wf.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.awt.*;
import java.awt.image.RenderedImage;
import javax.swing.*;
import javax.imageio.*;
import com.rameses.seti2.models.*;
import com.rameses.rcp.draw.figures.*;
import com.rameses.rcp.draw.interfaces.*;
import com.rameses.util.*;

public class WorkflowTransitionModel  {
   
    def item;
    def editing = true;
    def entity;
    def propList = [];
    
    def visibleWhen;
    boolean showPrompt;
    boolean showConfirm;
    String confirmMessage;
    
    void init() {
        entity = MapBeanUtils.copy( item.info );
        if( !entity.properties ) entity.properties = [:];
        entity.properties.each { k,v->
            if(k == "visibleWhen") {
                visibleWhen = v;
            }
            else if( k == "showPrompt" ) {
                showPrompt = Boolean.parseBoolean( v.toString());
            }
            else if( k == "showConfirm") {
                showConfirm = Boolean.parseBoolean( v.toString());
            }
            else if( k == "confirmMessage") {
                confirmMessage = v;
            }
            else {
                propList << [ key:k, value: v ];
            }
        }
        if( !entity.idx ) entity.idx = 0;
        entity.properties.clear();
    }
    
    def propListModel = [
        fetchList: { o->
            return propList;
        },
        addItem: { o->
            propList << o;
        },
        removeItem: { o->
            propList.remove(o);
        }
    ] as EditorListModel;
    
    def doOk() {
        item.caption = entity.action;
        item.info.clear();
        item.info.putAll( entity );
        propList.each {
            def val = it.value; 
            if((""+val).matches("true|false")) val = Boolean.parseBoolean( ""+val );
            entity.properties[(it.key)] = val;
        }
        if(visibleWhen) entity.properties.visibleWhen = visibleWhen;
        if(showPrompt) entity.properties.showPrompt = showPrompt;
        if( showConfirm) {
            entity.properties.showConfirm = showConfirm;
            if( confirmMessage) entity.properties.confirmMessage = confirmMessage;
        }
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}

