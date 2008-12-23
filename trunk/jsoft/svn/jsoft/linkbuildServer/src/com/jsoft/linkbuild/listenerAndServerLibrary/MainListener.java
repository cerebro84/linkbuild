/**
 * This class takes care of starting listening and managing tray icon
 * @author: Angelo Giuseppe De Michele
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;


public class MainListener
{
    //Helper variables
    private boolean started = false;
    private static MainListener instance = null;
    private boolean showComics = true;
    
    //GUI variables
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem startItem, exitItem, refreshItem, comicItem;
    
    /**
     * Calls initialization methods
     */
    private MainListener() 
    {
        prepareTray();
        setDeviceOn();
    }
    
    /**
     * This method instantiate a new SettingsManager or returns
     * the existing one if it has already been created.
     * @return the SettingsManager
     */
    public static MainListener getInstance() 
    {
      if(instance == null) 
      {
         instance = new MainListener();
      }
      return instance;
    }
    
    /** 
     * Tries to start bluetooth listening
     * @return true if succeded, false otherwise
     */
    private boolean setDeviceOn ()
    {
        try 
        {
            System.out.println("Setting device to be discoverable...");
            LocalDevice local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            ConnectionServer.getInstance().startNewListenerThread();
            started = true;
            if (trayIcon != null) //TrayIcon may not be supported
            {
                trayIcon.setToolTip("LinkBuild - started. Server name: " + local.getFriendlyName());
                startItem.setLabel("Stop Server");
                trayIcon.displayMessage("Link & Build", "Server has been started successfully. \nServer name is: " + local.getFriendlyName(), TrayIcon.MessageType.INFO);
            }
            else
            {
                System.out.println ("LinkBuild started successfully on server " + local.getFriendlyName());
            }
            return true;
        } 
        catch (BluetoothStateException ex) 
        {
            if (trayIcon != null)
                trayIcon.displayMessage("Link & Build error", "An error occurred while trying to set bluetooth on; is your device connected?", TrayIcon.MessageType.ERROR);
            else
                System.out.println ("LinkBuild server not started");
            return false;
        }
    }
    
    /**
     * Stops service
     * @return exit state
     */
    private boolean setDeviceOff()
    {
        boolean res = true;
        res = ThreadManager.onClosing() && res; 
        res = SettingsManager.onClosing() && res;
        res = ConnectionServer.onClosing() && res;
        res = BanManager.onClosing() && res;
        res = RegistrationManager.onClosing() && res;
        res = LogManager.onClosing() && res;
        try 
        {
            LocalDevice local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
        } 
        catch (BluetoothStateException ex) {}
        
        
        started = false;
        if (trayIcon != null) //TrayIcon may not be supported
            {
                trayIcon.setToolTip("LinkBuild - stopped.");
                startItem.setLabel("Start Server");
                trayIcon.displayMessage("Link & Build", "Server has been stopped successfully.", TrayIcon.MessageType.INFO);
            }
            else
            {
                System.out.println ("LinkBuild stopped successfully");
            }
        return res;
    }
    /** 
     * Prepares system tray
     */
    private void prepareTray() 
    {
        
        if (SystemTray.isSupported()) 
        {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("Images/trayIcon.png");
            ActionListener exitListener = new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    (new Timer()).schedule(new exitCheck(), 10000);
                    int exitCode = 0;
                    if (started)
                        if (!setDeviceOff()) exitCode = -1;
                    System.exit(exitCode);
                }
            };
            ActionListener startListener = new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    if (!started) setDeviceOn();
                    else setDeviceOff();
                }
            };
            ActionListener refreshListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    SettingsManager.getInstance().reloadConfiguration();
                }
            };
            ActionListener comicListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    showComics = !showComics;
                    if (showComics)
                        comicItem.setLabel("Disable comics");
                    else
                        comicItem.setLabel("Enable comics");
                }
            };
            popup = new PopupMenu();
            exitItem = new MenuItem("Exit");
            startItem = new MenuItem("Start server");
            refreshItem = new MenuItem ("Reload configuration");
            comicItem = new MenuItem ("Disable comics");
            exitItem.addActionListener(exitListener);
            startItem.addActionListener(startListener);
            refreshItem.addActionListener(refreshListener);
            comicItem.addActionListener(comicListener);
            popup.add(exitItem);
            popup.insert(startItem,0);
            popup.insert(refreshItem,1);
            popup.insert(comicItem,2);
            trayIcon = new TrayIcon(image, "LinkBuild - not started", popup);
            trayIcon.setImageAutoSize(true);            
            try 
            {
                tray.add(trayIcon);
            } 
            catch (AWTException e) 
            {
                System.err.println("TrayIcon could not be added.");
            }
        } 
        else 
        {
        //  System Tray is not supported
        System.out.println ("System tray is not supported");
        }
    }
    
    /**
     * This method displays a message through trayIcon or Sys.out if not supported
     * @param message is the message to display
     * @param isError has to be set true if message is an error, false if it's an information.
     */
    protected void displayTrayMessage (String message, boolean isError)
    {
        if (trayIcon != null && showComics) //TrayIcon may not be supported
        {
            MessageType type;
            if (isError) type = TrayIcon.MessageType.ERROR;
                else type = TrayIcon.MessageType.INFO;
            trayIcon.displayMessage("Link & Build", message, type);
        }
        else
        {
            if (isError)
                System.err.println (message);
            else
                System.out.println (message);
        }
    }
    /**
     * This class takes care of making sure that we exit even if some service doesn't stop correctly
     */
    private class exitCheck extends TimerTask
    {
        /**
         * Runs the timer
         */
        public void run ()
        {
            System.exit(-2);
        }
    }
}
    
   

