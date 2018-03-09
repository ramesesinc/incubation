/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author Elmo Nazareno
 */
public class TextPrinter {
     private PrinterService printerService = new PrinterService();
    
    private String printerName;
    private Template template;

    /**
     * @return the printerName
     */
    public String getPrinterName() {
        return printerName;
    }

    /**
     * @param printerName the printerName to set
     */
    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    /**
     * @return the template
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(Template template) {
        this.template = template;
    }
    
    public void setTemplate(InputStream is) {
        try {
            if(is==null) throw new Exception("InputStream in setTemplate must not be null");
            SimpleTemplateEngine se = new SimpleTemplateEngine();
            this.template = se.createTemplate(new InputStreamReader(is));
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try { is.close(); } catch(Exception ign){;}
        }
    }
    
    public void setTemplate(String str ) {
        try {
            if(str==null) throw new Exception("String in setTemplate must not be null");
            SimpleTemplateEngine se = new SimpleTemplateEngine();
            this.template = se.createTemplate(new StringReader(str));
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void print( Map data ) throws Exception {
        if(printerName == null ) printerName = printerService.getPrinters().iterator().next();
        Writable pw  = template.make(data);
        printerService.printString(printerName, pw.toString());
    }
}
