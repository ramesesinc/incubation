package com.rameses.rcp.draw;

import com.rameses.rcp.common.DrawModel;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.draw.figures.FigureCache;
import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.EditorListener;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKey;
import com.rameses.rcp.draw.tools.SelectionTool;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class DrawingEditor implements Editor{
    private Drawing drawing;
    private Canvas canvas;
    private Tool currentTool;
    private boolean readonly;
    private List<EditorListener> listeners;
    
    public DrawingEditor(){
        this(null);
    }
    
    public DrawingEditor(Drawing drawing){
        listeners = new ArrayList<EditorListener>();
        readonly = false;
        this.drawing = drawing;
    }
    
    @Override
    public Drawing getDrawing() {
        return drawing;
    }

    @Override
    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        setCurrentTool(getCurrentTool());
    }

    @Override
    public Image getImage() {
        return getImage(true);
    }
    
    @Override
    public Image getImage(boolean crop) {
        return getImage(crop, "PNG");
    }
    
    @Override
    public Image getImage(boolean crop, String imageType) {
        if (getDrawing().getFigures().isEmpty()){
            return null;
        }
        BufferedImage bi = null;
        try{
            bi = getDrawingImage(crop);
            
            //convert to requested imageType e.g. PNG, JPG, gif, BMP
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream image = ImageIO.createImageOutputStream(baos);
            ImageIO.write(bi, imageType, image);
            return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
            
        }catch(Exception ex){
            ex.printStackTrace();
            //return raw image 
            return bi;
        }
    }

    @Override
    public Tool getCurrentTool() {
        if (currentTool != null){
            return currentTool;
        }
        return getDefaultTool();
    }

    @Override
    public void setCurrentTool(Tool currentTool) {
        this.currentTool = currentTool;
        getDrawing().clearSelections();
        getCanvas().refresh();
        currentTool.setToolCursor();
    }

    @Override
    public void setDefaultTool() {
        setCurrentTool(getDefaultTool());
    }
    
    

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        if (canvas != null){
            canvas.setReadonly(readonly);
            setCurrentTool(getDefaultTool());
        }
    }

    @Override
    public Tool getDefaultTool(){
        return new SelectionTool(this);
    }

    @Override
    public void reset() {
        getDrawing().clearSelections();
        getCanvas().refresh();
    }

    @Override
    public List<Figure> deleteSelections() {
        List<Figure> deletedItems = getDrawing().deleteSelections();
        getCanvas().refresh();
        return deletedItems;
    }

    @Override
    public void openFigure(Figure figure) {
        for(EditorListener listener : listeners){
            listener.openFigure(figure);
        }
    }

    @Override
    public List showMenu(Figure figure) {
        List menus = new ArrayList();
        for(EditorListener listener : listeners){
            List items = listener.showMenu(figure);
            for (Object o : items){
                if (o instanceof Opener){
                    menus.add(o);
                }
            }
        }
        return menus;
    }
    
    

    @Override
    public void notifyAddedListener(Figure figure) {
        for(EditorListener listener : listeners){
            listener.figureAdded(figure);
        }
    }
    
    @Override
    public boolean notifyBeforeRemoveListener(List<Figure> figures) {
        for(EditorListener listener : listeners){
            return listener.beforeRemoveFigures(figures);
        }
        return true;
    }
    
    @Override
    public void notifyAfterRemoveListener(List<Figure> deletedItems) {
        for(EditorListener listener : listeners){
            listener.afterRemoveFigures(deletedItems);
        }
    }

    @Override
    public void notifyConnectionChangedListener(Connector c, Figure fromFigure, Figure toFigure) {
        for(EditorListener listener : listeners){
            listener.connectionChanged(c, fromFigure, toFigure);
        }
    }
    
    
    

    @Override
    public void addListener(EditorListener listener) {
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeListener(EditorListener listener) {
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

 
    @Override
    public void propertyChanged(AttributeKey key, Object value){
        for (Figure f: getDrawing().getSelections()){
            f.set(key, value);
        }
        getCanvas().refresh();
    }

    @Override
    public void loadDrawing(DrawModel handler) {
        loadDrawing(handler, handler.fetchData(null));
    }
    
    @Override
    public void loadDrawing(DrawModel handler, Object drawing) {
        Map data = new HashMap(); 
        if (drawing instanceof Map){
            data = (Map)drawing;
        }else if (drawing instanceof String){
            //TODO:
            //this is an xml file, process xml and return as map
            //data = XmlDataProcess.process(o);
            data = new HashMap();
        }
        
        loadFigures(handler, data);
        loadConnectors(handler, data);
        //getCanvas().revalidateRect(new Rectangle());
        //getCanvas().requestFocus();
    }
    
    private void loadFigures(DrawModel handler, Map data){
        List<Map> figures = (List)data.get("figures");
        if (figures != null){
            for (Map fprop : figures){
                Figure figure = loadFigure(handler, fprop);
                if (figure != null){
                    getDrawing().addFigure(figure);
                }
            }
        }
    }    
    
    private void loadConnectors(DrawModel handler, Map data){
        List<Map> list = (List)data.get("connectors");
        if (list != null && !list.isEmpty()){
            for (Map cprop : list){
                Figure startFigure = getDrawing().figureById(cprop.get("startFigureId").toString());
                Figure endFigure = getDrawing().figureById(cprop.get("endFigureId").toString());
                if (startFigure != null && endFigure != null){
                    loadConnector(handler, startFigure, endFigure, cprop);
                }
            }
        }
    }  
    
    private Connector loadConnector(DrawModel handler, Figure startFigure, Figure endFigure, Map cprop){
        Connector connector = (LineConnector) loadFigure(handler, cprop);
        if (connector != null){
            boolean update = connector.getPoints().isEmpty();
            connector.setStartFigure(startFigure, update);
            connector.setEndFigure(endFigure, update);
            getDrawing().addConnector(connector);
        }
        return connector;
    }
        
    private final Figure loadFigure(DrawModel handler, Map fprop){
        Map ui = (Map)fprop.get("ui");

        Figure figure = null;
        try{
            Figure prototype = FigureCache.getInstance().getFigure(ui.get("type")+"");
            if (prototype != null){
                figure = prototype.getClass().newInstance();
                if (handler != null){
                    figure.showHandles(handler.showHandles());
                }
                figure.readAttributes(fprop);
            }else{
                System.out.println("No Figure is associated with the type " + ui.get("type") + ".");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Unable to create Figure for type " + ui.get("type") + ".");
            System.out.println("[ERROR] : " + ex.getMessage());
        }
        return figure;
    }
    
    
    
    
    private BufferedImage getDrawingImage(boolean crop){
        Rectangle r = new Rectangle();
        if (getCanvas() != null){
            r = getCanvas().getBounds();
        }
        if (r.isEmpty()){
            //the canvas is not yet rendered, 
            // use the drawing bounds instead
            r = getDrawing().getBounds();
        }
        //add padding
        r.grow(10, 10);
        BufferedImage bi = new BufferedImage(r.x + r.width , r.y + r.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        DrawUtil.setHDRenderingHints(g);
        try{
            for( Figure f : getDrawing().getFigures()){
                f.draw(g);
            }
        }
        catch(Exception ex){
            bi = null;
            ex.printStackTrace();
        }

        if (bi != null && crop){
            bi = DrawUtil.cropImage(bi);
        }
        return bi;
    }   
}
