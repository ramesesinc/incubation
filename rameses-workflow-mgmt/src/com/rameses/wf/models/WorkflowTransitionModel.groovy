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
    
    void init() {
        entity = MapBeanUtils.copy( item.info );
        if( !entity.properties ) entity.properties = [:];
        entity.properties.each { k,v->
            propList << [ key:k, value: v ];
        }
    }
    
    def propListModel = [
        fetchList: { o->
            return propList;
        }
    ] as EditorListModel;
    
    def doOk() {
        item.caption = entity.action;
        item.info.clear();
        item.info.putAll( entity );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}

