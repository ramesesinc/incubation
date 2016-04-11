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
    //like showing the message box or any handlers. 
    //if true, then the signal will proceed. 
    boolean beforeSignal( Object transition  );
}
