package test.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class TestLayout implements LayoutManager 
{    
    private int CELL_SIZE = 110;
    
    public void addLayoutComponent(String name, Component comp) {;}
    public void removeLayoutComponent(Component comp) {;}
    
    public Dimension getLayoutSize(Container parent) 
    {
        synchronized (parent.getTreeLock()) 
        {
            int w=0, h=0;
            Insets margin = parent.getInsets();
            return new Dimension(w+margin.left+margin.right, w+margin.top+margin.bottom);
        }
    }
    
    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }
    
    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }
    
    public void layoutContainer(Container parent) 
    {
        synchronized (parent.getTreeLock()) 
        {
            Insets margin = parent.getInsets();
            int x = margin.left, y=margin.top, pw=parent.getWidth(), ph=parent.getHeight();
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);
            
            Component[] comps = parent.getComponents();
            if (comps.length == 0) return;
            
            int colWidth = CELL_SIZE;
            int colHeight = CELL_SIZE;
            int colCount = w / colWidth;
            
            for (int i=0; i<comps.length; i++) 
            {
                Component comp = comps[i];
                Dimension dim = comp.getPreferredSize();
                System.out.println(dim);
                if (comp instanceof JComponent)
                {
                    JComponent jc = (JComponent) comp; 
                    View view = (View) jc.getClientProperty(BasicHTML.propertyKey); 
                    if (view != null) 
                    {
                        
                    }
                }
            }
        }
    }
}

