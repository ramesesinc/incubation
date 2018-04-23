package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

public class MultiSelectTool extends AbstractTool {

    private Figure figure;
    private int lastX, lastY;
    private boolean moved;

    public MultiSelectTool() {
    }

    public MultiSelectTool(Editor editor, Figure figure) {
        super(editor);
        this.figure = figure;
        moved = false;
    }

    @Override
    public void mousePressed(int x, int y, MouseEvent e) {
        super.mousePressed(x, y, e);
        lastX = x;
        lastY = y;

        if (e.isShiftDown()) {
            getDrawing().toggleSelection(figure);
            figure = null;
        } else if (!getDrawing().isFigureSelected(figure)) {
            getDrawing().clearSelections();
            getDrawing().addSelection(figure);
        }
    }

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        super.mouseDrag(x, y, e);
        moved = (Math.abs(x - getStartX()) > 4) || (Math.abs(y - getStartY()) > 4);

        if (moved && !getEditor().isReadonly()) {
            int dx = x - lastX;
            int dy = y - lastY;

            for (Figure f : getDrawing().getSelections()) {
                if (!(f instanceof LineConnector)){
                    f.moveBy(dx, dy, e);
                }
            }
        }
        lastX = x;
        lastY = y;
    }

    @Override
    protected Cursor getToolCursor() {
        return new Cursor(Cursor.HAND_CURSOR);
    }
}
