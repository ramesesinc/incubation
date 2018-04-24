/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;

public class MoveCommand extends Command{
    private MoveDirection direction;
    
    public MoveCommand(Canvas canvas){
        super("move", canvas);
    }

    @Override
    public void execute() {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        int dx = 0;
        int dy = 0;
        
        switch(direction){
            case NORTH:
                dy -= 1;
                break;
            case SOUTH:
                dy += 1;
                break;
            case EAST:
                dx += 1;
                break;
            case WEST:
                dx -= 1;
                break;
        }
        
        for (Figure f : getDrawing().getSelections()){
            if (!(f instanceof Connector )){
                f.moveBy(dx, dy, null);
            }
        }
        
        getCanvas().refresh();
    }

    @Override
    public boolean accept(KeyEvent e) {
        boolean accept = false;
        
        if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_UP){
            direction = MoveDirection.NORTH;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_DOWN){
            direction = MoveDirection.SOUTH;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_RIGHT){
            direction = MoveDirection.EAST;
            accept = true;
        } else if (isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_LEFT){
            direction = MoveDirection.WEST;
            accept = true;
        }
        return accept;
    }

    
    public enum MoveDirection{
        NORTH, SOUTH, EAST, WEST
    }
}


