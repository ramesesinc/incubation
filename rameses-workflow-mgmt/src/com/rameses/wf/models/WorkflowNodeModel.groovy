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

public class WorkflowNodeModel  {
    
    def item;
    def editing = true;
    def entity;
    def propList = [];
    
    @PropertyChangeListener
    def listener = [
        "entity.idx" : { o->
            item.index = o;
        }
    ]
    
    
    void init() {
        entity = MapBeanUtils.copy( item.info );
        entity.nodetype = item.type;
        if( !entity.properties ) entity.properties = [:];
        entity.properties.each { k,v->
            propList << [ key:k, value: v ];
        }
    }
    
    def propListModel = [
        fetchList: { o->
            return propList;
        },
        addItem: { o->
            propList.add(o);
        },
        removeItem: { o->
            propList.remove(o);
        }
    ] as EditorListModel;
    
    def doOk() {
        if( !item.info.name ) {
            item.id = entity.name;
        }
        item.caption = entity.title;
        item.info.clear();
        item.info.putAll( entity );

        propList.each {
            def val = it.value; 
            if(val?.matches("true|false")) val = Boolean.parseBoolean( val );
            entity.properties[(it.key)] = val;
        }
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    

    
}

