package com.rameses.osiris2.reports;

import com.rameses.osiris2.client.OsirisContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public final class ReportUtil {
    
    public ReportUtil() {
    }
    
    public static JasperPrint generateJasper( Object data, Map conf ) throws Exception {
        JasperReport r = (JasperReport)conf.get("main");
        ReportDataSource md = new ReportDataSource(data);
        try {
            return JasperFillManager.fillReport(r,conf,md);
        } catch (JRException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public static InputStream generatePdf( Object data, Map conf ) throws Exception {
        JasperPrint jp = generateJasper(data, conf);
        ReportDataSource md = new ReportDataSource(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, bos);
        return new ByteArrayInputStream( bos.toByteArray() );
    }
    
    public static InputStream generateHtml( Object data, Map conf ) throws Exception {
        JasperPrint jp = generateJasper(data, conf);
        ReportDataSource md = new ReportDataSource(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JRHtmlExporter jhtml = new JRHtmlExporter();
        jhtml.setParameter(JRExporterParameter.JASPER_PRINT, jp );
        jhtml.setParameter(JRExporterParameter.OUTPUT_STREAM, bos );
        jhtml.exportReport();
        return new ByteArrayInputStream( bos.toByteArray() );
    }
    
    public static void view( JasperPrint p ) {
        JasperViewer.viewReport( p );
    }
    
    public static boolean print( JasperPrint jp, boolean withPrintDialog ) throws Exception {
        return JasperPrintManager.printReport(jp, withPrintDialog );
    }
    
    //this gets the jasper report
    public static JasperReport getJasperReport(String name) {
        //check in the env if there is an entry for report.custom
        //if it has, use the report dir as follows:
        //[original report dir]/[report.custom value]/[report filename]
        //otherwise use the original requested report name
        final String preferredName = name;
        String customReportName = null; 
        
        Map env = OsirisContext.getSession().getEnv();
        String testdir = (String) env.get("report.testdir"); 
        String cusDir = (String) env.get("report.custom");
        if (cusDir == null) { cusDir = (String) env.get("app.custom"); } 
        
        if (cusDir != null) {
            String oDir = name.substring(0, name.lastIndexOf("/"));
            String oFname = name.substring(name.lastIndexOf("/"));
            customReportName = oDir + "/" + cusDir + oFname;
            URL u = ReportUtil.class.getClassLoader().getResource(customReportName);
            if (u != null) { name = customReportName; } 
        }
        
        String reportPath = System.getProperty("user.dir") + "/reports/";
        if( name.endsWith(".jrxml")) {
            //check first if the file has already been compiled.
            URLConnection uc = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                //fix the directories
                String srptname = reportPath + name.replaceAll("jrxml", "jasper");
                
                String dirPath = srptname.substring(0, srptname.lastIndexOf("/"));
                File fd = new File(dirPath);
                if (!fd.exists()) { fd.mkdirs(); }
                
                File f = new File(srptname);
                URL u = ReportUtil.class.getClassLoader().getResource(name);
                is = u.openStream();
                uc = u.openConnection();
                long newModified = uc.getLastModified();
                
                if( f.exists() ) {
                    long oldModified = f.lastModified();
                    if( newModified != oldModified ) {
                        f.delete();
                        fos = new FileOutputStream(f);
                        JasperCompileManager.compileReportToStream( is,fos );
                        fos.flush();
                        f.setLastModified(newModified);
                    }
                } else {
                    fos = new FileOutputStream(f);
                    JasperCompileManager.compileReportToStream( is,fos );
                    fos.flush();
                    f.setLastModified(newModified);
                }
                return (JasperReport) JRLoader.loadObject(f);
                
            } catch(Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                try { is.close(); } catch(Exception ign){;}
                try { fos.close(); } catch(Exception ign){;}
            }
            
        } else if( name.endsWith(".jasper") ) {
            try {
                if (testdir != null) {
                    File ofile = new File(testdir + '/' + customReportName); 
                    if (ofile.exists() && !ofile.isDirectory()) {
                        return (JasperReport) JRLoader.loadObject(ofile); 
                    }
                    
                    ofile = new File(testdir + '/' + preferredName); 
                    if (ofile.exists() && !ofile.isDirectory()) { 
                        return (JasperReport) JRLoader.loadObject(ofile); 
                    } 
                } 
                
                URL u = ReportUtil.class.getClassLoader().getResource(name);
                return (JasperReport) JRLoader.loadObject(u); 
            } catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        } else {
            throw new IllegalStateException("Report name " + name + " not recognozed");
        }
    }
    
    public static boolean isTestMode() {
        Object testdir = OsirisContext.getSession().getEnv().get("report.testdir"); 
        return (testdir != null); 
    }
    
    public static boolean isDeveloperMode() {
        Object devmode = OsirisContext.getSession().getEnv().get("app.devmode");
        return ("true".equals( devmode+"" ));
    }
}
