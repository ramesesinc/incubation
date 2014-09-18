/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import java.lang.annotation.Annotation;

/**
 *
 * @author wflores
 */
public abstract class XConnectionFactory extends XConnection {

    public abstract XConnection getConnection(Annotation anno);
    
}
