/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.ftp;

/**
 *
 * @author rameses1
 */
public final class FtpManager { 
    
    public static FtpSession createSession( String configName ) {
        FtpLocationConf conf = FtpLocationConf.get( configName ); 
        FtpSession sess = new FtpSession( conf );  
        sess.connect(); 
        return sess; 
    }
}
