/*
 * DBReportModel.java
 *
 * Created on September 13, 2013, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.osiris2.AppContext;
import com.rameses.service.jdbc.DBServiceDriver;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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
        return (String) AppContext.getInstance().getEnv().get("app.context");
    }
    public String getCluster() {
        return (String) AppContext.getInstance().getEnv().get("app.cluster");
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
                Map params = getParameters();
                if (params != null) conf.putAll(params);

                String appUrl = AppContext.getInstance().getEnv().get("app.host")+"";
                String appContext = root.getContext(); 
                String cluster = root.getCluster();
                String surl = "jdbc:rameses://"+ appUrl +"/"+ cluster + "/" + appContext;
                conn = DriverManager.getConnection( surl );

                String parentName  =  "";
                String rptName = getReportName();
                if(rptName.indexOf("/")>0) {
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
        
    }
}
