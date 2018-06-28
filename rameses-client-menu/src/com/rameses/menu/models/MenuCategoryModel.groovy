package com.rameses.menu.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;


class MenuCategoryModel  { 

    @Binding
    def binding;
    
    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;

    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def menuHtml;
    def invokers = [:];

    def fontColor = "black";
    def fontFace = "arial";
    int fontSize = 5;
    int cellwidth = 350;
    int iconwidth = 80;
    
    String getTitle() {
        if( invoker.properties.formTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formTitle,this);
        }
        if( invoker.caption ) {
            return invoker.caption;
        }
         if( invoker.properties.windowTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.windowTitle,this);
        }
        return "";
    }
    
    @FormId
    String getFormId() {
        if( invoker.properties.formId ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formId,this);
        }
        return workunit.workunit.id + "_menu";
    }
    
    //this is used for getting the actions
    public String getFormName() {
        if( workunit.info.workunit_properties.formName ) {
            return workunit.info.workunit_properties.formName;
        }
        return getContext()+"_menu";
    }
    
    public String getContext() {
        String context = invoker.properties.context;
        if(context==null) {
            context = workunit?.info?.workunit_properties?.context;
        }
        if(!context.startsWith("menucategory/")){
            context= "menucategory/"+context;
        }
        if(context==null) throw new Exception("Please provide a context in category menu");
        return context;
    }
    
    /**************************************************************************
    * specify number of columns for the menu display. default is 2 cols
    ***************************************************************************/
    public int getCols() {
        String _cols = invoker?.properties?.cols;
        if(_cols==null) {
            _cols = workunit?.info?.workunit_properties?.cols;
        }
        if(_cols) {
            try {
                return Integer.parseInt(_cols);
            }
            catch(ign){
                return 2;
            }
        }
        else {
            return 2;
        }
    }
    
    
    def buildModel() {
        def model = [];
        
        String headMenu = getContext();
        def folders = session.getFolders(headMenu);
        int i = 1;
        def entry = null;
        folders.each {f ->
            if(i==1) {
                entry = [ list:[], rowsize : 0 ];
                model << entry;
            }  
            def m = [subitems:[]];
            m.id = f.properties.id;
            m.caption = f.properties.caption;
            m.icon = f.properties.icon;
            
            session.getFolders(headMenu + "/" + m.id ).each { sf->
                if(sf.invoker) {
                    def mm = [:];
                    mm.id = sf.toString();
                    mm.caption = sf.caption;
                    m.subitems << mm;
                    invokers.put( mm.id, sf.invoker ); 
                }       
            }
            if( !m.subitems ) return;
            
            entry.list << m;
            if( m.subitems.size() > entry.rowsize ) {
                entry.rowsize = m.subitems.size();
            }
            
            //reset the number
            if(i == cols) {
                i = 1;
            }
            else {
                i++;    
            }
        }
        
        //correct the final
        model = model.findAll{ it.rowsize > 0 };
        return model;
    }
    
    void render(def model) {
        
        int width = cellwidth*cols;
        def sb = new StringBuilder();
        sb.append( "<table width=${width}>" );
        model.each { row ->
            sb.append("<tr>");
            
            int height = 100 + (row.rowsize * 20);
            
            //display each category
            row.list.each { m->
               sb.append("<td width=${cellwidth}>");
               
                    //value of one cell here
                    sb.append("<table cellpadding=0 height=${height} width=${cellwidth}>");
                        sb.append("<tr>");
                            sb.append("<td valign=top style=\"padding-left:20px;\" height=${height} width=${iconwidth}>");
                                sb.append( "<image src=\"classpath://${m.icon}\" />");
                            sb.append("</td>");
                            sb.append("<td valign=top  style=\"padding-left:10px;;\" height=${height}>");
                                sb.append( "<b><font size=${fontSize} face=\"${fontFace}\" color=\"${fontColor}\">${m.caption}</font></b><br>");
                                m.subitems.each { sf->
                                    sb.append( "<a href=\"openItem\" id=\"${sf.id}\"><font size=${fontSize-1} face=\"${fontFace}\">${sf.caption}</font></a><br>" );
                                } 
                            sb.append("</td>");
                        sb.append("</tr>");
                    sb.append("</table>");
                
                sb.append("</td>");
            }
            
            sb.append("</tr>");
        }
        sb.append("</table>");
        menuHtml = sb.toString();
    }
    
    void init() {
        def model = buildModel();
        render( model );
    }
    

    def openItem( def h ) {
        def op = invokers.get( h.id );
        return Inv.invoke( op  ); 
    }

    
    //debugging purposes
    def viewHtml() {
        MsgBox.alert( menuHtml ); 
    }

    void changeColor() {
        def color = MsgBox.prompt( "Enter color ")
        if(color) fontColor = color;
        init();
        binding.refresh();
    }
    
    boolean isExist() {
        return true;
    }
    
} 