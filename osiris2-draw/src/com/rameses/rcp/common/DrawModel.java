package com.rameses.rcp.common;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.components.DrawComponentModel;
import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Connector;
import java.util.ArrayList;
import java.util.List;
import com.rameses.rcp.draw.figures.FigureCache;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;


public class DrawModel {
    private DrawComponentModel componentModel;
    private Editor editor;
    private boolean readonly = false;
    
    
    public void setComponentModel(DrawComponentModel componentModel){
        this.componentModel = componentModel;
    }
    
    public Editor getEditor(){
        return editor;
    }
    
    public void setEditor(Editor editor){
        this.editor = editor;
    }
    
    public Object fetchCategories(){
        return null;
    }
    
    public Object fetchData(Object o){
        return null;
    }
    
    public Object open(Object o){
        return null;
    }
    
    public List showMenu(Object o){
        return new ArrayList();
    }

    public DrawComponentModel getComponentModel() {
        return componentModel;
    }

    public void onAddItem(Object o){
    }
    
    public boolean beforeRemoveItem(Object o){
        return true;
    }
    
    public void afterRemoveItem(){
    }
    
    public List<Figure> getFigures(){
        return editor.getDrawing().getFigures();
    }
    
    public void reload(){
        editor.getDrawing().clearFigures();
        editor.setReadonly(isReadonly());
        loadDrawing();
        editor.getCanvas().revalidateRect(new Rectangle());
        editor.getCanvas().requestFocus();
        refresh();
    }
    
    public void refresh(){
        editor.getCanvas().refresh();
        editor.setDefaultTool();
        componentModel.getBinding().refresh();
        componentModel.getCallerBinding().refresh();
    }
    
    
    protected void loadDrawing(){
        Object o = fetchData(componentModel.getCategory());
        Map data = new HashMap(); 
        if (o instanceof Map){
            data = (Map)o;
        }else if (o instanceof String){
            //TODO:
            //this is an xml file, process xml and return as map
            //data = XmlDataProcess.process(o);
            data = new HashMap();
        }
        
        loadFigures(data);
        loadConnectors(data);
    }
    
    protected void loadFigures(Map data){
        List<Map> figures = (List)data.get("figures");
        if (figures != null){
            for (Map fprop : figures){
                Figure figure = loadFigure(fprop);
                if (figure != null){
                    editor.getDrawing().addFigure(figure);
                }
            }
        }
    }
    
    protected void loadConnectors(Map data){
        List<Map> list = (List)data.get("connectors");
        if (list != null && !list.isEmpty()){
            for (Map cprop : list){
                Figure startFigure = getEditor().getDrawing().figureById(cprop.get("startFigureId").toString());
                Figure endFigure = getEditor().getDrawing().figureById(cprop.get("endFigureId").toString());
                if (startFigure != null && endFigure != null){
                    loadConnector(startFigure, endFigure, cprop);
                }
            }
        }
    }    
        
    protected final Connector loadConnector(Figure startFigure, Figure endFigure, Map cprop){
        Connector connector = (LineConnector) loadFigure(cprop);
        if (connector != null){
            connector.setStartFigure(startFigure, false);
            connector.setEndFigure(endFigure, false);
            getEditor().getDrawing().addConnector(connector);
        }
        return connector;
    }
        
    protected final Figure loadFigure(Map fprop){
        Map ui = (Map)fprop.get("ui");

        Figure figure = null;
        try{
            Figure prototype = FigureCache.getInstance().getFigure(ui.get("type")+"");
            if (prototype != null){
                figure = prototype.getClass().newInstance();
                figure.showHandles(showHandles());
                figure.readAttributes(fprop);
            }else{
                System.out.println("No Figure is associated with the type " + ui.get("type") + ".");
            }
        }
        catch(Exception ex){
            System.out.println("Unable to create Figure for type " + ui.get("type") + ".");
            System.out.println("[ERROR] : " + ex.getMessage());
        }
        return figure;
    }
    
    public boolean showCategories() {
        return true;
    }
    
    public boolean isReadonly(){
        return readonly;
    }
    
    public final void setReadonly(boolean readonly){
        this.readonly = readonly;
        getEditor().setReadonly(readonly);
        refresh();
    }
    
    public boolean showHandles(){
        return true;
    }
    
    public boolean showToolbars(){
        return true;
    }
    
    public boolean showDrawTools(){
        return true;
    }
    
    public boolean showEditTools(){
        return true;
    }
    
    public String getXml(){
        return getEditor().getDrawing().getXml();
    }

    public Map getData(){
        return getEditor().getDrawing().getData();
    }
    
    public Image getImage(){
        return getImage(false);
    }
    
    public Image getImage(boolean crop){
        return getImage(true, "PNG");
    }
    
    public Image getImage(boolean crop, String imageType){
        return getEditor().getImage(crop, imageType);
    }
}
