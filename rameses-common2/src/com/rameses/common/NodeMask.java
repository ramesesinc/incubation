/*
 * NodeMask.java
 *
 * Created on August 1, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.common;

/**
 *
 * @author Elmo
 */
public interface NodeMask {
    
    final static int ROOT = 1;
    final static int CHILD = 2;
    final static int LEAF = 4;
    final static int DYNAMIC = 8;
    final static int ALLOW_CHILDREN = 16;
    
}
