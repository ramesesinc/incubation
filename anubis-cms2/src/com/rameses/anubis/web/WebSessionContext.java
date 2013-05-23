/*
 * WebSessionContext.java
 *
 * Created on July 6, 2012, 4:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.SessionContext;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class WebSessionContext extends SessionContext {
    
    private static String SESSIONID = "SESSIONID";
    /*
    private static String GET_SESSION = "session/getSession";
    private static String HAS_PERMISSION = "session/checkPermission";
    */
     private String sessionid;
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    WebSessionContext(HttpServletRequest req, HttpServletResponse res) {
        this.request = req;
        this.response = res;
        
        //automatically retreieve the session;
        Cookie cookie = CookieUtil.getCookie(SESSIONID,request);
        if(cookie!=null) {
            this.sessionid = cookie.getValue();
        }
    }
    
    
    /**
     * Gets the sessionid from the cookie. If the cookie's sessionid does not exist,
     * try to check if you can get the session. if you cant get the session,
     * then destroy the cookie.
     */
    public String getSessionid() {
        return this.sessionid;
    }
    
    public String createSession(String sid) {
        //do not create if there is already a sessionid
        this.sessionid = sid;
        if(this.sessionid == null ) {
            this.sessionid = "SESS" + (new java.rmi.server.UID());
        }
        CookieUtil.addCookie( SESSIONID, sessionid, response );
        return this.sessionid;
    }
    
    public String destroySession() {
        if(sessionid!=null) {
            CookieUtil.removeCookie(SESSIONID,request,response);
        }
        return sessionid;
    }
    
    
    public boolean checkPermission(String domain, String role, String permission) {
        Map map = new HashMap();
        map.put("domain", domain );
        map.put("role", role );
        map.put("permission", permission );
        //return (Boolean) execute(HAS_PERMISSION, map);
        return true;
    }
    
    /*
    private Object execute(  String action, Map params ) {
        try {
            ActionManager actionManager = AnubisContext.getCurrentContext().getProject().getActionManager();
            return actionManager.getActionCommand(action).execute( params );
        } catch(Exception e) {
            System.out.println("error executing action->"+action+": " +e.getMessage());
            throw new RuntimeException(e);
        }
    }
     */
   

}
