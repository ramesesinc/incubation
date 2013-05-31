/*
 * AbstractServlet.java
 *
 * Created on January 17, 2013, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.common;

import com.rameses.util.SealedMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public abstract class AbstractServlet extends HttpServlet {
    
    public abstract String getMapping();
    
    protected Object filterOutput( Object obj ) throws Exception {
        return obj;
    }
    
    protected Object[] filterInput( Object obj ) throws Exception {
        return  (Object[])obj;
    }
    
    //this is overridable
    protected Object[] readRequest( HttpServletRequest req ) throws IOException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(req.getInputStream());
            Object obj = in.readObject();
            if(obj instanceof SealedMessage) {
                obj = ((SealedMessage)obj).getMessage();
            }
            Object[] o = (Object[])obj;
            return  filterInput(obj);
        } catch(Exception e) {
            throw new IOException(e);
        } finally {
            try {in.close();} catch(Exception ign){;}
        }
    }
    
    protected void writeResponse( Object response, HttpServletResponse res ) {
        //prepare to write the response
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(res.getOutputStream());
            out.writeObject(filterOutput(response));
        } catch (Exception ex) {
            try {
                out = new ObjectOutputStream(res.getOutputStream());
                out.writeObject(filterOutput(ex));
            } catch(Exception ign){
                System.out.println("error in filtering output");
            }
        } finally {
            try { out.close(); } catch (Exception ex) {;}
        }
    }
    
    
}