package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.decorators.ArrowTip;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.LineDecoration;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.utils.Geom;
import com.rameses.rcp.draw.tools.ConnectorTool;
import com.rameses.rcp.draw.utils.DataUtil;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class LineConnector extends PolyLineFigure implements Connector {

    private Figure startFigure;
    private Figure endFigure;

    public LineConnector() {
        super();
        addPoint(0,0);
        addPoint(0,0);
    }
        
    @Override
    public Tool getTool() {
        return new ConnectorTool();
    }
    
    @Override
    public String getToolCaption() {
        return "Arrow Connector";
    }
    
    @Override
    public String getIcon() {
        return "images/draw/connector16.png";
    }
    
        
    @Override
    public String getType(){
        return "arrow";
    }
    

    @Override
    protected void drawFigure(Graphics2D g) {
        super.drawFigure(g);
        drawDecoration(g);
    }
        
    public void addPoint(int idx, Point pt){
        getPoints().add(idx, pt);
        updateDisplayBox();
    }
    
    @Override
    public void moveBy(int dx, int dy, MouseEvent e, Figure sourceFigure){
        Point p = null; 
        
        if (sourceFigure == startFigure){
            p = getStartPoint();
        }
        else if (sourceFigure == endFigure){
            p = getEndPoint();
        }
        
        p.x += dx;
        p.y += dy;
        
        if (startFigure.getSelected() && endFigure.getSelected() && startFigure == sourceFigure){
            for (int i = 1; i < getPoints().size() - 1; i++){
                Point pt = getPoints().get(i);
                pt.x += dx;
                pt.y += dy;
            }
            moveTextFigue(dx, dy, e);
        }
    }
    

    @Override
    public Figure getStartFigure() {
        return startFigure;
    }

    @Override
    public void setStartFigure(Figure figure) {
        setStartFigure(figure, true);
    }
    
    @Override
    public void setStartFigure(Figure figure, boolean updateConnectorPoint) {
        this.startFigure = figure;

        if (figure == null) {
            return;
        }
        if(updateConnectorPoint){
            Point loc = figure.getDisplayBox().getLocation();
            Point p = getStartPoint();
            p.x = figure.getCenter().x;
            p.y = figure.getCenter().y;
        }

        figure.addConnector(this);
    }

    @Override
    public Figure getEndFigure() {
        return endFigure;
    }

    @Override
    public void setEndFigure(Figure figure) {
        setEndFigure(figure, true);
    }
    
    @Override
    public void setEndFigure(Figure figure, boolean updateConnectorPoint) {
        this.endFigure = figure;
        
        if (figure == null) {
            return;
        }
        if (updateConnectorPoint){
            updateConnectionPoint(figure, getStartFigure(), getEndPoint());
            updateConnectionPoint(getStartFigure(), figure, getStartPoint());
        }
        figure.addConnector(this);
    }

    @Override
    public boolean isConnectionAllowed() {
        return false;
    }
    
    
    public Point getStartPoint() {
        return getPoints().get(0);
    }

    public Point getEndPoint() {
        return getPoints().get(getPoints().size() - 1);
    }
    
    @Override
    public LineDecoration getEndDecoration() {
        return new ArrowTip();
    }
    
    private void drawDecoration(Graphics2D g){
        if (startFigure != null && endFigure != null){
            LineDecoration decoration = getEndDecoration();
            if (decoration != null){
                Point ps = getStartPoint();
                Point pe = getEndPoint();
                if (getPoints().size() > 2){
                    ps = getPoints().get(getPoints().size()-2);
                    pe = getPoints().get(getPoints().size()-1);
                }
                Point2D.Double p1 = new Point2D.Double(pe.x, pe.y);
                Point2D.Double p2 = new Point2D.Double(ps.x, ps.y);
                decoration.draw((Graphics2D)g, endFigure, p1, p2);
            }
        }
    }

    /* Checks that xy is not in close proximity to existing points */
    @Override
    public boolean allowChop(int x, int y) {
        int tolerance = 5;
        
        for(Point p : getPoints()){
            if (Math.abs(p.x - x) <= tolerance && Math.abs(p.y - y) <= tolerance){
                return false;
            }
        }
        return true;
    }
    
    /* Chops a line segment.
     * split parameter
     *    - true, break to 2 segments in x,y
     *    - false, remove point in x,y
     */
    @Override
    public Point chop(int x, int y, boolean split){
        int idx; 
        
        if (split){
            idx = findIndexFromPoints(x, y);
            Point p = new Point(x, y);
            addPoint(idx, p);
            return p;
        }else{
            idx = findIndexFromPoints(x, y);
            getPoints().remove(idx);
        }
        return null;
    }
        
    
    /* Returns the points array index of the first 
     * point of the line segment xy is located
     */
    private int findIndexFromPoints(int x, int y){
        if (getPoints().size() == 2){
            return 1;
        }
        
        Point p1 = getPoints().get(0);
        for ( int i = 1; i < getPoints().size(); i++ ){
            Point p2 = getPoints().get(i);
            if (Geom.pointInLine(p1.x, p1.y, p2.x, p2.y, x, y, 10)){
                return i;
            }
            p1 = p2;
        }
        throw new RuntimeException("Invalid (x,y).");
    }

    protected void updateConnectionPoint(Figure figure, Figure targetFigure, Point p){
        if (figure == null){
            return;
        }
        Point refPoint = targetFigure.getCenter();
        
        Point intersect = figure.intersect(refPoint);
        if (intersect != null){
            p.x = intersect.x;
            p.y = intersect.y;
        }
        else {
            Point loc = figure.getDisplayBox().getLocation();
            p.x = figure.getCenter().x;
            p.y = figure.getCenter().y;
        }
    }    
    
    @Override
    public void readAttributes(Map prop){
        super.readAttributes(prop);
        Map ui = (Map)prop.get("ui");
        readPoints(ui);
        readStartDecoration(ui);
        readEndDecoration(ui);
    }
    
    
    @Override
    public Map toMap() {
        Map map = (Map)super.toMap();
        map.put("startFigureId", getStartFigure().getId() );
        map.put("endFigureId", getEndFigure().getId());
        return map;
    }    
    
    
    private void readPoints(Map prop){
        getPoints().clear();
        List<Point> points = DataUtil.decodePoints("points", prop);
        for(Point pt : points){
            addPoint(pt);
        }
    }

    private void readStartDecoration(Map prop) {
        String decorationClass = (String) prop.get("startDecoration");
        if (decorationClass != null){
            setStartDecoration((LineDecoration)DrawUtil.loadClass(decorationClass));
        }
    }

    private void readEndDecoration(Map prop) {
        String decorationClass = (String) prop.get("endDecoration");
        if (decorationClass != null){
            setEndDecoration((LineDecoration)DrawUtil.loadClass(decorationClass));
        }
    }
}
