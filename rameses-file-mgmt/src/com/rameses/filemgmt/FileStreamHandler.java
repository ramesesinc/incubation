/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import java.util.Map;

/**
 *
 * @author wflores 
 */
public interface FileStreamHandler { 
    
    void ontransfer( Map data, long bytesTransferred ); 
    void oncomplete( Map data ); 
}
