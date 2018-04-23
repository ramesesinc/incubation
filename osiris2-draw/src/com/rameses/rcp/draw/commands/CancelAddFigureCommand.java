/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import java.awt.event.KeyEvent;

public class CancelAddFigureCommand extends Command{
    
    public CancelAddFigureCommand(Canvas canvas){
        super("camceladd", canvas);
    }

    @Override
    public void execute() {
        if (getEditor().getCurrentTool().isActive()){
            getEditor().getCurrentTool().cancel();
        }
    }

    @Override
    public boolean accept(KeyEvent e) {
        return (e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_ESCAPE);
    }
    
}