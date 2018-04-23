package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.interfaces.Tool;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.SwingUtilities;

public class SelectionTool extends AbstractTool {
    private Tool toolDelegate;
    
    public SelectionTool(){
    }
    
    public SelectionTool(Editor editor){
        super(editor);
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        super.mousePressed(x, y, e);
        
        if (toolDelegate != null){
            return;
        }
        
        Handle handle = getDrawing().handleAt(x, y);
        if (handle != null){
            toolDelegate = new HandleTool(getEditor(), handle);
        }else{
            Figure figure = null;
            if (e.isControlDown()){
                figure = getDrawing().innerFigureAt(x, y);
            }else{
                figure = getDrawing().figureAt(x, y);
            }
            
            if (figure != null){
                toolDelegate = new MultiSelectTool(getEditor(), figure);
            }else{
                if (!e.isShiftDown()){
                    getEditor().getDrawing().clearSelections();
                }
                toolDelegate = new SelectAreaTool(getEditor());
            }
        }
        toolDelegate.mousePressed(x, y, e);
    }   

    @Override
    public void mouseClicked(int x, int y, MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1){
            openFigure(x, y, e);
        }
    }
    
    
    
    @Override
    public void mouseMoved(int x, int y, MouseEvent e) {
        Handle h = getDrawing().handleAt(x, y);
        if (h != null && h.getOwner().isAllowResize()){
            getCanvas().setCursor(h.getCursor());
        }
        else{
            getCanvas().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        if (toolDelegate != null){
            toolDelegate.mouseDrag(x, y, e);
        }
    }


    
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (toolDelegate != null){
            toolDelegate.mouseReleased(x, y, e);
            toolDelegate = null;
        }
    }
    
    @Override
    public void openFigure(int x, int y, MouseEvent e) {
        Figure figure = getDrawing().figureAt(x, y);
        if (figure != null){
            getEditor().openFigure(figure);
        }
    }

    @Override
    public void showMenu(int x, int y, int sx, int sy, MouseEvent e) {
        Figure figure = getDrawing().figureAt(x, y);
        if (figure != null){
            List menus = getEditor().showMenu(figure);
            if (!menus.isEmpty()){
                getEditor().getCanvas().showMenu(sx, sy, menus);
            }
        }
    }
    
    @Override
    protected Cursor getToolCursor(){
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }    
    
}
