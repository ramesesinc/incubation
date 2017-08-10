package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;
        

/**
* workunit properties
* cols = choose only columns from schema, in the order displayed separated by commas.
* allowCreate = if true create button will be displayed. default is true
* allowOpen = if true open button will be displayed. default is true
* allowDelete = if true delete button will be displayed. default is false
* 
* init action must be called.
*/
public class CrudTreeListModel extends CrudListModel {
     
    def treeNode = [
        fetchNodes: { o->
            println "fetch nodes " + o.item;
            if(o.item?.name == 'folder1') {
                return [
                    [name:'folder11', caption:'Sub Folder 1', title:'Folder 111'],
                ]
            }
            return [
                [name:'folder1', caption:'Folder1', title:'Folder 111'],
                [name:'folder2', caption:'Folder2', title:'Folder 113']
            ];        
        },
        isRootVisible : { return false; },
        openFolder : { node->
            println "opening node " + node;
        }
    ] as TreeNodeModel;
    
}
