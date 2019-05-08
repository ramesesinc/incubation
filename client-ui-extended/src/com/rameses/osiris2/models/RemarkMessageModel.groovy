package com.rameses.osiris2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;   

public class RemarkMessageModel  {

    def mode;
    def label;
    def message;
    def handler; 
    
    void create() {
        mode = 'create'; 
    }

    void open() {
        mode = 'read'; 
    }

    def doOk() {
        if (handler) { 
            handler( message ); 
        }
        return "_close";
    }

    def doCancel() {
        return "_close";
    }
}