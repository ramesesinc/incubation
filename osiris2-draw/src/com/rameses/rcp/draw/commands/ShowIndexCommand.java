/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;
import java.util.List;


public class ShowIndexCommand extends Command{
    
    public ShowIndexCommand(Canvas canvas){
        super("showindex", canvas);
    }

    @Override
    public void execute() {
        for (Figure f : getDrawing().getFigures()){
            f.toggleShowIndex();
        }
        getCanvas().refresh();
        
    }

    @Override
    public boolean accept(KeyEvent e) {
        return (isAltPressed(e) && e.getKeyCode() == KeyEvent.VK_I);
    }
    
}