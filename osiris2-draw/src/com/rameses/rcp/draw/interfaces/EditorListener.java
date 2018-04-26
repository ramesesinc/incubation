package com.rameses.rcp.draw.interfaces;

import java.util.List;


public interface EditorListener {
    public void openFigure(Figure figure);
    public void figureAdded(Figure figure);
    public boolean beforeRemoveFigures(List<Figure> figures);
    public void afterRemoveFigures(List<Figure> deletedItems);
    public List showMenu(Figure figure);
}
