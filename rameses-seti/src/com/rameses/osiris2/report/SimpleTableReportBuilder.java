/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author dell
 */
public class SimpleTableReportBuilder {
    
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
    
    public static String parseReport(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();

        int pageWidth = 612; 
        int pageHeight = 792;
        int topMargin = 18; int leftMargin = 18; 
        int bottomMargin = 18; int rightMargin = 18; 
        String orientation = "Portrait"; 
        
        ColumnHeaderBand b = new ColumnHeaderBand(); 
        String strcolheader = b.build( report ); 
        int cwidth = b.getWidth(); 
        int totalwidth = (cwidth + leftMargin + rightMargin); 
        if ( totalwidth <= 612) {
            // do nothing, use the default settings 
        } else if ( totalwidth <= 792) {
            pageWidth = 792;
            pageHeight = 612; 
            orientation = "Landscape";
        } else {
            pageHeight = 612;             
            pageWidth = totalwidth; 
            orientation = "Landscape";
        }
        
        int columnWidth = pageWidth - leftMargin - rightMargin; 
        
        sb.append( buildHeader() );
        sb.append("<jasperReport name=\""+report.getName()+"\" language=\"groovy\" columnCount=\"1\"");
        sb.append("  printOrder=\"Vertical\" orientation=\""+ orientation +"\" pageWidth=\""+pageWidth+"\" pageHeight=\""+pageHeight+"\"");
        sb.append("  columnWidth=\""+columnWidth+"\" columnSpacing=\"0\" leftMargin=\""+leftMargin+"\" rightMargin=\""+rightMargin+"\"");
        sb.append("  topMargin=\""+topMargin+"\" bottomMargin=\""+bottomMargin+"\" whenNoDataType=\"NoPages\" isTitleNewPage=\"false\" isSummaryNewPage=\"false\" >");
        
        sb.append("<property name=\"ireport.scriptlethandling\" value=\"0\" />");
	sb.append("<property name=\"ireport.encoding\" value=\"UTF-8\" />");
	sb.append("<import value=\"java.util.*\" />");
	sb.append("<import value=\"net.sf.jasperreports.engine.*\" />");
	sb.append("<import value=\"net.sf.jasperreports.engine.data.*\" />");

        sb.append("<parameter name=\"PRINTEDBY\" isForPrompting=\"false\" class=\"java.lang.String\"/>");
	sb.append("<parameter name=\"PRINTDATE\" isForPrompting=\"false\" class=\"java.util.Date\"/>");
        
        sb.append( buildFields(report) );        
        sb.append( new PageHeaderBand().build(report, pageWidth) ); 
        sb.append( strcolheader );
        sb.append( new DetailBand().build(report) );
        sb.append( new PageFooterBand().build(report, pageWidth) ); 
        sb.append("</jasperReport>");
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
    
    
    
    private static class PageHeaderBand {
        String build( SimpleTableReport report, int pageWidth ) { 
            String title = report.getTitle(); 
            if ( title == null || title.trim().length() == 0) return ""; 
            
            StringBuilder sb = new StringBuilder();
            sb.append("<pageHeader><band height=\"20\" isSplitAllowed=\"true\" >"); 
            sb.append("<staticText>");
            sb.append("   <reportElement x=\"0\" y=\"0\" width=\""+ pageWidth +"\" height=\"16\" key=\"staticText-pageHeader-1\" />"); 
            sb.append("   <box></box>");
            sb.append("   <textElement>");
            sb.append("      <font pdfFontName=\"Helvetica-Bold\" size=\"12\" isBold=\"true\" />"); 
            sb.append("   </textElement>"); 
            sb.append("   <text><![CDATA["+ title +"]]></text>");
            sb.append("</staticText>");
            sb.append("</band></pageHeader>"); 
            return sb.toString(); 
        }
    }
    private static class ColumnHeaderBand {
        private int totalWidth;
        
        int getWidth() { return totalWidth; } 

        String build( SimpleTableReport report ) { 
            ReportColumn[] cols = report.getColumns().toArray(new ReportColumn[]{}); 
            if ( cols.length <= 0 ) return ""; 

            int x = 0, y = 0; 
            StringBuilder sb = new StringBuilder(); 
            sb.append("<columnHeader><band height=\"14\" isSplitAllowed=\"true\">");
            for ( int i=0; i<cols.length; i++) { 
                int cw = cols[i].getWidth();
                Class datatype = cols[i].getFieldType(); 
                
                String textAlignment = null; 
                if ( java.util.Date.class.isAssignableFrom( datatype )) {
                    textAlignment = "Center"; 
                } else if ( java.lang.Long.class.isAssignableFrom(datatype) || java.lang.Integer.class.isAssignableFrom(datatype)) {
                    textAlignment = "Center"; 
                } else if ( java.lang.Double.class.isAssignableFrom(datatype) || java.math.BigDecimal.class.isAssignableFrom(datatype)) {
                    textAlignment = "Right"; 
                }        
                
                sb.append("<staticText>");
                sb.append("   <reportElement x=\""+ x +"\" y=\""+ y +"\" width=\""+ cw +"\" height=\"14\" key=\"staticText-columnHeader-"+ i +"\" />"); 
                sb.append("   <box></box>");
                sb.append("   <textElement");
                if ( textAlignment != null ) {
                    sb.append(" textAlignment=\""+ textAlignment +"\"");
                } 
                sb.append(" >");
                sb.append("      <font pdfFontName=\"Helvetica-Bold\" size=\"9\" isBold=\"true\" />"); 
                sb.append("   </textElement>"); 
                sb.append("   <text><![CDATA["+ cols[i].getCaption() +"]]></text>");
                sb.append("</staticText>");
                x += cw; 
            } 
            sb.append("</band></columnHeader>");
            totalWidth = x; 
            return sb.toString(); 
        }
    }
    private static class DetailBand {
        String build( SimpleTableReport report ) { 
            ReportColumn[] cols = report.getColumns().toArray(new ReportColumn[]{}); 
            if ( cols.length <= 0 ) return ""; 

            int x = 0, y = 0, h = 12;
            StringBuilder sb = new StringBuilder(); 
            sb.append("<detail><band height=\"12\" isSplitAllowed=\"true\">");
            for ( int i=0; i<cols.length; i++) { 
                int cw = cols[i].getWidth();
                String cname = cols[i].getName(); 
                Class datatype = cols[i].getFieldType();
                String classname = cols[i].getFieldType().getName(); 

                String pattern = null;
                String textAlignment = null; 
                boolean stretchWhenOverflow = true;
                if ( java.util.Date.class.isAssignableFrom( datatype )) {
                    pattern = "yyyy-MM-dd HH:mm:ss"; 
                    textAlignment = "Center"; 
                    stretchWhenOverflow = false;
                    classname = java.util.Date.class.getName(); 
                } else if ( java.lang.Long.class.isAssignableFrom(datatype) || java.lang.Integer.class.isAssignableFrom(datatype)) {
                    pattern = "#,##0"; 
                    textAlignment = "Center"; 
                    stretchWhenOverflow = false;
                    classname = java.lang.Number.class.getName();                    
                } else if ( java.lang.Double.class.isAssignableFrom(datatype) || java.math.BigDecimal.class.isAssignableFrom(datatype)) {
                    pattern = "#,##0.00"; 
                    textAlignment = "Right"; 
                    stretchWhenOverflow = false;
                    classname = java.lang.Number.class.getName();
                } 
                
                sb.append("<textField isStretchWithOverflow=\""+stretchWhenOverflow+"\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\""); 
                if ( pattern != null ) sb.append(" pattern=\""+ pattern +"\""); 
                
                sb.append(">"); 
                sb.append("   <reportElement x=\""+x+"\" y=\""+y+"\" width=\""+cw+"\" height=\""+h+"\" key=\"textField-detail-"+ i +"\" />"); 
                sb.append("   <box></box>");
                sb.append("   <textElement verticalAlignment=\"Top\" ");
                if ( textAlignment != null ) sb.append(" textAlignment=\""+ textAlignment +"\""); 
                
                sb.append(" >");
                sb.append("      <font size=\"9\" />"); 
                sb.append("   </textElement>"); 
                sb.append("   <textFieldExpression class=\""+classname+"\"><![CDATA[$F{"+cname+"}]]></textFieldExpression>");
                sb.append("</textField>");
                x += cw; 
            } 
            sb.append("</band></detail>");
            return sb.toString(); 
        }
    }
    private static class PageFooterBand {
        String build( SimpleTableReport report, int pageWidth ) { 
            String title = report.getTitle(); 
            if ( title == null || title.trim().length() == 0) return ""; 
            
            StringBuilder sb = new StringBuilder();
            sb.append("<pageFooter><band height=\"17\" isSplitAllowed=\"true\" >"); 
            sb.append("<textField isStretchWithOverflow=\"false\" pattern=\"\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
            sb.append("   <reportElement mode=\"Transparent\" x=\"0\" y=\"5\" width=\""+ pageWidth +"\" height=\"12\" key=\"textField-pageFooter-1\" forecolor=\"#000000\" backcolor=\"#FFFFFF\" />"); 
            sb.append("   <box></box>");
            sb.append("   <textElement>");
            sb.append("      <font fontName=\"SansSerif\" pdfFontName=\"Helvetica\" size=\"8\" isBold=\"false\" isItalic=\"false\" isUnderline=\"false\" isPdfEmbedded=\"false\" pdfEncoding=\"Cp1252\" isStrikeThrough=\"false\" />"); 
            sb.append("   </textElement>");
            sb.append("   <textFieldExpression class=\"java.lang.String\" >");
            sb.append("<![CDATA[\"GENERATED BY: ETRACS v2.5          PRINTED BY: \"+ $P{PRINTEDBY} + \"          PRINT DATE: \"+ ($P{PRINTDATE} ? new java.text.SimpleDateFormat(\"yyyy-MM-dd hh:mm:ss\").format($P{PRINTDATE}): \"\")]]>");
            sb.append("   </textFieldExpression>");
            sb.append("</textField>");
            sb.append("</band></pageFooter>"); 
            return sb.toString(); 
        }
    }
}
