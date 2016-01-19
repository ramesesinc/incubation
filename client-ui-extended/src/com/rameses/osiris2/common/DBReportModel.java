/*
 * DBReportModel.java
 *
 * Created on September 13, 2013, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.AppContext;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.reports.ReportModel;
import com.rameses.osiris2.reports.ReportUtil;
import com.rameses.osiris2.reports.SubReport;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.jdbc.DBServiceDriver;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseExpression;

/**
 *
 * @author Elmo
 */
public abstract class DBReportModel  {
    
    private Map params = new HashMap();
    private String mode = "init"; 
    private boolean dynamic; 

    private ReportModelImpl report = new ReportModelImpl();
    private FormPanelModelImpl fpModel = new FormPanelModelImpl();
            
    public abstract String getReportName();
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public String getMode() { return mode; }
    public void setMode( String mode ) { 
        this.mode = mode;  
    }
    
    public SubReport[] getSubReports() {
        return null; 
    }
    public Map getQuery() {
        return params; 
    }

    
    public String getContext() {
        return null; 
    }
    public String getCluster() {
        return null; 
    }
    
    public String back() {
        setMode( "init" ); 
        return "default"; 
    }
    
    
    public String viewReport() {
        report.createReport(); 
        return "report";
    }
    
    public String preview() {
        String outcome = viewReport(); 
        setMode( "view" ); 
        return outcome; 
    }
    
    public void print() { 
        print( true ); 
    } 
    
    public void print( boolean withPrintDialog ) { 
        report.print( withPrintDialog ); 
    } 
    
    public Object getReport() {
        return report;
    }       
    
    public List getFormControls() { 
        return null; 
    }  
    
    public class CustomReportClassLoader extends ClassLoader {
        private String parentName;
        public  CustomReportClassLoader(String n){
            parentName = n;
            if(parentName.trim().length()>0) {
                parentName = parentName + "/";
            }
        }
        public URL getResource(String name) {
            return getClass().getClassLoader().getResource( parentName +  name );
        }
    } 
    
    public Object getFormControl(){
        return fpModel;
    }
    
    
    private ClientContext currentContext; 
    private ClientContext getCurrentContext() {
        if (currentContext == null) {
            currentContext = ClientContext.getCurrentContext(); 
        }
        return currentContext; 
    }
    
    
    private ReportParamServiceProxy svcproxy;
    private ReportParamServiceProxy getServiceProxy() {
        if ( svcproxy == null ) {
            svcproxy = (ReportParamServiceProxy) InvokerProxy.getInstance().create("ReportParameterService", ReportParamServiceProxy.class); 
        } 
        return svcproxy; 
    }
    
    public static interface ReportParamServiceProxy {
        Map getStandardParameter();
    }
    
    
    private class FormPanelModelImpl extends com.rameses.rcp.common.FormPanelModel {
        
        DBReportModel root = DBReportModel.this; 

        public List<Map> getControlList() {  
            return root.getFormControls(); 
        } 
    } 
    
    private class ReportModelImpl extends ReportModel { 
                
        DBReportModel root = DBReportModel.this; 
        JasperReport mainReport;
        JasperPrint reportOutput;
        
        public Object getReportData() {
            return null; 
        }
        public String getReportName() {
            return root.getReportName(); 
        }
        public Map getParameters() {
            return root.getQuery(); 
        }
        public SubReport[] getSubReports() {
            return root.getSubReports(); 
        }
        public JasperPrint getReport() {
            return reportOutput;
        } 
        
        JasperPrint createReport() {
            Connection conn = null;
            try {
                Class.forName(DBServiceDriver.class.getName());
                
                if (ReportUtil.isDeveloperMode()) { mainReport = null; }
                
                if (mainReport == null || root.isDynamic()) {
                    mainReport = ReportUtil.getJasperReport( getReportName() );
                }

                Map conf = new HashMap(); 
                SubReport[] subReports = getSubReports(); 
                if (subReports != null) { 
                    for (SubReport sr: subReports) { 
                        conf.put( sr.getName(), sr.getReport() ); 
                    } 
                } 
                
                Map stdparams = root.getServiceProxy().getStandardParameter(); 
                if ( stdparams != null ) {
                    conf.putAll( stdparams ); 
                } 
                
                Map params = getParameters();
                if ( params != null ) { 
                    conf.putAll( params );
                } 

                Map invparams = new HashMap(); 
                invparams.put("params", conf); 
                new ReportParameterLoader().load( invparams );                  
                
                if ( stdparams != null ) { 
                    Iterator keys = stdparams.keySet().iterator(); 
                    while ( keys.hasNext() ) {
                        String skey = keys.next()+""; 
                        Object oval = stdparams.get(skey); 
                        conf.put(skey, oval); 

                        if ( oval == null ) { continue; }

                        String sval = oval.toString().toLowerCase();
                        if (!(sval.endsWith(".jpg") || sval.endsWith(".png"))) { continue; } 
                        if ( sval.startsWith("http://") ) { continue; }

                        conf.put(skey, getInputStream( oval.toString() )); 
                    }                 
                }
                
                Map appenv = ClientContext.getCurrentContext().getAppEnv(); 
                Iterator keys = appenv.keySet().iterator(); 
                while ( keys.hasNext() ) { 
                    Object key = keys.next(); 
                    conf.put("ENV."+ key.toString().toUpperCase(), appenv.get(key)); 
                } 
                
                JRParameter[] jrparams = mainReport.getParameters(); 
                if ( jrparams != null ) {
                    for ( JRParameter jrp : jrparams ) {
                        if (jrp.isSystemDefined()) { continue; } 
                        
                        String pname = jrp.getName();                         
                        try { 
                            if ( pname.indexOf('.') > 0 ) { 
                                Object pvalue = propertyResolver.getProperty(conf, pname); 
                                conf.put( pname, pvalue ); 
                            } 
                        } catch(Throwable t) {
                            System.out.println("Error on parameter [" + pname  + "] caused by " + t.getMessage());
                        }
                        
                        evaluateParameter( jrp, conf ); 
                    } 
                } 
                
                Map env = AppContext.getInstance().getEnv(); 
                String appUrl = env.get("app.host")+"";
                String appContext = root.getContext(); 
                String appCluster = root.getCluster(); 
                if ( appContext==null ) appContext = env.get("app.context")+""; 
                if ( appCluster==null ) appCluster = env.get("app.cluster")+""; 
                
                String surl = "jdbc:rameses://"+ appUrl +"/"+ appCluster + "/" + appContext;
                conn = DriverManager.getConnection( surl );

                String parentName  =  "";
                String rptName = getReportName();
                if ( rptName.indexOf("/") > 0 ) { 
                    parentName = rptName.substring(0, rptName.lastIndexOf("/"));
                } 
                conf.put(JRParameter.REPORT_CLASS_LOADER, new CustomReportClassLoader(parentName));
                reportOutput = JasperFillManager.fillReport(mainReport, conf, conn ); 
                return reportOutput; 
            } catch (RuntimeException re) {
                throw re;
            } catch (JRException ex) {
                ex.printStackTrace();
                throw new IllegalStateException(ex.getMessage(), ex);
            } catch(Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                try {conn.close();} catch(Exception ign){;}
            }
        }
        
        URL getURL( String imagename ) { 
            try { 
                return root.getCurrentContext().getClassLoader().getResource( getImagePath( imagename ) ); 
            } catch( Throwable t ) { 
                return null; 
            } 
        }
        
        InputStream getInputStream( String imagename ) {
            return root.getCurrentContext().getClassLoader().getResourceAsStream( getImagePath( imagename ) );
        }
        
        String getImagePath( String imagename ) { 
            Map appEnv = root.getCurrentContext().getAppEnv(); 
            String customfolder = (String) appEnv.get("report.custom"); 
            if ( customfolder==null ) {
                customfolder = (String) appEnv.get("app.custom");
            }

            String path = "images/" + imagename; 
            if( customfolder != null ) { 
                String cpath = "images/" + customfolder + "/" + imagename; 
                if ( root.getCurrentContext().getResource(cpath) != null ) { 
                    path = cpath; 
                } 
            } 
            return path;  
        }    
        
        void evaluateParameter( JRParameter jrp, Map conf ) { 
            if ( jrp.isSystemDefined() ) { return; } 
            
            Object dve = jrp.getDefaultValueExpression(); 
            if ( dve instanceof JRBaseExpression ) {
                JRBaseExpression jrbe = (JRBaseExpression)dve; 
                if ( jrbe.getValueClass() != null && !jrbe.getValueClass().getName().equals("java.lang.Object") ) { return; } 

                String text = jrbe.getText();
                if ( text == null || text.trim().length() == 0 ) { return; } 
                
                StringBuilder sb = new StringBuilder( text );
                if ( sb.charAt(0) == '"' ) { sb.deleteCharAt(0); }
                if ( sb.length() > 0 && sb.charAt(sb.length()-1) == '"' ) {
                    sb.deleteCharAt( sb.length()-1 );
                } 
                
                try { 
                    String sval = sb.toString().toLowerCase();
                    if ( sval.startsWith("http://") ) { return; }
                    if ( sval.endsWith(".jpg") || sval.endsWith(".png") ) {
                        conf.put( jrp.getName(), getInputStream( text ) );  
                    } 
                } catch(Throwable t) {
                    System.out.println("Error on parameter [" + jrp.getName()  + "] caused by " + t.getMessage());
                } 
            }                        

        }
    } 
}
