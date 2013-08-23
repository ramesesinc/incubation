/*
 * TableControl.java
 *
 * Created on January 31, 2011, 11:17 AM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.framework.Binding;
import java.awt.Color;
import javax.swing.table.TableModel;

public interface TableControl 
{
    String getId();
    String getName();
    String getVarName();
    
    Binding getBinding();
    TableModel getModel();
    AbstractListDataProvider getDataProvider(); 
    
    Object createExpressionBean(Object bean);
    
    public Color getEvenBackground();
    public Color getOddBackground();
    public Color getErrorBackground();
    public Color getEvenForeground();
    public Color getOddForeground();
    public Color getErrorForeground();    
}
