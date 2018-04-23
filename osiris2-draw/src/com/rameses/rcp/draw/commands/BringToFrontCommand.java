/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;

public class BringToFrontCommand extends Command{
    
    public BringToFrontCommand(Canvas canvas){
        super("bringtofront", canvas);
    }

    @Override
    public void execute() {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        for (int i = 0; i < getDrawing().getSelections().size(); i++){
            Figure f = getDrawing().getSelections().get(i);
            
            //get index from figures and remove 
            int idx = getDrawing().getFigures().indexOf(f);
            Figure tmpf = getDrawing().getFigures().remove(idx);
            
            //insert in current i location
            getDrawing().getFigures().add(tmpf);
        }
        
        getCanvas().refresh();
    }

    @Override
    public boolean accept(KeyEvent e) {
        return isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_HOME;
    }
    
}