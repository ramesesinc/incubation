package com.rameses.rcp.draw;

import com.rameses.rcp.common.Opener;
import com.rameses.rcp.draw.interfaces.Canvas;
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
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class DrawingEditor implements Editor{
    private Drawing drawing;
    private Canvas canvas;
    private Tool currentTool;
    private boolean interactive;
    private List<EditorListener> listeners;
    
    public DrawingEditor(){
        listeners = new ArrayList<EditorListener>();
        interactive = true;
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
        return getImage(false);
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
    public boolean isInteractive() {
        return interactive;
    }

    @Override
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        if (canvas != null){
            canvas.setInteractive(interactive);
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
    public void deleteSelections() {
        getDrawing().deleteSelections();
        getCanvas().refresh();
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
            return listener.beforeRemoveFigure(figures);
        }
        return true;
    }
    
    @Override
    public void notifyAfterRemoveListener() {
        for(EditorListener listener : listeners){
            listener.afterRemoveFigure();
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
    
    private BufferedImage getDrawingImage(boolean crop){
        Rectangle r = getCanvas().getBounds();
        BufferedImage bi = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
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
