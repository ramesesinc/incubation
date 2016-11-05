package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
import com.rameses.seti2.models.*;
import java.rmi.server.UID;
import com.rameses.rulemgmt.developer.*;
import javax.swing.*;
import com.rameses.io.*;

class RulesetListModel extends CrudListModel {
    
    @Service("RuleMigrationService")
    def service;

    void exportDef() {
        if(!selectedItem) throw new Exception("Please select an item");
        def chooser = new JFileChooser();
        int i = chooser.showSaveDialog(null);
        if(i==0) {
            def m = service.downloadDefByRuleset( [ruleset:selectedItem.name] );
            FileUtil.writeObject( chooser.selectedFile, m );
            MsgBox.alert("downloaded!");
        }
    }
    
    void importDef() {
        def chooser = new JFileChooser();
        int i = chooser.showOpenDialog(null);
        if(i==0) {
            def m = FileUtil.readObject( chooser.selectedFile );
            service.uploadDef( m );
            MsgBox.alert("completed!");
        }
    }
    
}