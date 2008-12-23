/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linkbuild;

import javax.bluetooth.*;
import java.io.*;
import java.util.*;
import java.io.IOException;

/**
 *
 * @author sbrandollo
 */
public class Discovery {

    public static final Hashtable<String, Object> devicesDiscovered = new Hashtable<String, Object>();

    public Discovery() throws BluetoothStateException, InterruptedException
    {
        final Object inquiryCompletedEvent = new Object();
        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() 
        {
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) 
            {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.put(btDevice.getBluetoothAddress(), btDevice);
                try 
                {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) 
                {
                }
            }

            public void inquiryCompleted(int discType) 
            {
                System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) 
            {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) 
            {
            }
        };

        synchronized(inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                System.out.println("wait for device inquiry to complete...");
                inquiryCompletedEvent.wait();
                System.out.println(devicesDiscovered.size() +  " device(s) found");
            }
        }
    }
    public Hashtable getDiscovered()
    {
        return devicesDiscovered;
    }
}
