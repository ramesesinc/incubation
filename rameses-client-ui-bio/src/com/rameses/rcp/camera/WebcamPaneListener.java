/*
 * WebcamPaneListener.java
 *
 * Created on December 5, 2013, 9:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

/**
 *
 * @author wflores
 */
interface WebcamPaneListener 
{
    void onselect(byte[] bytes); 
    void oncancel();
}
