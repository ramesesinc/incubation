/*
 * SessionContext.java
 *
 * Created on January 22, 2014, 10:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import com.rameses.client.interfaces.UserProfile;
import com.rameses.client.interfaces.UserSetting;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class SessionContext 
{
    private static Object LOCK = new Object();
    private static SessionContext currentSession;
    
    static SessionContext getCurrent() { return currentSession; } 
    static void setCurrent(SessionContext newSession) { 
        currentSession = newSession;  
    } 
    
    public static String getSessionId() {
        SessionContext sc = getCurrent();
        Map headers = (sc == null? null: sc.getHeaders()); 
        return sc.getString(headers, "SESSIONID");
    }
    
    public static UserProfile getProfile() {
        SessionContext sc = getCurrent();
        return (sc == null? null: sc.profileImpl); 
    }
    
    public static UserSetting getSettings() {
        SessionContext sc = getCurrent();
        return (sc == null? null: sc.userSettingImpl); 
    }
    
    public static Object getProperty(String name) {
        SessionContext sc = getCurrent();
        return (sc == null? null: sc.properties.get(name)); 
    }
    
    private UserProfileImpl profileImpl;    
    private UserSettingImpl userSettingImpl;
    private AppContext appContext;
    private Map properties;
    private Map env;
    private Map headers; 
    private Map settings;
    
    SessionContext(AppContext appContext) { 
        this.appContext = appContext; 

        userSettingImpl = new UserSettingImpl();        
        profileImpl = new UserProfileImpl();
        env = new EnvMap(appContext.getEnv()); 
        properties = new HashMap();
        setHeaders(new HashMap()); 
        setSettings(new HashMap()); 
    }
    
    public Map getEnv() { return env; } 
    public Map getProperties() { return properties; } 
    
    public final Map getHeaders() { return headers; } 
    public void setHeaders(Map headers) {
        this.headers = (headers == null? new HashMap(): headers);
        this.headers.put("CLIENTTYPE", "mobile"); 
    } 
    
    public void setSettings(Map settings) {
        this.settings = (settings == null? new HashMap(): settings); 
    }
    
    void close() {
        if (properties != null) properties.clear();
        if (headers != null) headers.clear();
        if (env != null) env.clear();
    }
    
    private String getString(Map map, String name) {
        Object value = (map == null? null: map.get(name));
        return (value == null? null: value.toString()); 
    }
    
    private class UserProfileImpl implements UserProfile 
    {
        SessionContext root = SessionContext.this; 

        public String getUserId() {
            Map headers = root.getHeaders();
            return root.getString(headers, "USERID");
        }
        
        public String getUserName() {
            Map headers = root.getHeaders();
            return root.getString(headers, "USER");
        }

        public String getFullName() {
            Map headers = root.getHeaders();
            return root.getString(headers, "FULLNAME");
        }

        public String getName() {
            Map headers = root.getHeaders();
            return root.getString(headers, "NAME");            
        }

        public String getJobTitle() {
            Map headers = root.getHeaders();
            return root.getString(headers, "JOBTITLE");
        }

        public String getPassword() {
            Object value = root.getProperties().get("encpwd");
            return (value == null? null: value.toString()); 
        }
        
        public void set(String name, Object value) {
            root.getProperties().put(name, value); 
        }

        public Object get(String name) {
            return root.getProperties().get(name); 
        }
    }
    
    private class UserSettingImpl implements UserSetting
    {
        SessionContext root = SessionContext.this; 
        
        public String getOnlineHost() {
            return getString(root.settings, "ONLINE_HOST");
        }

        public String getOfflineHost() {
            return getString(root.settings, "OFFLINE_HOST");            
        }

        public int getPort() {
            return getInt(root.settings, "PORT");
        }

        public int getSessionTimeout() {
            return getInt(root.settings, "SESSION_TIMEOUT");
        }

        public int getTrackerDelay() {
            return getInt(root.settings, "TRACKER_DELAY");
        }

        public int getUploadDelay() {
            return getInt(root.settings, "UPLOAD_DELAY");
        }
        
        private String getString(Map map, String name) {
            Object value = (map == null? null: map.get(name));
            return (value == null? null: value.toString()); 
        }
        
        private int getInt(Map map, String name) {
            try {
                return Integer.parseInt(map.get(name).toString()); 
            } catch(Throwable t) {
                return -1;
            }
        } 
    }
}
