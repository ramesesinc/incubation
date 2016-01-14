/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

/**
 *
 * @author dell
 */
public class OrderField {
    
    private String name;
    private String direction = "ASC";
    
    
    public String toString() {
        return name +":"+direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    
}
