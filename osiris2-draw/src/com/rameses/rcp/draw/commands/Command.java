package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.KeyEvent;

public abstract class Command {
    private String name;
    private Canvas canvas;

    public Command(String name, Canvas canvas) {
        this.name = name;
        this.canvas = canvas;
    }
    
    public abstract void execute();
    
    public abstract boolean accept(KeyEvent e);
    
    public String getName(){
        return name;
    }
    
    public Canvas getCanvas(){
        return canvas;
    }
    
    public Drawing getDrawing(){
        return canvas.getDrawing();
    }
    
    public Editor getEditor(){
        return canvas.getEditor();
    }
    
    protected boolean isControlPressed(KeyEvent e) {
        return (e.getModifiers() & KeyEvent.CTRL_MASK) != 0;
    }
        
    protected boolean isAltPressed(KeyEvent e) {
        return (e.getModifiers() & KeyEvent.ALT_MASK) != 0;
    }

    @Override
    public int hashCode() {
        if (name == null){
            return this.hashCode();
        }
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        
        if (!(obj instanceof Command)){
            return false;
        }
        
        Command h = (Command)obj;
        return hashCode() == h.hashCode();
    }
    
}
