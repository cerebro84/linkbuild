package com.jsoft.linkbuild.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;


/**
 * This class must be extendend by Client to use connectToServer method and to seceive data
 * @author Angelo Giuseppe De Michele
 */
public abstract class ClientLibrary 
        extends MIDlet 
        implements DiscoveryListener, CommandListener

{   
    //Constants
    private static final String RECORDSTORE = "Search";
    private static final int RECORDSNUMBER = 1;
    
    // These must be equal to the ones in linkBuildServer.ConnectionServer
    public final static byte BOOLEAN            =  1; //has be sent before sending a boolean
    public final static byte STRING             =  2; //has be sent before sending a string
    public final static byte VECTORSTRING       =  3; //has be sent before sending a vector of Strings
    public final static byte INT                =  4; //has be sent before sending an int
    public final static byte BYTE               =  5; //has be sent before sending a byte
    public final static byte CHAR               =  6; //has be sent before sending a char
    public final static byte BYTES              =  7; //has be sent before vector of bytes;

    public final static byte HELLO              =  11; //Sent by client to login
    public final static byte AUTH_OK            =  12; //Sent by server to notify login success
    public final static byte AUTH_FAILED        = -12; // Sent by server to notify login failure
    public final static byte APP_NOT_AVAILABLE  = -13; // Sent by server to notify that the requested app is not available on this server
    public final static byte PING_DISCONNECTION = -14;  //Sent by server to notify a client that it is being disconnected because of its latency
    public final static byte GOODBYE            =  19;   //Sent by client to disconnect
    public final static byte SERVER_MESSAGE     =  20; // Sent by server to send a custom message
    
    private final static boolean VERBOSE = true; //debugging purpose
    
    //Streams
    private StreamConnection conn;
    private DataInputStream din = null;
    private DataOutputStream dout;
    
    //GUI
    private Display display;
    private Displayable previous;
    private LocalDevice local;
    private Vector devices, services;
    private List dev_list;
    private DiscoveryAgent agent;
    private Command back,refresh, ok;
    private Form mainForm;
    TextField serverName;
    
    //For search
    private int tries = 0;
    private int currentDevice = 0;       //used as an indicator to the device queried for 
    
    //For status
    private boolean connected = false;

    /**
     * Called by actual client to let library connect
     * It shows a list of found servers and logs into the selected one
     */
    public void connectToServer (Displayable prev)
    {
        try 
        {
            dev_list = new List("Select Device", List.IMPLICIT); //the list of devices;
            back = new Command("Back", Command.BACK, 1);
            refresh = new Command("Refresh", Command.ITEM, 1);
            ok = new Command("OK", Command.OK, 1);
            mainForm = new Form("Server search");
            serverName = new TextField("Server name:", "", 15, TextField.ANY);
            
            try 
            {
                RecordStore loginData = RecordStore.openRecordStore(RECORDSTORE, false);
                serverName.setString(new String(loginData.getRecord(1)));
                loginData.closeRecordStore();
            } 
            catch (RecordStoreException ex) {} 
            mainForm.append(serverName);
            mainForm.addCommand(ok);
            local = LocalDevice.getLocalDevice();
            agent = local.getDiscoveryAgent();
            mainForm.setCommandListener(this);
            dev_list.addCommand(back);
            dev_list.addCommand(refresh);
            dev_list.setCommandListener(this);
            display = Display.getDisplay(this);
            display.setCurrent(mainForm);
            previous = prev;
        } 
        catch (BluetoothStateException ex) 
        {
            do_alert(ex.toString(),4000);
        }
     }
    
    /**
     * Called to disconnect cleanly
     * @throws java.io.IOException if is impossible to communicate with server
     */
    public void disconnect () throws IOException
    {
        dout.writeByte(GOODBYE);
        dout.flush();
    }

    void close() 
    {
        try 
        {
            this.destroyApp(true);
            this.notifyDestroyed();
        } 
        catch (MIDletStateChangeException ex) {}
    }
    
    /**
     * Used by connectToServer to initiate search
     */
    private void findDevices()
    {
        try
        {
            Display.getDisplay(this).setCurrent(dev_list);
            dev_list.setTitle("Searching server");
            dev_list.deleteAll();
            devices = new java.util.Vector();
            LocalDevice local    = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = local.getDiscoveryAgent();
            agent.startInquiry(DiscoveryAgent.GIAC,this);
        }
        catch(Exception e)
        {
            this.do_alert("Error in initiating search" , 4000);
        }
    }
    
    /**
     * Called by agent when a device is discovered
     * @param remoteDevice is the discovered device
     * @param deviceClass is its class
     */
    public void deviceDiscovered(RemoteDevice remoteDevice,DeviceClass deviceClass) 
    {
        try {
            if (remoteDevice.getFriendlyName(false).toLowerCase().equals(serverName.getString().toLowerCase())) 
            {
                devices.addElement(remoteDevice);
                agent.cancelInquiry(this);
                
            }
        } catch (IOException ex) 
        {
            do_alert (ex.toString(),4000);
        }
        // devices.addElement(remoteDevice);
    }
    
    /**
     * Called by agent when a service has been discovered
     * @param transID
     * @param serviceRecord
     */
    public void servicesDiscovered(int transID,ServiceRecord[] serviceRecord) 
    {
        services = new java.util.Vector();
        for (int x = 0; x < serviceRecord.length; x++)
            services.addElement(serviceRecord[x]);
        try
        {
            dev_list.append(((RemoteDevice)devices.elementAt(currentDevice)).getFriendlyName(false),null);
        }
        catch(Exception e)
        {   
            this.do_alert("Error in initiating search" , 4000);
        }
    }
    
    /**
     * Called by agent when service search completed; if Server has been found, it comes shown
     * @param transID
     * @param respCode
     */
    public void serviceSearchCompleted(int transID, int respCode) 
    {
        if (currentDevice == devices.size() - 1) 
        {
            display.setCurrent(dev_list);
            commandAction(List.SELECT_COMMAND, dev_list);
        }
        dev_list.setTitle("Finished");
        switch (respCode)
        { 
        /*case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
        {
            display.getCurrent().setTitle("Finished");
            return;
        }*/
        case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE: //Il servizio è probabilmente occupato e ritentiamo
            if (tries < 5)
            {
                try 
                {
                    Thread.sleep(1000);
                } 
                catch (InterruptedException ex) {}
                this.findServices((RemoteDevice) devices.elementAt(currentDevice));
                tries++;
            }
            else 
            {
                tries = 0;
                currentDevice++;
                this.findServices((RemoteDevice)devices.elementAt(currentDevice));
            }
        /*default:
            if(currentDevice == devices.size() -1)
            {
                display.getCurrent().setTitle("Finished");
                return;
            }
            else 
            {                               //search next device
                currentDevice++;
                this.findServices((RemoteDevice)devices.elementAt(currentDevice));    
            }*/
        }
    }

    /**
     * Called by agent when inquiry is completed
     * @param param is completion code
     */
    public void inquiryCompleted(int param)
    {
    switch (param) 
    {
        case DiscoveryListener.INQUIRY_COMPLETED:    //Inquiry completed normally
            if (devices.size() > 0)
            {                 //Atleast one device has been found
                services = new java.util.Vector();
                this.findServices((RemoteDevice)
                         devices.elementAt(0));     //Check if the first device offers the service
            }
            else
                dev_list.setTitle("No devices found");
        break;
        case DiscoveryListener.INQUIRY_ERROR:       // Error during inquiry
            this.do_alert("Inquiry error" , 4000);
        break;
        case DiscoveryListener.INQUIRY_TERMINATED:  // Inquiry terminated by agent.cancelInquiry()
             findServices((RemoteDevice)devices.elementAt(0));
        break;
       }
}
    /**
     * Calls discoveryAgent to search for services
     * @param device is the RemoteDevice to query
     */
    private void findServices(RemoteDevice device){
    try
    {
        UUID[] uuids  = new UUID[1];
        uuids[0]      = new UUID("37012f0c68af4fbf8dbe6bbaf7aa4300",false);    //The UUID of the LinkBuild service
        
       
        agent.searchServices(null,uuids,device,this);            
    }
    catch(Exception e)
    {
        this.do_alert("Error in initiating search" , 4000);
    }
}
    
    /**
     * Used to show messages
     * @param msg is the message
     * @param time_out is the duration of the popup
     */
    protected void do_alert(String msg,int time_out)
    {
        Alert alert = new Alert("Link & Build");
        alert.setString(msg);
        alert.setTimeout(time_out);
        Display.getDisplay(this).setCurrent(alert);
    }
    
      
    /**
     * Called when a GUI event happens
     * @param com is the element who triggered the action
     * @param dis is the display where action happened
     */
    public void commandAction(Command com, Displayable dis) 
    {
        if (com == ok)
        {
            try 
            {
                RecordStore loginData = RecordStore.openRecordStore(RECORDSTORE, true);
                for (int i = 0; i < (RECORDSNUMBER+1 - loginData.getNumRecords()); i++)
                {
                    loginData.addRecord(new byte[]{0}, 0, 0);
                }
                loginData.setRecord (1, serverName.getString().getBytes(), 0, serverName.getString().getBytes().length);
                loginData.closeRecordStore();
            }
            catch (RecordStoreException ex) {}
            findDevices ();
        }
        if (com == back)
        {
            notifyConnectionFailed("Cancelled by user");
        }
        else if (com == refresh)
        {
            findDevices();
        }
        else if (com == List.SELECT_COMMAND)
        {
            // Server was choose, so we connect
            conn = null;
            ServiceRecord service = (ServiceRecord)services.elementAt(dev_list.getSelectedIndex());
            String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            try
            {
                conn = (StreamConnection) Connector.open(url);       //establish the connection
            }
            catch (IOException e)
            {
                this.do_alert("Error opening StreamConnection: " + e.toString(), 4000);
            }
            try
            {
                dout = conn.openDataOutputStream();//Get the output stream
            }
            catch (IOException e)
            {
                this.do_alert("Error opening DataOutputStream: " + e.toString(), 4000);
            }
            //Identifies
            identifyMyself();
            display.setCurrent(previous);
        }
        
    }
    /**
     * Method to start simple login protocol
     * @return true if logged in, false otherwise
     */
    private boolean identifyMyself()
    {
        try 
        {
            (new Thread(new ReceiverThread())).start();//Esegui il thread ricevente
            dout.writeByte(HELLO);
            dout.writeUTF(this.getClass().getName());            
            connected = true;
            if (getConnectionParameters().length > 0)
                send(getConnectionParameters());
            dout.flush();
            return true;
        } 
        catch (IOException ex) 
        {
            notifyDisconnection("I/O Error");
            return false;
        }
        catch (Exception ex)
        {
            notifyDisconnection(ex.toString());
            return false;
        }
    }
    
     /**
     * Called by client to send an array of Strings. 
     * @param array is the array to send
     * @throws java.io.IOException
     */
    protected void send(String [] array) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(VECTORSTRING);
        dout.writeInt(array.length);
        for (int i = 0; i < array.length; i++)
        {
            dout.writeUTF(array[i]);
        }
        dout.flush();
    }
    
    /**
     * Called by client to send an int. 
     * @param toSend is the int to send
     * @throws java.io.IOException
     */
    protected void send (int toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(INT);
        dout.writeInt(toSend);
        dout.flush();
    }
    
    /**
     * Called by client to send a single byte. 
     * @param toSend is the byte to send
     * @throws java.io.IOException
     */
    protected void send (byte toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(BYTE);
        dout.writeByte(toSend);
        dout.flush();
    }
    
    /**
     * Called by client to send an array of chars. 
     * @param toSend is the char to send
     * @throws java.io.IOException
     */
    protected void send (char toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(CHAR);
        dout.writeChar(toSend);
        dout.flush();
    }
    
    /**
     * Called by client to send a String. 
     * @param toSend is the String to send
     * @throws java.io.IOException
     */
    protected void send (String toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(STRING);
        dout.writeUTF(toSend);
        dout.flush();
    }
    
    /**
     * Called by client to send a boolean. 
     * @param toSend is the boolean to send
     * @throws java.io.IOException
     */
    protected void send (boolean toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(BOOLEAN);
        dout.writeBoolean(toSend);
        dout.flush();
    }
    
    /**
     * Called by client to send an array of bytes. 
     * @param toSend is the array of bytes to send
     * @throws java.io.IOException
     */
    protected void send (byte[] toSend) throws IOException
    {
        if (!connected)
        {
            do_alert ("Cannot send without connection",3000);
            return;
        }
        dout.writeByte(BYTES);
        dout.writeInt(toSend.length);
        dout.write(toSend, 0, toSend.length);
        dout.flush();
    }
    
    /**
     * This method is called by library to notify reception of a String Array
     * @param ret is the String Array to notify
     */
    abstract void notifyReceived(String[] ret);
    
   /**
    * This method is called by library to notify reception of an int
    * @param ret is the int to notify
    */
    abstract void notifyReceived(int ret);
    
    /**
     * This method is called by library to notify reception of a byte
     * @param byte is the byte to notify
     */
    abstract void notifyReceived(byte ret);
    
    /**
     * This method is called by library to notify reception of a char
     * @param ret is the char to notify
     */
    abstract void notifyReceived(char ret);
    
    /**
     * This method is called by library to notify reception of a String
     * @param ret is the String to notify
     */
    abstract void notifyReceived(String ret);
    
    /**
     * This method is called by library to notify reception of a boolean
     * @param ret is the boolean to notify
     */
    abstract void notifyReceived(boolean ret);
    
    /**
     * This method is called by library to notify reception of a byte Array
     * @param ret is the byte Array to notify
     */
    abstract void notifyReceived(byte[] ret);
    
    /**
     * This method is called by library to notify disconnection
     * @param why is the reason of disconnection
     */
    abstract void notifyDisconnection(String why);
    
    /**
     * This method is called by library to notify connection
     */
    abstract void notifyConnection();
    
    /**
     * This method is called by library to notify reception of a String Array
     * @param why is the reasone because connection failed
     */
    abstract void notifyConnectionFailed(String why);
    
    /**
     * This method return connection parameters. If array dimension is equal to 0, libray will suppose registration service isn't needed.
     * @return A string array containing connection parameters or empty if no registration is needed
     */
    abstract String [] getConnectionParameters();

    /**
    * Takes care of interpreting the receiving stream and send the received messages to the notification methods
    */
    class ReceiverThread implements Runnable
    {
        private boolean cont = true;
        
        /**
         * Class constructor
         */
        public ReceiverThread() {}

        /**
         * Method to start thread
         */
        public void run() 
        {
            try 
            {
                din = conn.openDataInputStream();
                while (cont) 
                {
                    byte b;
                    if ((b = din.readByte()) != 0)
                    {
                        if (b == INT)
                        {
                            notifyReceived(din.readInt());
                            continue;
                        }
                        else if (b == BYTE)
                        {
                            notifyReceived(din.readByte());
                            continue;
                        }
                        else if (b == CHAR)
                        {
                            notifyReceived(din.readChar());
                            continue;
                        }
                        else if (b == BOOLEAN)
                        {
                            notifyReceived(din.readBoolean());
                            continue;
                        }
                        else if (b == STRING)
                        {
                            notifyReceived(din.readUTF());
                            continue;
                        }
                        else if (b == BYTES)
                        {
                            byte [] ret = new byte [din.readInt()];
                            din.read(ret, 0, ret.length);
                            notifyReceived(ret);
                            continue;
                        }    
                        else if (b == VECTORSTRING)
                        {
                            String [] ret = new String [din.readInt()];
                            for (int i = 0; i < ret.length; i++)
                            {
                                ret[i] = din.readUTF();
                            }
                            notifyReceived(ret);
                            continue;
                        }
                        
                        else if (b == AUTH_OK)
                        {
                            connected = true;
                            // display.setCurrent(previous);
                            notifyConnection();
                            continue;
                        }
                        else if (b == APP_NOT_AVAILABLE)
                        {
                            notifyConnectionFailed ("Application not available on this server");
                            break;
                        }
                        else if (b == AUTH_FAILED)
                        {
                            notifyConnectionFailed ("Login error");
                            cont = false;
                            break;
                        }
                        else if (b == SERVER_MESSAGE)
                        {
                            String msg = din.readUTF();
                            do_alert(msg, 4000);
                            continue;
                        } 
                        else if (b == PING_DISCONNECTION)
                        {
                            notifyDisconnection("Ping timeout");
                            cont = false;
                            break;
                        }
                    }
                }
            } 
            catch (IOException ex) 
            {
                cont = false;
                notifyDisconnection("Link down");
            } 
        }
    }   
}
