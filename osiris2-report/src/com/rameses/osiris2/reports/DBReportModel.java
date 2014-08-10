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
import com.rameses.rcp.common.Action;
import com.rameses.service.jdbc.DBServiceDriver;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
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
public abstract class DBReportModel {
    
    private boolean allowSave = false;
    private boolean allowPrint = true;
    private String mode = "initial";
    
    public abstract String getReportName();
    
    public boolean isAllowSave() { return allowSave; }
    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
    }
    
    public boolean isAllowPrint() { return allowPrint; }
    public void setAllowPrint(boolean allowPrint) {
        this.allowPrint = allowPrint;
    }
    
    public SubReport[] getSubReports() { return null; }
    
    public Map getParameters() { return null; }
    
    private JasperPrint reportOutput;
    
    private JasperReport mainReport;
    
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
    
    
    private JasperPrint createReport() {
        Connection conn = null;
        try {
            Class.forName(DBServiceDriver.class.getName());
            if (mainReport == null) {
                mainReport = ReportUtil.getJasperReport(getReportName());
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
            String appContext = AppContext.getInstance().getEnv().get("app.context")+"";
            String cluster = AppContext.getInstance().getEnv().get("app.cluster")+"";
            String surl = "jdbc:rameses://"+ appUrl +"/"+ cluster + "/" + appContext;
            conn = DriverManager.getConnection( surl );
            
            String parentName  =  "";
            String rptName = getReportName();
            if(rptName.indexOf("/")>0) {
                parentName = rptName.substring(0, rptName.lastIndexOf("/"));
            }
            conf.put(JRParameter.REPORT_CLASS_LOADER, new CustomReportClassLoader(parentName));
            return JasperFillManager.fillReport(mainReport, conf, conn );
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
    
   
    public String viewReport() {
        reportOutput = createReport();
        mode = "report";
        return "report";
    }
    
    public JasperPrint getReport() {
        return reportOutput;
    }
    
    /*
    public List getReportActions() {
        List list = new ArrayList();
        list.add( new Action("_close", "Close", null));
        return list;
    }
     */
    
    //this method is invoked by the back button
    public Object back() {
        mode = "initial";
        return "default";
    }
    public Object close() {
        mode = "initial";
        return "_close";
    }
    
    public String getMode() {
        return mode;
    }
}
