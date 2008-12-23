/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linkbuild.bluetooth;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.*;
import java.io.*;
import java.util.*;
import java.io.IOException;

/**
 *
 * @author sbrandollo
 */
public class Discovery implements DiscoveryListener
{
    public final Hashtable <String, Object> devices;
    /**
     * Constructor
     */
    public Discovery()
    {
         devices = new Hashtable<String, Object>();
         start_inquiry();
    }
        
    public synchronized void start_inquiry()
    {
        try
        {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
            if (started)
            {
                System.out.println("wait for device inquiry to complete...");
            }            
        }
        catch (BluetoothStateException ex)
        {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    /**
     * Method written by DiscoveryListener interfaces
     * This method will be invoked when the device has discovered
     */
    public void deviceDiscovered(RemoteDevice device, DeviceClass code)
    {
        try
        {
            System.out.println("Device " + device.getBluetoothAddress() + " found - " + device.getFriendlyName(true));
            devices.put(device.getBluetoothAddress(), device);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1)
    {
        
    }
    public void serviceSearchCompleted(int arg0, int arg1)
    {
        
    }
    public void inquiryCompleted(int arg0)
    {
        System.out.println(devices.size() + " device(s) found");
    }
    
    public Hashtable getDiscovered()
    {
        return devices;
    }
}