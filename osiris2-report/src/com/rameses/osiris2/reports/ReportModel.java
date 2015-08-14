/*
 * ReportData.java
 *
 * Created on November 25, 2009, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import com.rameses.osiris2.client.Inv;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author elmo
 */
public abstract class ReportModel {
    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;

    private boolean dynamic = false;    
    private boolean allowSave = true;
    private boolean allowPrint = true;
    private boolean allowBack = false;
    
    protected final PropertyResolver propertyResolver = PropertyResolver.getInstance();
    
    public abstract Object getReportData();
    public abstract String getReportName();
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public boolean isAllowSave() { return allowSave; }
    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
    }
    
    public boolean isAllowPrint() { return allowPrint; }
    public void setAllowPrint(boolean allowPrint) {
        this.allowPrint = allowPrint;
    }
        
    public boolean isAllowBack() { return allowBack; } 
    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack; 
    }
    
    public boolean isAllowEdit() { 
        return ReportUtil.isDeveloperMode(); 
    } 
    
    public SubReport[] getSubReports() { return null; }
    
    public Map getParameters() { return null; }
    
    private JasperPrint reportOutput;
    
    private JasperReport mainReport;
    
    private JasperPrint createReport() {
        try {
            if (ReportUtil.isDeveloperMode()) { mainReport = null; }
            
            if (mainReport == null || isDynamic()) { 
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
            
            JRParameter[] jrparams = mainReport.getParameters(); 
            if ( jrparams != null ) {
                for ( JRParameter jrp : jrparams ) {
                    String pname = jrp.getName(); 
                    try { 
                        if ( pname.indexOf('.') <= 0 ) { continue; } 

                        Object pvalue = propertyResolver.getProperty(conf, pname); 
                        conf.put( pname, pvalue ); 
                    } catch(Throwable t) {
                        System.out.println("Error on parameter [" + pname  + "] caused by " + t.getMessage());
                    }
                }
            }
            
            JRDataSource ds = null;
            Object data = getReportData();
            if (data != null) {
                ds = new ReportDataSource(data);
            } else {
                ds = new JREmptyDataSource();
            }
            
            conf.put("REPORT_UTIL", new ReportDataUtil());
            conf.put("REPORTHELPER", new ReportDataSourceHelper());
            
            
            return JasperFillManager.fillReport(mainReport, conf, ds);
        } catch (RuntimeException re) {
            throw re;
        } catch (JRException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
    
    public String viewReport() {
        reportOutput = createReport();
        return "report";
    }
    
    public JasperPrint getReport() {
        return reportOutput;
    }
    
    public List getReportActions() {
        List list = new ArrayList();
        list.add( new Action("_close", "Close", null));
        
        List<Action> xactions = lookupActions("reportActions");
        while (!xactions.isEmpty()) {
            Action a = xactions.remove(0);
            if ( ! containsAction(list, a)){
                list.add(a);
            }
        }
        return list;
    }
    
    private boolean containsAction(List<Action> list, Action a){
        for (Action aa : list){
            if (aa.getName().equals(a.getName()))
                return true;
        }
        return false;
    }
    
    protected final List<Action> lookupActions(String type) {
        List<Action> actions = new ArrayList();
        try {
            actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
                public boolean accept(com.rameses.osiris2.Invoker o) {
                    return o.getWorkunitid().equals(invoker.getWorkunitid());
                }
            });
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup actions caused by " + t.getMessage());
        }
        
        for (int i=0; i<actions.size(); i++) {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        }
        return actions;
    }
    
    //this method is invoked by the back button
    public Object back() { return "_close"; }
    public Object edit() { 
        try { 
            Map params = new HashMap(); 
            params.put("report", this); 
            return Inv.lookupOpener("sysreport:edit", params); 
        } catch(Throwable t) { 
            return null; 
        } 
    } 
    
}
