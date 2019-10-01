/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.mach;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        //MachineInfo.getInstance().getInfo(); 
        
        System.out.println("** networks ** ");
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
        while ( en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement(); 
            if ( !ni.isUp() ) continue; 
            System.out.println("> "+ ni.getName() +": "+ ni.getDisplayName() );
            for (Enumeration ee = ni.getInetAddresses(); ee.hasMoreElements(); ) {
                InetAddress addr = (InetAddress) ee.nextElement();
                if ( addr instanceof Inet4Address ) {
                    Inet4Address a4 = (Inet4Address) addr; 
                    System.out.println( a4 );
                }
            }
        }
    }
}
