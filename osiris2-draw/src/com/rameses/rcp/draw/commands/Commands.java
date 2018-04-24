package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;



// <editor-fold defaultstate="collapsed" desc="DeleteKeyHandler">

public class Commands {
    private List<Command> commands = new ArrayList<Command>();
    private Canvas canvas;
    
    public Commands(Canvas canvas){
        this.canvas = canvas;
    }
    
    public void buildCommands(){
        addCommand(new DeleteCommand(canvas));
        addCommand(new ArrangeCommand(canvas));
        addCommand(new CancelAddFigureCommand(canvas));
        addCommand(new MoveCommand(canvas));
    }
    
    public void addCommand(Command handler){
        if (!commands.contains(handler)){
            commands.add(handler);
        }
    }
    
    public Command getCommand(String name){
        for (Command c : commands){
            if (c.getName().equalsIgnoreCase(name)){
                return c;
            }
        }
        return null;
    }
    
    public void execute(KeyEvent e){
        for(Command handler : commands){
            if (handler.accept(e)){
                handler.execute();
            }
        }
    }
}

