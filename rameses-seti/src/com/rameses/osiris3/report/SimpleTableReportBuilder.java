/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author dell
 */
public class SimpleTableReportBuilder {
    
    private static int LEFT_START = 20;
    private static int BAND_HEIGHT = 15;
    
    private static String buildHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<!DOCTYPE jasperReport PUBLIC \"//JasperReports//DTD Report Design//EN\" \"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd\">");
        return sb.toString();
    }
    
    public static String buildFields(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        for(ReportColumn c: report.getColumns()) {
            sb.append("\n");
            sb.append( "<field name=\"" + c.getName() +  "\" class=\"" + c.getFieldType().getName() + "\"/>" );
        }
        return sb.toString();
    }
    
    public static String buildTitle(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<title>");
        sb.append( "<band height=\"" + BAND_HEIGHT + "\"><staticText>");
        sb.append( "<reportElement x=\"" + LEFT_START + "\" y=\"0\" width=\"" + report.getMaxWidth() + "\" height=\"" + BAND_HEIGHT + "\"/>");
        sb.append("<text>"+ report.getTitle() +"</text>");
        sb.append( "</staticText></band>");
        sb.append("</title>");
        return sb.toString();
    }
    
    public static String buildColumnHeaders(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<columnHeader>");
        sb.append("<band height=\"" + BAND_HEIGHT + "\">");
        int ix = LEFT_START;
        for(ReportColumn c: report.getColumns()) {
            int iwidth = c.getWidth();
            sb.append("\n");
            sb.append("<staticText>");
            sb.append("<reportElement x=\"" + ix + "\" y=\"0\" width=\"" + iwidth + "\" height=\"" + BAND_HEIGHT + "\"/>");
            sb.append("<textElement>");
            sb.append("<font pdfFontName=\"Helvetica-Bold\" isBold=\"true\"/>");
            sb.append("</textElement>");
            sb.append("<text>"+ c.getCaption() +"</text>");
            sb.append("</staticText>");         
            ix+= iwidth;
        }
        sb.append("</band>");
        sb.append("</columnHeader>");
        return sb.toString();
    }
    
    public static String buildDetail(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<detail>");
        sb.append("<band height=\"" + BAND_HEIGHT + "\">");
        int ix = LEFT_START;
        for(ReportColumn c: report.getColumns()) {
            int iwidth = c.getWidth();
            sb.append("\n");
            sb.append("<textField isBlankWhenNull=\"true\">");
            sb.append("<reportElement x=\"" + ix + "\" y=\"0\" width=\"" + iwidth + "\" height=\"" + BAND_HEIGHT + "\" isPrintWhenDetailOverflows=\"true\"/>");
            sb.append("<textFieldExpression class=\""+c.getFieldType().getName()+"\"><![CDATA[$F{" + c.getName() + "}]]></textFieldExpression>");
            sb.append("</textField>");         
            ix+= iwidth;
        }
        sb.append("</band>");
        sb.append("</detail>");
        return sb.toString();
    }
    
    public static String parseReport(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append( buildHeader() );
        sb.append("<jasperReport name=\""+report.getName()+"\">" );
        sb.append( buildFields(report) );
        if( report.getTitle()!=null ) {
            sb.append( buildTitle(report) );
        }
        sb.append( buildColumnHeaders(report) );
        sb.append( buildDetail(report) );
        sb.append("</jasperReport>");
        
        System.out.println(sb.toString());
        return sb.toString();
    }
    
    public static JasperReport buildReport(SimpleTableReport report) throws Exception {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(parseReport(report).getBytes());
            return JasperCompileManager.compileReport(is);
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            try { is.close(); } catch(Exception e){;}
        }
    }
    
    
}
