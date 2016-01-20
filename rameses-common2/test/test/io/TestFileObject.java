/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.io;

import com.rameses.io.AbstractChunkHandler;
import com.rameses.io.FileObject;
import java.io.File;
import javax.swing.JFileChooser;
import junit.framework.TestCase;

/**
 *
 * @author wflores
 */
public class TestFileObject extends TestCase {
    
    public TestFileObject(String testName) {
        super(testName);
    }
    
    public void test1() {
        JFileChooser jfc = new JFileChooser();
        int opt = jfc.showOpenDialog( null );
        if ( opt == JFileChooser.APPROVE_OPTION ) {
            System.out.println(jfc.getSelectedFile());
            
            File file = jfc.getSelectedFile();
            FileObject fo = new FileObject( file ); 
            fo.read( new AbstractChunkHandler() {

                public boolean isAutoComputeTotals() {
                    return true; 
                }

                public void start() {
                    System.out.println("--- start ---");
                    System.out.println("id="+ getMeta().getId());
                    System.out.println("filename="+ getMeta().getFileName());
                    System.out.println("filetype="+ getMeta().getFileType());
                    System.out.println("ChunkCount="+ getMeta().getChunkCount());
                    System.out.println("FileSize="+ getMeta().getFileSize());
                }

                public void end() {
                    System.out.println("--- end ---");
                    System.out.println("ChunkCount="+ getMeta().getChunkCount());
                    System.out.println("FileSize="+ getMeta().getFileSize());
                }

                public void handle(int indexno, byte[] bytes) {
                    System.out.println(indexno + "-> " + bytes);
                }
            });
        }
    }
}
