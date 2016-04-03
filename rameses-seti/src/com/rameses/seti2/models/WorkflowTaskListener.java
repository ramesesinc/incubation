/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.models;

/**
 *
 * @author dell
 */
public interface WorkflowTaskListener {
    //for overriding. If you need to do something before signalling. 
    //the return value will be stored in the info
    Object getInfoBeforeSignal( Object transition  );
}
