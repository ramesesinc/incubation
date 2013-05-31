/*
 * ScriptInfoServlet.java
 *
 * Created on July 7, 2012, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.AnubisContext;
import com.rameses.anubis.ConnectionContext;
import com.rameses.anubis.JsonUtil;
import com.rameses.anubis.Module;
import com.rameses.anubis.Project;
import com.rameses.util.ExceptionManager;
import groovy.lang.GroovyObject;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class JsInvokeServlet extends AbstractAnubisServlet {
    
    protected void handle(HttpServletRequest hreq, HttpServletResponse hres) throws Exception {
        AnubisContext actx = AnubisContext.getCurrentContext();
        actx.setConnectionContext( new ConnectionContext("js-invoke") );
        try {
            String pathInfo = hreq.getPathInfo();
            
            Project project = actx.getProject();
            
            String[] arr = pathInfo.substring(1).split("/");
            String connection = arr[1];
            String service = arr[2];
            service = service.substring(0, service.indexOf("."));
            String action = pathInfo.substring(pathInfo.lastIndexOf(".")+1);
            
            Module module = actx.getModule();
            if(module!=null) {
                actx.getConnectionContext().setModule( module );
            }
            
            //get the arguments
            String _args = hreq.getParameter("args");
            Object[] args = null;
            if (_args!=null && _args.length()>0) {
                if (!_args.startsWith("["))
                    throw new RuntimeException("args must be enclosed with []");
                
                args = JsonUtil.toObjectArray( _args );
            }
            
            GroovyObject gobj =(GroovyObject) project.getServiceManager().lookup( service, connection );
            if (args == null) args = new Object[]{};
            Object result = gobj.invokeMethod( action, args  );
            writeResponse( JsonUtil.toString(result), hres );
        } catch(Exception e) {
            e.printStackTrace();
            e = ExceptionManager.getOriginal(e);
            hres.setStatus(hres.SC_INTERNAL_SERVER_ERROR);
            writeResponse(e.getMessage(), hres);
        } finally {
            actx.removeConnectionContext();
        }
    }
    
    private void writeResponse(String result, HttpServletResponse hres) throws Exception {
        Writer w = null;
        try {
            w = hres.getWriter();
            w.write( result );
        } catch(Exception e) {
            throw e;
        } finally {
            try { w.close(); } catch(Exception e){;}
        }
    }
    
}