/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.KeyEvent;
import java.util.List;


public class DeleteCommand extends Command{
    
    public DeleteCommand(Canvas canvas){
        super("delete", canvas);
    }

    @Override
    public void execute() {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        boolean delete = getEditor().notifyBeforeRemoveListener(getDrawing().getSelections());
        if (delete){
            List<Figure> deletedItems = getEditor().deleteSelections();
            getEditor().notifyAfterRemoveListener(deletedItems);
        }
    }

    @Override
    public boolean accept(KeyEvent e) {
        return (e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_DELETE);
    }
    
}