/*
 * HomePage.java
 *
 * Created on August 16, 2013, 5:51 PM
 */

package system.home;

import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  wflores
 */
@Template(HomePageTemplate.class)
public class HomePage extends javax.swing.JPanel {
    
    public HomePage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xTileView1 = new com.rameses.rcp.control.XTileView();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout());

        xTileView1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xTileView1.setName("model"); // NOI18N
        xTileView1.setOpaque(false);
        xTileView1.setPadding(new java.awt.Insets(10, 20, 10, 0));
        add(xTileView1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XTileView xTileView1;
    // End of variables declaration//GEN-END:variables
    
}
