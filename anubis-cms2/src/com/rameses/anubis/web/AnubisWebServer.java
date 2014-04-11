package com.rameses.anubis.web;

import com.rameses.server.BootLoader;
import com.rameses.server.ServerLoader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Properties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class AnubisWebServer implements ServerLoader  {
    
    public final static String 
            ANUBIS_HOME = "anubis.home",
            ANUBIS_HOSTS = "anubis.hosts", 
            WEB_CONF_DIR = "web.conf.dir",
            WEB_LIB_DIR  = "web.lib.dir",
            WEB_APPS_DIR = "web.apps.dir",
            KEY_LIBRARY_PATH = "web.lib.path",
            KEY_RESOURCE_PATH = "web.res.path",
            KEY_WEB_APPS_PATH = "web.apps.path";
    
    
    private String name;
    
    public AnubisWebServer(String name) {
        this.name = name;
    }
    
    public static void main(String[] args) throws Exception {
        BootLoader b = new BootLoader();
        b.start();
    }
    
    private Server svr;
    private Map conf;
    
    public Map getInfo() {
        return   conf;
    }
    
    public void init(String baseUrl,  Map conf) throws Exception {
        this.conf  = conf;
        System.out.println("**************************************************");
        System.out.println("  STARTING ANUBIS CMS WEB SERVER ");
        System.out.println("**************************************************");
        
        String rundir = getSystemProperty("osiris.run.dir");
        String basedir = getSystemProperty("osiris.base.dir");
        
        //this is where anubis ommon libs and files are located.
        String svrdir = System.getProperty("anubis.server.dir", basedir);
        
        Properties webconfig = new Properties();
        if (conf != null) webconfig.putAll(conf);

        String workspace = webconfig.getProperty("home.url");
        if (workspace == null)
            throw new Exception("Anubis webserver failed to load. home.url must be specified in the server.conf");
        
        System.out.println("rundir: " + rundir);
        System.out.println("basedir: " + basedir);
        System.out.println("svrdir: " + svrdir);
        System.out.println("anubis-workspace: " + workspace);

        System.getProperties().put(ANUBIS_HOME, workspace);
        System.getProperties().put(ANUBIS_HOSTS, workspace + "/anubis.hosts");
        System.getProperties().put(KEY_LIBRARY_PATH, basedir + "/lib/anubis.lib");
        System.getProperties().put(WEB_CONF_DIR, rundir);
        System.getProperties().put(WEB_LIB_DIR, svrdir);
        
        HandlerList handlerlist = new HandlerList();
        //buildWebApps(handlerlist);
        
        ServletContextHandler mainctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        mainctx.setContextPath("/");
        mainctx.setResourceBase(System.getProperty(KEY_LIBRARY_PATH));
        mainctx.addServlet(createServletHolder(AnubisStartupServlet.class, "cms-startup", 1), null);
        mainctx.addServlet(AnubisResourceServlet.class, "/res/*");        
        mainctx.addServlet(AnubisThemeServlet.class, "/themes/*");
        mainctx.addServlet(JsProxyServlet.class, "/js-proxy/*");
        mainctx.addServlet(JsInvokeServlet.class, "/js-invoke/*");
        mainctx.addServlet(AnubisActionServlet.class, "/actions/*");
        mainctx.addServlet(AnubisMediaServlet.class, "/media/*");
        mainctx.addServlet(AnubisUploadServlet.class, "/upload/*");
        mainctx.addServlet(BasicResourceServlet.class, "/js/*");
        mainctx.addServlet(BasicResourceServlet.class, "/images/*");
        mainctx.addServlet(AnubisPollServlet.class, "/poll/*");
        mainctx.addServlet(BasicResourceServlet.class, "/favicon.ico");
        mainctx.addServlet(AnubisMainServlet.class, "/*");
        
        //extensions
        
        
        handlerlist.addHandler(mainctx);
        
        SelectChannelConnector conn = new SelectChannelConnector();
        conn.setName(name);
        conn.setPort(Integer.parseInt(webconfig.getProperty("port", "18080")));
        
        String pval = webconfig.getProperty("low-resources-connections", null);
        if (pval != null) conn.setLowResourcesConnections(Integer.parseInt(pval));
        
        pval = webconfig.getProperty("low-resources-max-idle-time", null);
        if (pval != null) conn.setLowResourcesMaxIdleTime(Integer.parseInt(pval));
        
        pval = webconfig.getProperty("max-buffers", null);
        if (pval != null) conn.setMaxBuffers(Integer.parseInt(pval));
        
        conn.setMaxIdleTime(Integer.parseInt(webconfig.getProperty("max-idle-time", "30000")));
        conn.setRequestHeaderSize(Integer.parseInt(webconfig.getProperty("request-header-size", "8192")));
        
        pval = webconfig.getProperty("request-buffer-size", null);
        if (pval != null) conn.setRequestBufferSize(Integer.parseInt(pval));
        
        pval = webconfig.getProperty("response-buffer-size", null);
        if (pval != null) conn.setResponseBufferSize(Integer.parseInt(pval));
        
        pval = webconfig.getProperty("response-header-size", null);
        if (pval != null) conn.setResponseHeaderSize(Integer.parseInt(pval));
        
        conn.setThreadPool(new QueuedThreadPool(Integer.parseInt(webconfig.getProperty("thread-pool-size", "250"))));
        
        ResourceHandler static_resource_handler = new ResourceHandler();
        static_resource_handler.setResourceBase(System.getProperty(KEY_LIBRARY_PATH));
        static_resource_handler.setDirectoriesListed(false);
        handlerlist.addHandler(static_resource_handler);
        
        svr = new Server();
        svr.addConnector(conn);
        svr.setHandler(handlerlist);
    }
    
    
    public void start() throws Exception {
        try {
            svr.start();
        } catch(Exception ex) {
            System.out.println("Failed to start server!");
            return;
        }
        
        try {
            svr.join();
        } catch(Exception ex) {
            throw ex;
        } finally {
            try { svr.stop(); }catch(Exception ign){;}
        }
    }
    
    public void stop() throws Exception {
        System.out.println("Shutting down server...");
        try { svr.stop(); }catch(Exception ign){;}
    }
    
    private void buildWebApps(HandlerList handlerList) throws Exception {
        File dir = new File(System.getProperty(KEY_WEB_APPS_PATH));
        if (!dir.exists() || !dir.isDirectory()) return;
        
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".war");
            }
        });
        
        String webappdir = System.getProperty(KEY_WEB_APPS_PATH);
        for (File file : files) {
            String fname = file.getName();
            fname = fname.substring(0, fname.lastIndexOf(".war"));
            if (fname.trim().length() == 0) continue;
            
            WebAppContext wac = new WebAppContext();
            wac.setContextPath("/" + fname);
            wac.setWar(webappdir + "/" + file.getName());
            wac.setCopyWebInf(true);
            wac.setCopyWebDir(true);
            handlerList.addHandler(wac);
        }
    }
    
    private ServletHolder createServletHolder(Class servlet, String name, int startupOrder) throws Exception {
        ServletHolder holder = new ServletHolder(servlet);
        holder.setName(name);
        if (startupOrder > 0) holder.setInitOrder(startupOrder);
        
        return holder;
    }
    
    String getSystemProperty(String name) {
        String userdir = System.getProperty("user.dir");
        String s = System.getProperty(name);
        if (s == null) System.getProperties().put(name, userdir);
        
        return System.getProperty(name);
    }
    
    
}
