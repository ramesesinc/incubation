/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.io;

import com.rameses.io.ChunkHandler;
import com.rameses.io.FileChunker;
import java.io.File;
import javax.swing.JFileChooser;
import junit.framework.TestCase;

/**
 *
 * @author wflores
 */
public class TestFileChunker extends TestCase {
    
    public TestFileChunker(String testName) {
        super(testName);
    }
    
    public void test1() {
        JFileChooser jfc = new JFileChooser();
        int opt = jfc.showOpenDialog( null );
        if ( opt == JFileChooser.APPROVE_OPTION ) {
            System.out.println(jfc.getSelectedFile());
            
            File file = jfc.getSelectedFile();
            FileChunker fc = new FileChunker( file );
            fc.parse( new ChunkHandler() {
                public void start() {}
                public void end() {}
                
                public void handle(int indexno, byte[] bytes) {
                    System.out.println("indexno="+ indexno + ", bytes="+ bytes);
                }
            });
        }
    }
}
