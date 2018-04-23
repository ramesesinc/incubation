package com.rameses.rcp.draw.interfaces;

import com.rameses.osiris2.Invoker;
import com.rameses.rcp.draw.support.AttributeKey;
import java.awt.Image;
import java.util.List;

public interface Editor {
    public Drawing getDrawing();
    public void setDrawing(Drawing drawing);
    
    public Canvas getCanvas();
    public void setCanvas(Canvas canvas);
    
    public Image getImage();
    public Image getImage(boolean crop);
    public Image getImage(boolean crop, String imageType);
    
    public Tool getCurrentTool();
    public void setCurrentTool(Tool tool);
    public Tool getDefaultTool();
    
    public boolean isReadonly();
    public void setReadonly(boolean readonly);
    
    public void reset();

    public void deleteSelections();

    public void openFigure(Figure figure);
    
    public void addListener(EditorListener listener);
    public void removeListener(EditorListener listener);

    public void notifyAddedListener(Figure createdFigure);
    public boolean notifyBeforeRemoveListener(List<Figure> figures);
    public void notifyAfterRemoveListener();

    public List<Invoker> showMenu(Figure figure);

    public void propertyChanged(AttributeKey key, Object value);

    public void setDefaultTool();

}
