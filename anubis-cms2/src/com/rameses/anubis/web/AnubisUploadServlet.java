/*
 * AnubisUploadServlet.java
 *
 * Created on March 24, 2013, 8:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.ActionCommand;
import com.rameses.anubis.AnubisContext;
import com.rameses.anubis.Project;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.ProgressListener;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 */
public class AnubisUploadServlet extends AbstractAnubisServlet {
    
    private final static ExecutorService thread = Executors.newCachedThreadPool();
    
    protected void handle(HttpServletRequest hreq, HttpServletResponse hres) throws Exception {
        
        
        MultipartRequest mreq = (MultipartRequest)hreq;
        
        Uploader uploader = (Uploader)mreq.getAttribute( "_upload_" );
        if(uploader==null) {
            Continuation c = ContinuationSupport.getContinuation( mreq );
            c.suspend();
            Uploader uploder = new Uploader(mreq, c);
            hreq.setAttribute( "_upload_", uploader );
            thread.submit( uploader );
        } else {
            
            
        }
        
        
        
        //mreq.process(  )
    }
    
    private class Uploader implements Runnable  {
        
        private MultipartRequest req;
        private Continuation continuation;
        
        public Uploader(MultipartRequest req, Continuation continuation) {
            this.req = req;
            this.continuation = continuation;
        }
        
        public void run() {
            try {
                WebAnubisContext ctx = (WebAnubisContext) AnubisContext.getCurrentContext();
                Project project = ctx.getProject();
                String path = req.getPathInfo();
                final ActionCommand cmd = project.getActionManager().getActionCommand( path );
                if(cmd!=null) {
                    ProgressListener listener = new ProgressListener() {
                        public void update(long l, long l0, int i) {
                            Map map = new HashMap();
                            try {
                                cmd.execute( map );
                            } catch(Exception e){;}
                        }
                    };
                    req.setListener( listener );
                }
                
                ActionCommand action = project.getActionManager().getActionCommand( path );
                ctx.setRequest( req );
                action.execute( new HashMap() );
            } catch(Exception e) {
                e.printStackTrace();
                
            } finally {
                continuation.resume();
            }
        }
        
    }
    
}
