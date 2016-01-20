package com.rameses.osiris2.reports;

import com.rameses.io.IOStream;
import com.rameses.osiris2.client.Inv;
import com.rameses.osiris2.client.OsirisContext;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static JasperReport getJasperReport( String name ) {
        if( name.endsWith(".jrxml")) {
            String reportPath = System.getProperty("user.dir") + "/reports/"; 
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
                
            } catch( RuntimeException re ) { 
                throw re; 
            } catch( Exception e ) { 
                throw new IllegalStateException(e.getMessage(), e); 
            } finally { 
                try { is.close(); } catch(Exception ign){;}
                try { fos.close(); } catch(Exception ign){;}
            }
            
        } else if( name.endsWith(".jasper") ) {
            try {
                URL res = getResource( name ); 
                if ( res == null ) { 
                    throw new Exception("Report name "+ name +" not recognized"); 
                }  
                return (JasperReport) JRLoader.loadObject( res ); 
                
            } catch( RuntimeException re ) { 
                throw re; 
            } catch( Exception ex ) { 
                throw new IllegalStateException(ex.getMessage(), ex); 
            } 
        } 
        
        throw new IllegalStateException("Report name "+ name +" not recognized"); 
    } 
    
    private static URL getResourceImpl( String name ) throws Exception  {
        File customFolder = null; 
        if ( isDeveloperMode() ) { 
            customFolder = getCustomFolder(); 
        }
        
        Map env = OsirisContext.getSession().getEnv(); 
        String cusDir = (String) env.get("report.custom");
        if ( cusDir == null || cusDir.trim().length()==0 ) { 
            cusDir = (String) env.get("app.custom"); 
        } 
        
        final String preferredName = name; 
        String customReportName = null; 
        if ( cusDir != null ) { 
            String oDir = name.substring(0, name.lastIndexOf("/")); 
            String oFname = name.substring(name.lastIndexOf("/")); 
            customReportName = oDir + "/" + cusDir + oFname; 
        } 
        
        List<String> names = new ArrayList();
        if ( customFolder != null ) {
            names.addAll( getReportNames( customReportName ) ); 
            names.addAll( getReportNames( preferredName ) );
            for ( String tmpname : names ) {
                File ofile = new File(customFolder, tmpname); 
                if ( ofile.exists() && ofile.isFile() ) { 
                    return ofile.toURI().toURL(); 
                } 
            }
        } 
        names.clear(); 
        names.addAll( getReportNames( customReportName ) ); 
        names.addAll( getReportNames( preferredName ) ); 
        for ( String tmpname : names ) {
            URL u = ReportUtil.class.getClassLoader().getResource( tmpname ); 
            if ( u != null ) { return u; }
        }
        return null; 
    } 
    
    private static List<String> getReportNames( String name ) { 
        List<String> names = new ArrayList();
        if ( name == null || name.trim().length()==0 ) {
            return names; 
        }
        
        Map env = ClientContext.getCurrentContext().getAppEnv(); 
        Object ov = ( env == null? null : env.get("printer.name") ); 
        String printerName = (ov == null? null : ov.toString().trim());         
        if ( printerName != null && printerName.length() > 0 ) {
            int idx = name.lastIndexOf("."); 
            String str = name.substring(0, idx); 
            names.add( str + "." + printerName + name.substring(idx));  
        }
        names.add( name ); 
        return names; 
    }
    
    public static URL getResource( String name ) {
        try {
            return getResourceImpl( name ); 
        } catch( MalformedURLException mue ) { 
            mue.printStackTrace(); 
            return null; 
        } catch( Exception e ) { 
            return null; 
        } 
    }

    public static InputStream getResourceAsStream( String name ) {
        try {
            URL res = getResource( name ); 
            return ( res==null? null : res.openStream() );  
        } catch( java.io.FileNotFoundException ffe ) { 
            return null; 
        } catch( Exception e ) { 
            return null; 
        } 
    } 

    public static URL getImage( String name ) { 
        Map env = OsirisContext.getSession().getEnv(); 
        String reshost = (String) env.get("res.host"); 
        String resname = "images/" + name; 
        if ( reshost != null && reshost.trim().length() > 0 ) { 
            try {
                CacheResource cache = new CacheResource(); 
                URL oURL = cache.getResource( resname ); 
                if ( oURL != null ) { return oURL; } 
                
                String shost = "http://"+ reshost +"/"+ resname; 
                oURL = new URL( shost ); 
                oURL.openStream(); 
                cache.put( resname, oURL ); 
                return oURL; 
            } catch( MalformedURLException mue ) { 
                mue.printStackTrace(); 
            } catch( java.io.FileNotFoundException ffe ) { 
                //do nothing 
            } catch (Throwable t) { 
                t.printStackTrace(); 
            } 
        } 
        return getResource( resname ); 
    } 
    
    public static InputStream getImageAsStream( String name ) { 
        try {
            URL res = getImage( name ); 
            return ( res==null? null : res.openStream() );  
        } catch( java.io.FileNotFoundException ffe ) { 
            return null; 
        } catch( Exception e ) { 
            return null; 
        } 
    } 
    

    public static boolean hasReport( String name ) {
        URL url = getResource( name ); 
        return ( url != null ); 
    }
        
    public static boolean isDeveloperMode() {
        try { 
            Object opener = Inv.lookupOpener("sysreport:edit", new HashMap()); 
            return ( opener==null? false: true ); 
        } catch(Throwable t) {
            return false; 
        } 
    }
    
    public static File getCustomFolder() { 
        try { 
            File file = new File( System.getProperty("java.io.tmpdir") + "/.rameses_report_custom" ); 
            if ( file.exists() && file.isFile() ) {
                byte[] bytes = IOStream.toByteArray( file ); 
                file = new File( new String(bytes) );
                if ( !file.exists() ) {
                    file.mkdirs();
                } 
                return file; 
            }
        } catch( Throwable t ) {
            t.printStackTrace(); 
        } 
        
        File userdir = new File( System.getProperty("user.dir") ); 
        File outputdir = new File( userdir, "customreport" ); 
        if ( !outputdir.exists() ) outputdir.mkdir(); 
        
        return outputdir; 
    }
    
    public static void setCustomFolder( File folder ) {
        FileWriter fw = null; 
        try { 
            File file = new File( System.getProperty("java.io.tmpdir") + "/.rameses_report_custom" ); 
            fw = new FileWriter( file ); 
            fw.write( folder.getAbsolutePath() ); 
            fw.flush(); 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } finally { 
            try { fw.close(); }catch(Throwable t) {;} 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" CacheResource facility ">  
    
    public static class CacheResource { 
        
        static {
            System.out.println("Starting cache resource cleaner...");
            new Thread( new CacheResourceCleaner() ).start(); 
        }
        
        final Base64Cipher cipher = new Base64Cipher(); 
        
        public boolean contains( String name ) { 
            File file = get( name ); 
            return ( file != null ); 
        } 
        
        public File get( String name ) {
            File dir = getCacheDir(); 
            String enckey = Encoder.MD5.encode( name ).toLowerCase(); 
            File file = new File( dir, enckey );
            return ( file.isFile()? file : null );  
        }

        public URL getResource( String name ) { 
            File file = get( name ); 
            if ( file == null ) { return null; } 
            
            try { 
                return file.toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException( ex.getMessage(), ex ); 
            }
        }
        
        public InputStream getResourceAsStream( String name ) {
            URL url = getResource( name ); 
            try { 
                return ( url == null? null: url.openStream() );
            } catch (IOException ex) {
                throw new RuntimeException( ex.getMessage(), ex ); 
            } 
        } 
        
        public void put( String name, File file ) {
            try { 
                put( name, file.toURI().toURL() );
            } catch (MalformedURLException ex) { 
                throw new RuntimeException( ex.getMessage(), ex ); 
            } 
        }

        public void put( String name, URL url ) {
            put( name, IOStream.toByteArray(url) ); 
        }

        public void put( String name, byte[] bytes ) {
            String encdata = cipher.encode( bytes ); 
            String enckey = Encoder.MD5.encode( name ).toLowerCase();    
            File dir = getCacheDir();             
            File file = new File( dir, enckey ); 
            FileOutputStream fos = null; 
            try {
                fos = new FileOutputStream( file ); 
                IOStream.write( new ByteArrayInputStream( bytes ), fos );  
            } catch( FileNotFoundException ffe ) {
                throw new RuntimeException( ffe.getMessage(), ffe ); 
            } finally {
                try { fos.close(); } catch(Throwable t){;} 
            } 
        } 

        public File getCacheDir() { 
            File dir = new File( System.getProperty("java.io.tmpdir"), "rameses_cache_resource" ); 
            if ( !dir.isDirectory() ) dir.mkdir(); 
            
            return dir; 
        } 
    }
    
    private static class CacheResourceCleaner implements Runnable {
        
        public void run() {
            File dir = new File( System.getProperty("java.io.tmpdir"), "rameses_cache_resource" ); 
            if ( !dir.isDirectory() ) return; 
            
            File bakdir = new File( dir.getParentFile(), "rameses_cache_resource_"+System.currentTimeMillis()); 
            dir.renameTo( bakdir ); 
            
            for ( File f : bakdir.listFiles() ) { 
                try {
                    if ( f.isFile() ) {
                        f.delete(); 
                    } 
                } catch (Throwable t) { 
                    System.out.println( "[CacheResourceCleaner] " + t.getMessage() ); 
                } 
            } 
            
            try { 
                bakdir.delete(); 
            } catch( Throwable t ) {
                System.out.println( "[CacheResourceCleaner] " + t.getMessage() ); 
            } 
        }
    }
    
    // </editor-fold>
}
