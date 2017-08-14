/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;

import com.rameses.osiris2.report.CrosstabReport.FieldProperty;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author wflores
 */
public final class CrosstabReportBuilder {
        
    
    public JasperReport buildReport( CrosstabReport report ) throws Exception {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream( parseReport(report).getBytes());
            return JasperCompileManager.compileReport(is);
        }
        catch(Exception e) {
            throw e;
        } finally {
            try { is.close(); } catch(Exception e){;}
        }
    }  
    
    private ReportColumn findColumn(List<ReportColumn> cols, String name ) { 
        if ( name == null ) return null; 
        
        for (int i=0; i<cols.size(); i++) { 
            ReportColumn rc = cols.get(i); 
            if ( name.equals(rc.getName()) ) { 
                return rc; 
            } 
        } 
        return null; 
    }
    
    private String parseReport(CrosstabReport report) throws Exception { 
        for (FieldProperty fp : report.getRowGroups()) {
            ReportColumn rc = report.findColumn( fp.getName() ); 
            if ( rc == null ) throw new Exception("Unable to find report column "+ fp.getName());
        }
        for (FieldProperty fp : report.getColumnGroups()) {
            ReportColumn rc = report.findColumn( fp.getName() ); 
            if ( rc == null ) throw new Exception("Unable to find report column "+ fp.getName());
        }
        for (FieldProperty fp : report.getMeasures()) {
            ReportColumn rc = report.findColumn( fp.getName() ); 
            if ( rc == null ) throw new Exception("Unable to find report column "+ fp.getName());
        }
        
        int pageWidth = 612; 
        int pageHeight = 792;
        int topMargin = 18; int leftMargin = 18; 
        int bottomMargin = 18; int rightMargin = 18; 
        int columnWidth = pageWidth - leftMargin - rightMargin; 
        String orientation = report.getPreferredOrientation(); 
        if ( "Landscape".equals(orientation)) {
            pageWidth = 792; pageHeight = 612; 
            columnWidth = pageWidth - leftMargin - rightMargin; 
        } 
        
        StringBuilder sb = new StringBuilder();        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<!DOCTYPE jasperReport PUBLIC \"//JasperReports//DTD Report Design//EN\" \"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd\">");
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
        
        sb.append("<group name=\"data\">");
        sb.append(" <groupExpression><![CDATA[]]></groupExpression>");
        sb.append(" <groupHeader><band height=\"0\" isSplitAllowed=\"true\"></band></groupHeader>");
        sb.append(" <groupFooter>");
        sb.append(" <band height=\"0\" isSplitAllowed=\"true\" >");
        sb.append("  <crosstab>");
        sb.append("   <reportElement x=\"0\" y=\"-20\" width=\""+columnWidth+"\" height=\"20\" key=\"crosstab-1\" />");

        sb.append( buildRowGroupHeader( report ));
        sb.append( buildRowGroup( report ) );
        sb.append( buildColGroup( report ) );
        sb.append( buildMeasure( report ) );
        
        sb.append("  </crosstab>");
        sb.append(" </band>");
        sb.append(" </groupFooter>");
        sb.append("</group>");
                
        sb.append("<background><band height=\"0\" isSplitAllowed=\"true\"></band></background>");
        sb.append( buildTitle( report, columnWidth ));
        sb.append("<pageHeader><band height=\"0\" isSplitAllowed=\"true\"></band></pageHeader>");
        sb.append("<columnHeader><band height=\"0\" isSplitAllowed=\"true\"></band></columnHeader>");
        sb.append("<detail><band height=\"0\" isSplitAllowed=\"true\"></band></detail>");
        sb.append("<columnFooter><band height=\"0\" isSplitAllowed=\"true\"></band></columnFooter>");
        sb.append("<pageFooter><band height=\"0\" isSplitAllowed=\"true\"></band></pageFooter>");
        sb.append("<summary><band height=\"0\" isSplitAllowed=\"true\"></band></summary>");
        sb.append("</jasperReport>");
        return sb.toString();
    }
    
    private String buildFields(SimpleTableReport report) throws Exception {
        StringBuilder sb = new StringBuilder();
        for(ReportColumn c: report.getColumns()) {
            sb.append("\n");
            sb.append( "<field name=\"" + c.getName() +  "\" class=\"" + c.getFieldType().getName() + "\"/>" );
        }
        return sb.toString();
    }
    
    private String buildTitle( CrosstabReport report, int pageWidth ) { 
        String title = report.getTitle();
        StringBuilder sb = new StringBuilder(); 
        if ( title == null || title.trim().length() == 0 ) {
            sb.append("<title>");
            sb.append(" <band height=\"20\" isSplitAllowed=\"true\"></band>");
            sb.append("</title>");
            return sb.toString(); 
        }
        
        sb.append("<title>");
        sb.append("<band height=\"70\" isSplitAllowed=\"true\">");
        sb.append("<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
        sb.append(" <reportElement mode=\"Transparent\" x=\"0\" y=\"0\" width=\""+pageWidth+"\" height=\"14\" forecolor=\"#000000\" backcolor=\"#FFFFFF\" key=\"title-staticText-1\"/>");
        sb.append(" <box></box>");
        sb.append(" <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
        sb.append("  <font fontName=\"SansSerif\" pdfFontName=\"Helvetica\" size=\"9\"/>");
        sb.append(" </textElement>");
        sb.append(" <textFieldExpression class=\"java.lang.String\"><![CDATA[\""+title+"\"]]></textFieldExpression>");
        sb.append("</textField>");
        sb.append("</band>");
        sb.append("</title>");
        return sb.toString(); 
    }
    
    private String buildRowGroupHeader( CrosstabReport report ) {
        StringBuilder sb = new StringBuilder(); 
        sb.append("   <crosstabHeaderCell>");
        sb.append("    <cellContents mode=\"Transparent\">");
        sb.append("     <box></box>");
        
        int idx=1; int x=0;
        for (FieldProperty fp : report.getRowGroups()) {
            ReportColumn rc = report.findColumn( fp.getName()); 
            if ( rc == null ) continue; 
            
            int cw = rc.getWidth();
            String caption = fp.getCaption();
            if ( caption == null ) caption = rc.getCaption();
            if ( caption == null ) caption = "";
            
            sb.append("<staticText>");
            sb.append(" <reportElement x=\""+x+"\" y=\"0\" width=\""+cw+"\" height=\"20\" key=\"rowGroupHeader-staticText-"+idx+"\"/>");
            sb.append(" <box></box>");
            sb.append(" <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
            sb.append("  <font/>");
            sb.append(" </textElement>");
            sb.append(" <text><![CDATA["+ caption +"]]></text>");
            sb.append("</staticText>");
            idx += 1;
            x += cw; 
        }
        sb.append("    </cellContents>");
        sb.append("   </crosstabHeaderCell>");        
        return sb.toString(); 
    }
    
    private String buildRowGroup( CrosstabReport report ) {
        StringBuilder sb = new StringBuilder(); 

        int totalColWidth = 0; 
        for (FieldProperty fp : report.getRowGroups()) {
            ReportColumn rc = report.findColumn( fp.getName()); 
            totalColWidth += rc.getWidth(); 
        }
        
        int idx = 1;
        for (FieldProperty fp : report.getRowGroups()) {
            ReportColumn rc = report.findColumn( fp.getName()); 
            Class clazz = rc.getFieldType(); 
            String pattern = fp.getPattern(); 
            if ( pattern == null ) pattern = "";
            
            int width = rc.getWidth(); 
            
            sb.append("<rowGroup name=\""+rc.getName()+"\" width=\""+width+"\"");
            if ( idx == 1 ) sb.append(" totalPosition=\"End\"");
            
            sb.append(">");
            sb.append(" <bucket>");
            sb.append("  <bucketExpression class=\""+clazz.getName()+"\"><![CDATA[$F{"+rc.getName()+"}]]></bucketExpression>");
            sb.append(" </bucket>");

            sb.append(" <crosstabRowHeader>");
            sb.append("  <cellContents mode=\"Transparent\">");
            sb.append("   <box></box>");
            sb.append("   <textField pattern=\""+pattern+"\" isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\">");
            sb.append("    <reportElement x=\"0\" y=\"0\" width=\"100\" height=\"14\" key=\"textField\"/>");
            sb.append("    <box></box>");
            sb.append("    <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
            sb.append("     <font/>");
            sb.append("    </textElement>");
            sb.append("    <textFieldExpression class=\""+clazz.getName()+"\"><![CDATA[$V{"+rc.getName()+"}]]></textFieldExpression>");
            sb.append("   </textField>");
            sb.append("  </cellContents>");
            sb.append(" </crosstabRowHeader>");

            if ( idx == 1) {
                sb.append(" <crosstabTotalRowHeader>");
                sb.append("  <cellContents mode=\"Transparent\">");
                sb.append("   <box></box>");
                sb.append("   <textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
                sb.append("    <reportElement x=\"0\" y=\"5\" width=\""+totalColWidth+"\" height=\"25\" key=\"trh-textField-"+idx+"\"/>");
                sb.append("    <box leftPadding=\"10\"><topPen lineWidth=\"1.0\" lineStyle=\"Solid\"/></box>");
                sb.append("    <textElement textAlignment=\"Left\" verticalAlignment=\"Middle\">");
                sb.append("     <font/>");
                sb.append("    </textElement>");
                sb.append("    <textFieldExpression class=\"java.lang.String\"><![CDATA[\"TOTALS\"]]></textFieldExpression>");
                sb.append("   </textField>");
                sb.append("  </cellContents>");
                sb.append(" </crosstabTotalRowHeader>");
            }
            
            sb.append("</rowGroup>");
            idx += 1; 
        } 
        return sb.toString(); 
    }
    
    private String buildColGroup( CrosstabReport report ) {
        FieldProperty fp = report.getColumnGroups().get(0);         
        ReportColumn rc = report.findColumn( fp.getName()); 
        
        FieldProperty mfp = report.getMeasures().get(0);
        String alignment = fp.getAlignment();
        if ( alignment == null ) alignment = mfp.getAlignment();
        if ( alignment == null ) alignment = "Center";
        
        StringBuilder sb = new StringBuilder(); 
        sb.append("<columnGroup name=\""+rc.getName()+"\" height=\"20\" totalPosition=\"End\" headerPosition=\"Center\">");
        sb.append(" <bucket>");
        sb.append("  <bucketExpression class=\"java.lang.String\"><![CDATA[$F{"+rc.getName()+"}]]></bucketExpression>");
        sb.append(" </bucket>");
        
        sb.append(" <crosstabColumnHeader>");
        sb.append("  <cellContents mode=\"Transparent\">");
        sb.append("   <box></box>");
        sb.append("   <textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
        sb.append("    <reportElement x=\"0\" y=\"0\" width=\"75\" height=\"20\" key=\"textField\"/>");
        sb.append("    <box></box>");
        sb.append("     <textElement textAlignment=\""+alignment+"\" verticalAlignment=\"Middle\">");
        sb.append("      <font/>");
        sb.append("     </textElement>");
        sb.append("     <textFieldExpression class=\"java.lang.String\"><![CDATA[$V{"+rc.getName()+"}]]></textFieldExpression>");
        sb.append("   </textField>");
        sb.append("  </cellContents>");
        sb.append(" </crosstabColumnHeader>");
        
        /*
        sb.append(" <crosstabTotalColumnHeader>");
        sb.append("  <cellContents mode=\"Transparent\">");
        sb.append("   <box></box>");
        sb.append("   <textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
        sb.append("    <reportElement x=\"0\" y=\"0\" width=\"75\" height=\"20\" key=\"textField\"/>");
        sb.append("    <box></box>");
        sb.append("    <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
        sb.append("     <font/>");
        sb.append("    </textElement>");
        sb.append("    <textFieldExpression class=\"java.lang.String\"><![CDATA[\"TOTALS\"]]></textFieldExpression>");
        sb.append("   </textField>");
        sb.append("  </cellContents>");
        sb.append(" </crosstabTotalColumnHeader>");
        */
        
        sb.append("</columnGroup>"); 
        return sb.toString(); 
    }
    
    private String buildMeasure( CrosstabReport report ) {
        FieldProperty mfp = report.getMeasures().get(0);
                
        String alignment = mfp.getAlignment()+"";
        if ( alignment.equalsIgnoreCase("left") ) alignment = "Left";
        else if ( alignment.equalsIgnoreCase("right") ) alignment = "Right";
        else alignment = "Center";

        FieldProperty cfp = report.getColumnGroups().get(0);         
        ReportColumn cf = report.findColumn( cfp.getName());         
        ReportColumn mf = report.findColumn( mfp.getName()); 
        
        String pattern = mfp.getPattern(); 
        if ( pattern == null ) pattern="";
        
        StringBuilder sb = new StringBuilder(); 
        sb.append("<measure name=\""+mf.getName()+"_Sum\" class=\""+mf.getFieldType().getName()+"\" calculation=\"Sum\">");
        sb.append(" <measureExpression><![CDATA[$F{"+mf.getName()+"}]]></measureExpression>");
        sb.append("</measure>");
        
        sb.append("<crosstabCell width=\"75\" height=\"14\">"); 
        sb.append(" <cellContents mode=\"Transparent\">"); 
        sb.append("  <box></box>"); 
        sb.append("  <textField pattern=\""+pattern+"\" isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >"); 
        sb.append("   <reportElement x=\"0\" y=\"0\" width=\"75\" height=\"14\" key=\"textField\"/>"); 
        sb.append("   <box></box>"); 
        sb.append("   <textElement textAlignment=\""+alignment+"\" verticalAlignment=\"Middle\">"); 
        sb.append("    <font/>"); 
        sb.append("   </textElement>"); 
        sb.append("   <textFieldExpression class=\""+mf.getFieldType().getName()+"\"><![CDATA[$V{"+mf.getName()+"_Sum}]]></textFieldExpression>"); 
        sb.append("  </textField>"); 
        sb.append(" </cellContents>"); 
        sb.append("</crosstabCell>"); 
        
        sb.append("<crosstabCell width=\"0\" height=\"14\" columnTotalGroup=\""+cf.getName()+"\">"); 
        sb.append(" <cellContents mode=\"Transparent\">"); 
        sb.append("  <box></box>"); 
        sb.append(" </cellContents>"); 
        sb.append("</crosstabCell>"); 

//        sb.append("<crosstabCell width=\"75\" height=\"14\" columnTotalGroup=\""+cf.getName()+"\">"); 
//        sb.append(" <cellContents mode=\"Transparent\">"); 
//        sb.append("  <box></box>"); 
//        sb.append("  <textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >"); 
//        sb.append("   <reportElement x=\"0\" y=\"0\" width=\"75\" height=\"14\" key=\"textField\"/>"); 
//        sb.append("   <box></box>"); 
//        sb.append("   <textElement textAlignment=\""+alignment+"\" verticalAlignment=\"Middle\">"); 
//        sb.append("    <font/>"); 
//        sb.append("   </textElement>"); 
//        sb.append("   <textFieldExpression class=\"java.lang.Number\"><![CDATA[$V{"+mf.getName()+"_Sum}]]></textFieldExpression>"); 
//        sb.append("  </textField>"); 
//        sb.append(" </cellContents>"); 
//        sb.append("</crosstabCell>"); 

        for ( FieldProperty fp : report.getRowGroups()) {
            ReportColumn rf = report.findColumn( fp.getName());             
            sb.append("<crosstabCell width=\"75\" height=\"30\" rowTotalGroup=\""+rf.getName()+"\">"); 
            sb.append(" <cellContents mode=\"Transparent\">"); 
            sb.append("  <box></box>"); 
            sb.append("  <textField pattern=\""+pattern+"\" isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >"); 
            sb.append("   <reportElement x=\"0\" y=\"5\" width=\"75\" height=\"25\" key=\"textField\"/>"); 
            sb.append("   <box><topPen lineWidth=\"1.0\" lineStyle=\"Solid\"/></box>"); 
            sb.append("   <textElement textAlignment=\""+alignment+"\" verticalAlignment=\"Middle\">"); 
            sb.append("    <font/>"); 
            sb.append("   </textElement>"); 
            sb.append("   <textFieldExpression class=\"java.lang.Number\"><![CDATA[$V{"+mf.getName()+"_Sum}]]></textFieldExpression>"); 
            sb.append("  </textField>"); 
            sb.append(" </cellContents>"); 
            sb.append("</crosstabCell>"); 

            sb.append("<crosstabCell width=\"0\" height=\"30\" rowTotalGroup=\""+rf.getName()+"\" columnTotalGroup=\""+cf.getName()+"\">"); 
            sb.append(" <cellContents mode=\"Transparent\">"); 
            sb.append("  <box></box>"); 
            sb.append(" </cellContents>"); 
            sb.append("</crosstabCell>"); 
                        
            /*sb.append("<crosstabCell width=\"75\" height=\"30\" rowTotalGroup=\""+rf.getName()+"\" columnTotalGroup=\""+cf.getName()+"\">"); 
            sb.append(" <cellContents mode=\"Transparent\">"); 
            sb.append("  <box></box>"); 
            sb.append("  <textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >"); 
            sb.append("   <reportElement x=\"0\" y=\"0\" width=\"75\" height=\"30\" key=\"textField\"/>"); 
            sb.append("   <box></box>"); 
            sb.append("   <textElement textAlignment=\""+alignment+"\" verticalAlignment=\"Middle\">"); 
            sb.append("    <font/>"); 
            sb.append("   </textElement>"); 
            sb.append("   <textFieldExpression class=\"java.lang.Number\"><![CDATA[$V{"+mf.getName()+"_Sum}]]></textFieldExpression>"); 
            sb.append("  </textField>"); 
            sb.append(" </cellContents>"); 
            sb.append("</crosstabCell>"); */
        }
        
        sb.append("<whenNoDataCell>");
        sb.append(" <cellContents mode=\"Transparent\">");
        sb.append("  <box></box>");
        sb.append(" </cellContents>");
        sb.append("</whenNoDataCell>");
        return sb.toString(); 
    }
}
