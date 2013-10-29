/*
 * UpdateCenter.java
 *
 * Created on October 24, 2013, 10:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author compaq
 */
public class UpdateCenter 
{
    private String appPath = System.getProperty("user.dir") + "/osiris2";
    private String appurl; 
    private URL[] urls;
    private Map env; 
    
    public UpdateCenter(String appurl) {
        this.appurl = appurl;
    }

    public String getAppPath() { return appPath; }    
    public void setAppPath(String appPath) { this.appPath = appPath; }
    
    public Map getEnv() { return env; }    
    
    public ClassLoader getClassLoader(ClassLoader sourceClassLoader) {
        if (urls == null) {
            return sourceClassLoader;
        } else {
            return new URLClassLoader(urls, sourceClassLoader);
        } 
    }    
        
    public void start() throws Exception 
    {
        System.out.println("starting update");
        String hostPath = UpdateCenterUtil.buildHostPath( appurl );

        UpdateConf conf = UpdateCenterUtil.getUpdateConf(appurl, appPath );
        
        //compare the modules with old list, whatever is remaining should be deleted
        List<String> oldList = UpdateCenterUtil.getExistingFiles( conf.getModulePath() );
        List<ModuleEntry> forDownload = new ArrayList();
        for(ModuleEntry me: conf.getModules() ) {
            String fileName = me.getFilename();
            boolean existing = oldList.remove(fileName);
            if(!existing) forDownload.add( me ); 
        }
        
        //delete all files that are not used anymore.
        for( String s : oldList ) {
            File ff = new File( conf.getModulePath() + s );
            ff.delete();
        }
        
        //download all new modules
        for(ModuleEntry me : forDownload) {
            System.out.println("... updating " + me.getFilename() );
            UpdateCenterUtil.download( hostPath, me );
        }
        
        env = conf.getEnv();
        urls = new URL[conf.getModules().size()];
        int i = 0;
        for(ModuleEntry me: conf.getModules()) {
            urls[i++] = me.getURL();
        }
    }    
}
