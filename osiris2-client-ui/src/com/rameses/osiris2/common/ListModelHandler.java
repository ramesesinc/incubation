/*
 * ListControllerHandler.java
 *
 * Created on April 28, 2013, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

interface ListModelHandler 
{
    Object getSelectedEntity();
    
    void handleInsert(Object data);
    
    void handleUpdate(Object data); 
    
    void handleRemove(Object data);
    
    void moveFirstPage();
    
    void moveBackPage();
    
    void moveNextPage();
    
    void moveLastPage();
    
    void moveBackRecord();
    
    void moveNextRecord();    
}
