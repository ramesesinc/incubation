package com.rameses.anubis.web;

import com.rameses.anubis.AnubisContext;
import com.rameses.anubis.File;
import com.rameses.anubis.PermalinkManager;
import com.rameses.anubis.Project;
import com.rameses.anubis.SessionContext;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnubisMainServlet extends AbstractAnubisServlet {
    
    protected final void handle(HttpServletRequest hreq, HttpServletResponse hres) throws Exception {
        
        AnubisContext actx = AnubisContext.getCurrentContext();
        
        String fullPath = hreq.getPathInfo();
        ServletContext app = config.getServletContext();
        
        //run the CMS file
        Project project = actx.getProject();
        Map params = RequestUtil.buildRequestParams( hreq );
        actx.setParams( params );
        
        //fire all actions first if any.
        project.getActionManager().fireActions( fullPath, params);
        
        String mimeType = app.getMimeType( fullPath );
        String ext = CmsWebConstants.PAGE_FILE_EXT;
        if ( mimeType == null ) {
            mimeType = "text/html";
        } else {
            //if mimetype is not null then automatically we consider it as media.
            ext = CmsWebConstants.MEDIA_FILE_EXT;
        }
        
        if (fullPath.equals("/")) {
            if(project.getWelcomePage()!=null) {
                hres.sendRedirect( project.getWelcomePage() );
            }
            else {
                File ff = project.getFolderManager().findFirstVisibleFile("/");
                if (ff != null) fullPath = ff.getPath();
                hres.sendRedirect(fullPath);
            }
            return;
            
        } else {
            if(fullPath.endsWith("/")) fullPath = fullPath.substring(0, fullPath.length()-1);
        }
        
        String filename = fullPath + ext;
        File file = null;
        SessionContext ctx = actx.getSession();
        
        //FIND the associated file in permalink. Bypass if its a fragment request. We assume
        //it is a fragment if requested via ajax. Requests thru ajax headers are marked
        //with : X-Requested-With=XMLHttpRequest
        
        boolean ajaxRequest = false;
        String requestWith = hreq.getHeader("X-Requested-With");
        if( requestWith !=null && requestWith.equalsIgnoreCase("XMLHttpRequest") ) {
            ajaxRequest = true;
            actx.setAttribute( "ajaxRequest", true );
        }
        
        if(!ajaxRequest) {
            try {
                //check if filename matches permalinks. if secured,
                //use secured permalinks instead
                PermalinkManager permalink = project.getPermalinkManager();
                String resolvedName = permalink.resolveName( fullPath, params  );
                if(resolvedName!=null) filename = resolvedName;
                file = project.getFileManager().getFile( filename );
            } catch(com.rameses.anubis.FileNotFoundException fe) {
                hres.setStatus(HttpServletResponse.SC_NOT_FOUND);
                file = project.getFileManager().getFile( "/404.pg" );
                InputStream is = project.getContentManager().getContent( file, params );
                ResponseUtil.write( hreq, hres, mimeType, is);
            } catch(Exception e) {
                throw e;
            }
        } else {
            file = project.getFileManager().getFile( filename );
        }
        
        if (file.getHref() != null) {
            hres.sendRedirect( file.getHref() );
            return;
        }
        
        boolean secured = false;
        if(file.isSecured()) {
            secured = true;
        }
        
        //set authenicated as true if there is sessionid
        boolean allow_access = true;
        if (secured && ctx.getSessionid()==null) allow_access = false;
        if (!allow_access) {
            String path = CmsWebConstants.LOGIN_PAGE_PATH;
            String requestPath = hreq.getRequestURI();
            String qry = hreq.getQueryString();
            if (qry!=null && qry.trim().length()>0){
                requestPath += "?"+ qry;
            }
            
            if (ajaxRequest) {
                hres.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
                //hres.sendRedirect(path  + "?target=" + URLEncoder.encode(requestPath));
                //ResponseUtil.write( hreq, hres, mimeType, is);
                //hres.sendError(  )
                String referer = hreq.getHeader("Referer");
                //remove the server part
                referer =referer + "#"+ file.getHashid();
                String redirect = path  + "?target=" + URLEncoder.encode(referer);
                ResponseUtil.write( hreq, hres, mimeType, redirect);
            } else {
                //file = project.getFileManager().getFile( "/reload.pg" );
                //InputStream is = project.getContentManager().getContent(file, params);
                //ResponseUtil.write( hreq, hres, mimeType, is);
                hres.sendRedirect( path + "?target=" + URLEncoder.encode(requestPath) );
            }
        } else {
            boolean authorized = true;
            String domain = file.getDomain();
            String role = file.getRole();
            String permission = file.getPermission();
            
            if (role!=null || permission!=null) {
                authorized = ctx.checkPermission(domain, role, permission);
            }
            
            //if no permission, redirect to a non-authorized page
            if ( !authorized ) {
                hres.setStatus(HttpServletResponse.SC_FORBIDDEN);
                file = project.getFileManager().getFile( "/403.pg" );
                InputStream is = project.getContentManager().getContent(file, params);
                ResponseUtil.write(hreq, hres, mimeType, is);
                return;
            }
            
            hres.setStatus(HttpServletResponse.SC_OK);
            InputStream is = project.getContentManager().getContent(file,params);
            ResponseUtil.write( hreq, hres, mimeType, is);
        }
    }
}

