/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;

public class SendBackwardCommand extends Command{
    
    public SendBackwardCommand(Canvas canvas){
        super("sendbackward", canvas);
    }

    @Override
    public void execute() {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        for (Figure f : getDrawing().getSelections()){
            int idx = getDrawing().getFigures().indexOf(f);
            if (idx != 0){
                Figure tmpf = getDrawing().getFigures().get((idx - 1));
                getDrawing().getFigures().set((idx - 1), f);
                getDrawing().getFigures().set(idx, tmpf);
            }
        }
        
        getCanvas().refresh();
    }

    @Override
    public boolean accept(KeyEvent e) {
        return isControlPressed(e) && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN;
    }
    
}