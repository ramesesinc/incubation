package com.rameses.rcp.draw.components;

import javax.swing.JMenu;

public class AttributeMenu extends JMenu {
    private final AttributePickerModel model;
    private final com.rameses.rcp.common.ComponentBean bean;

    public AttributeMenu(String text, final com.rameses.rcp.common.ComponentBean bean, final AttributePickerModel model) {
        super(text);
        setEnabled(true);
        this.bean = bean;
        this.model = model;
    }
}
