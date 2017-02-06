import java.io.File;

import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dell
 */
public class NewClass {
    public static void main(String[] args) {
        Map m = System.getProperties();
        for(Object k: m.keySet()) {
            System.out.println(k+"=" + m.get(k));
        }
        JFileChooser jf = new javax.swing.JFileChooser(); 
        
        /*
        
        jf.setFileFilter( new FileFilter(){
            public boolean accept(File f) {
                if( f.get)
            }

            @Override
            public String getDescription() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        */ 
    }
            
}
