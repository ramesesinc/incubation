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

public class WorkflowInfoModel  {
    
    def mode = 'create';
    def handler;
    def entity;

    void create() {
        mode = "create";
        entity = [:];
    }
    
    void open() {
        mode = 'read';
        def m = [:];
        m.putAll( entity );
        entity = m;
    }
    
    def doOk() {
        handler( entity );
        return "_close";
    }
    
    def doCancel() {
        return  "_close";
    }
    
}

