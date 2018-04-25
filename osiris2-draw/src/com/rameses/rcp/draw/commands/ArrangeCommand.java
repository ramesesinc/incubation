/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;

public class ArrangeCommand extends Command{
    private ArrangeDirection direction;
    
    public ArrangeCommand(Canvas canvas){
        super("arrange", canvas);
    }

    @Override
    public void execute() {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        switch(direction){
            case FORWARD:
                bringForward();
                break;
            case TOFRONT:
                bringToFront();
                break;
            case BACKWARD:
                sendBackward();
                break;
            case TOBACK:
                sendToBack();
                break;
        }
        getCanvas().refresh();
    }

    @Override
    public boolean accept(KeyEvent e) {
        boolean accept = false;
        
        if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_PAGE_UP){
            direction = ArrangeDirection.FORWARD;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_HOME){
            direction = ArrangeDirection.TOFRONT;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN){
            direction = ArrangeDirection.BACKWARD;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_END){
            direction = ArrangeDirection.TOBACK;
            accept = true;
        }
        return accept;
    }

    private void bringForward() {
        for (Figure f : getDrawing().getSelections()){
            int idx = getDrawing().getFigures().indexOf(f);
            if (idx < getDrawing().getFigures().size() - 1){
                Figure tmpf = getDrawing().getFigures().get((idx + 1));
                getDrawing().getFigures().set((idx + 1), f);
                getDrawing().getFigures().set(idx, tmpf);
            }
        }
    }

    private void bringToFront() {
        for (int i = 0; i < getDrawing().getSelections().size(); i++){
            Figure f = getDrawing().getSelections().get(i);
            
            //get index from figures and remove 
            int idx = getDrawing().getFigures().indexOf(f);
            Figure tmpf = getDrawing().getFigures().remove(idx);
            
            //insert in current i location
            getDrawing().getFigures().add(tmpf);
        }
    }

    private void sendBackward() {
        for (Figure f : getDrawing().getSelections()){
            int idx = getDrawing().getFigures().indexOf(f);
            if (idx != 0){
                Figure tmpf = getDrawing().getFigures().get((idx - 1));
                getDrawing().getFigures().set((idx - 1), f);
                getDrawing().getFigures().set(idx, tmpf);
            }
        }
    }

    private void sendToBack() {
        for (int i = 0; i < getDrawing().getSelections().size(); i++){
            Figure f = getDrawing().getSelections().get(i);
            
            //get index from figures and remove 
            int idx = getDrawing().getFigures().indexOf(f);
            Figure tmpf = getDrawing().getFigures().remove(idx);
            
            //insert in current i location
            getDrawing().getFigures().add(i, tmpf);
        }
    }

    
    public enum ArrangeDirection{
        FORWARD, TOFRONT, BACKWARD, TOBACK
    }
}


