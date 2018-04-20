package com.rameses.rcp.draw.interfaces;

import java.util.List;


public interface EditorListener {
    public void openFigure(Figure figure);
    public void figureAdded(Figure figure);
    public boolean beforeRemoveFigure(List<Figure> figures);
    public void afterRemoveFigure();
    public List showMenu(Figure figure);
}
