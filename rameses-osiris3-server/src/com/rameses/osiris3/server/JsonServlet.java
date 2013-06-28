/*
 * JsonServlet.java
 *
 * Created on January 10, 2013, 6:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class JsonServlet extends ServiceInvokerServlet {
    
    
    /***
     * check first if there's an args parameters. if yes parse to json
     * if there's an env, parse json
     * else
     * create a map and place all parameters.assume the service accepts only a map entry
     */
    public String getMapping() {
        return "/json/*";
    }
    
    
    protected Object[] readRequest( HttpServletRequest req ) throws IOException{
        Object[] args = null;
        String _args = req.getParameter("args");
        if(_args!=null && _args.trim().length()>0) {
            if(!_args.startsWith("["))
                throw new RuntimeException("args must be enclosed with []");
            args = JsonUtil.toObjectArray( _args );
            
        } else {
            Map map = new HashMap();
            Enumeration<String> en = req.getParameterNames();
            while(en.hasMoreElements()) {
                String s = en.nextElement();
                if(!s.equals("env")) {
                    map.put( s, JsonUtil.toObject(req.getParameter(s)) );
                }
            }
            args = new Object[]{map};
        }
        
        Map env = new HashMap();
        String _env = req.getParameter( "env" );
        if(_env!=null && _env.trim().length()>0) {
            if(!_env.startsWith("{"))
                throw new RuntimeException("env must be enclosed with []");
            env = JsonUtil.toMap( _env );
        }
        return new Object[]{ args, env };
    }
    
    protected void writeResponse( Object response, HttpServletResponse res ) {
        res.setContentType("application/json");
        try {
            res.getWriter().println( JsonUtil.toString(response) );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
