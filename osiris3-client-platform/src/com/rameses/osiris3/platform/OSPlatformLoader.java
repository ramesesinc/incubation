/*
 * OSPlatformLoader.java
 *
 * Created on October 24, 2013, 10:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.AppLoader;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author wflores
 */
class OSPlatformLoader 
{
    
    public OSPlatformLoader() {
    }
    
    public static OSPlatformLoader.DownloadResult downloadUpdates() throws Exception {
        String filename = System.getProperty("user.dir") + "/client.conf";
        File file = new File(filename);
        if (!file.exists()) throw new Exception("client.conf does not exist");
        
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        
        String appsys = (String) props.get("app.url");
        if (appsys == null || appsys.trim().length() == 0)
            throw new NullPointerException("app.url must be provided");
        
        UpdateCenter updateCenter = new UpdateCenter(appsys);
        updateCenter.start();
        
        Map env = new HashMap(props);
        env.putAll(updateCenter.getEnv());
                
        String loaderName = (String) env.get("app.loader");
        if (loaderName == null || loaderName.trim().length() == 0)
            throw new NullPointerException("app.loader must be provided in the ENV");

        ClassLoader classLoader = updateCenter.getClassLoader(OSManager.getOriginalClassLoader());         
        AppLoader appLoader = (AppLoader) classLoader.loadClass(loaderName).newInstance(); 
        return new DownloadResult(classLoader, appLoader, env);
    } 
    
    public static class DownloadResult 
    {
        private ClassLoader classLoader;
        private AppLoader appLoader;
        private Map env;
        
        DownloadResult(ClassLoader classLoader, AppLoader appLoader, Map env) {
            this.classLoader = classLoader;
            this.appLoader = appLoader;
            this.env = env; 
        }

        public ClassLoader getClassLoader() { 
            return classLoader; 
        } 
        
        public AppLoader getAppLoader() { 
            return appLoader; 
        } 
        
        public Map getEnv() { 
            return env; 
        } 
    }    
}
