/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;


import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo Nazareno
 */
public class TextPrinter {
    
    private PrinterService printerService = new PrinterService();
    private Template template;
    
    //we must make this static so it will only be set once
    private static String printerName; //"EPSON LQ-300+ /II ESC/P 2"
    
    public void parseTemplate(String s ) throws Exception {
        SimpleTemplateEngine se = new SimpleTemplateEngine();
        template = se.createTemplate(s);
    }
    
    public void setPrinterName(String name ) {
        TextPrinter.printerName = name;
    }
    
    public String getPrinterName() {
        return  TextPrinter.printerName;
    }

    public List getPrinters() {
        return printerService.getPrinters();
    }
    
    public void print( Map map ) throws Exception {
        if( printerName == null )
            throw new Exception("Please choose a printerName");
        if(template == null )
            throw new Exception("Please parse a template first");
        String line = null;
        StringReader strReader = null;
        BufferedReader bufferedReader = null;
        try{
            Writable pw = template.make(map);
            strReader = new StringReader(pw.toString());
            bufferedReader = new BufferedReader(strReader);            
            while((line = bufferedReader.readLine()) != null) {
                printerService.printString(printerName, line + "\n");
            }
        }
        catch(FileNotFoundException ex) {
            ex.printStackTrace();               
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {strReader.close();} catch(Exception ex){;}
            try {bufferedReader.close();} catch(Exception ex){;}
        }
    }
}
