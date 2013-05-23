/*
 * ExecutionInfo.java
 *
 * Created on January 28, 2013, 9:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

/**
 *
 * @author Elmo
 */
public class ExecutionInfo {
    
    private Object result;
    private Object[] args;
    private String serviceName;
    private String methodName;
    
    /** Creates a new instance of ExecutionInfo */
    public ExecutionInfo(String serviceName, String methodName, Object[] args ) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }
    
    public String toString() {
        return this.serviceName+"."+this.getMethodName();
    }
}
