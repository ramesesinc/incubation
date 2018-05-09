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

public class WorkflowGUIModel  {
    
    @Service("WorkflowGUIService")
    def guiSvc;
    
    
    @FormTitle
    String title;
    
    boolean editing = false;
    def entity;
    def data;
    def processname;
    
    def transitions = [];
    
    def _deletedfigures = [];
    def _deletedconnectors = [];
    
    void create() {
        boolean pass = false;
        def m = [:];
        m.handler = { o->
            entity = guiSvc.create( o );
            pass = true;
        }    
        Modal.show("sys_wf_info:create", m );
        if( !pass ) throw new BreakException();
        editing = true;
        open();
    }
    
    void open() {
        title = entity.title;
        processname = entity.name;
        data = guiSvc.getData( processname );
        if(!data) throw new Exception(processname + " not found ");
    }
    
    void viewInfo() {
        def m = [:];
        m.entity = data.info;
        Modal.show("sys_wf_info:open", m );
    }
    
    void edit() {
        editing = true;
        _deletedfigures.clear();
        _deletedconnectors.clear();
        handler.refresh();
    }
    
    void save() {
        if(!MsgBox.confirm("You are about to save the changes. Proceed?")) return;

        def dd = handler.data;
        dd._deletedfigures = _deletedfigures;
        dd._deletedconnectors = _deletedconnectors;
        dd.processname = processname;
        guiSvc.save( dd );
        data = dd;
        editing = false;
        handler.refresh();
        MsgBox.alert( "Data saved");
    }
    
    def handler = [
        isReadonly: {
           return !editing;
        },
        fetchCategories : {'workflow'},
        fetchData : { data },
        open : {
            if( it instanceof Connector ) {
                Modal.show( 'sys_wf_transition:open', [item:it, editing: editing] );
            }
            else {
                Modal.show( 'sys_wf_node:open', [item:it, editing: editing] );
            }
        },
        showMenu : {
            [Inv.lookupOpener('node:open', [entity:it])]
        },
        afterRemove: { nlist->
            nlist.each { n->
                if( n instanceof Connector) {
                    _deletedconnectors << n.toMap();
                }
                else {
                    _deletedfigures << n.toMap();
                }
            }
        }
    ] as GraphModel;
    
    
}

