/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.KeyStroke;


public class DeleteCommand extends Command{
    
    public DeleteCommand(Canvas canvas){
        super("draw_delete", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getDrawing().getSelections().isEmpty()){
            return;
        }
        
        boolean delete = getEditor().notifyBeforeRemoveListener(getDrawing().getSelections());
        if (delete){
            List<Figure> deletedItems = getEditor().deleteSelections();
            getEditor().notifyAfterRemoveListener(deletedItems);
        }
    }
    
}