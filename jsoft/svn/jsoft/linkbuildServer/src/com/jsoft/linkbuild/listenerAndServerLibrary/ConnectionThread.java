/**
 *
 * @author cerebro84
 * This class manages sending and receiving data from and to devices
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ConnectionThread extends Thread
{
    //Constants for communication protocol
    public final static byte BOOLEAN            = 1;    //Sent before sending a boolean
    public final static byte STRING             = 2;    //Sent before sending a string
    public final static byte VECTORSTRING       = 3;    //Sent before sending a String Array
    public final static byte INT                = 4;    //Sent before sending an int
    public final static byte BYTE               = 5;    //Sent before sending a byte
    public final static byte CHAR               = 6;    //Sent before sending a char
    public final static byte BYTES              = 7;    //Sent before sending a Byte Array

    public final static byte HELLO              = 11;   //Sent by client to request login
    public final static byte AUTH_OK            = 12;   //Sent by server if authentication succeded
    public final static byte AUTH_FAILED        = -12;  //Sent by server if authentication failed
    public final static byte APP_NOT_AVAILABLE  = -13;  //Sent by server if requested application is not available
    public final static byte PING_DISCONNECTION = -14;  //Sent by server to notify a client that it is being disconnected because of its latency
    public final static byte GOODBYE            = 19;   //Sent by server/client to disconnect
    
    public final static byte SERVER_MESSAGE     = 20;   //Sent by server to send a custom message
    
    //Debugging constants
    private final static boolean VERBOSE        = ConnectionServer.VERBOSE; //Debug
    
    //Connection variables
    private final UUID uuid = new UUID("37012f0c68af4fbf8dbe6bbaf7aa4300", false); //the uid of the Link & Build service, it has to be unique,
    private final String name = "LinkBuild Server";                       //the name of the service
    private final String url  =  "btspp://localhost:" + uuid         //the service url
                                                    + ";name=" + name 
                                                    + ";authenticate=false;encrypt=false;";
    private StreamConnectionNotifier server = null;
    private StreamConnection conn = null;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    
    //Helper variables
    protected boolean canContinue = false;
    private SettingsManager sm;
    private String devName, devAddr;
    private boolean authenticated = false; //for the timer to know if is necessary to get user out
    private boolean cont = true; //for the main loop
    private boolean disconnecting = false; //this has to be set if we are already disconnecting
    private Timer timerPing;
    
    /**
     * Create a new ConnectionThread in listening mode
     */
    protected ConnectionThread () {}
    
    /**
     * This method runs the main loop, waiting for a connection and receiving data
     */
    public void run() 
    {
        try {
            sm = SettingsManager.getInstance();
            try {
                System.out.println("Start advertising service...");
                server = (StreamConnectionNotifier) Connector.open(url);
                System.out.println("Waiting for incoming connection... from thread " + Thread.currentThread().getName());
                conn = server.acceptAndOpen();
                RemoteDevice dev = RemoteDevice.getRemoteDevice(conn);
                devName = dev.getFriendlyName(true);
                devAddr = dev.getBluetoothAddress();
                if (VERBOSE) {
                    System.out.println(devName + " Connected... client has got 30 seconds to login");
                }
                (timerPing = new Timer()).schedule(new loginCheck(), 30000);
                ConnectionServer.getInstance().startNewListenerThread();
                din = new DataInputStream(conn.openInputStream());
            } catch (EOFException ex) {
                if (VERBOSE) {
                    System.out.println(Thread.currentThread().getName() + ": Client disconnected, closing thread");
                }
                cont = false;
                if (VERBOSE) {
                    System.out.println("EOF");
                }
            } catch (IOException ex) {}
            while (cont) {
                try {
                    byte cmd;
                    // char c;
                    if ((cmd = din.readByte()) > 0) {
                        if (VERBOSE) {
                            System.out.println("Received " + cmd + " from " + devName);
                        }
                        if (cmd == HELLO) {
                            if (!SayHello()) 
                            {
                                if (canContinue)
                                    ConnectionServer.getInstance().removeConnection(devAddr, ConnectionServer.REGISTRATION);
                                else
                                    disconnect();
                            }
                        } else if (cmd == BOOLEAN) {
                            ConnectionServer.getInstance().receive(din.readBoolean(), devAddr);
                        } else if (cmd == STRING) {
                            if (VERBOSE) {
                                System.out.println("Ricevo una stringa da " + devAddr);
                            }
                            ConnectionServer.getInstance().receive(din.readUTF(), devAddr);
                        } else if (cmd == VECTORSTRING) {
                            String[] data = new String[din.readInt()];
                            for (int i = 0; i < data.length; i++) {
                                data[i] = din.readUTF();
                            }
                            ConnectionServer.getInstance().receive(data, devAddr);
                        } else if (cmd == INT) {
                            ConnectionServer.getInstance().receive(din.readInt(), devAddr);
                        } else if (cmd == BYTE) {
                            ConnectionServer.getInstance().receive(din.readByte(), devAddr);
                        } else if (cmd == CHAR) {
                            ConnectionServer.getInstance().receive(din.readChar(), devAddr);
                        } else if (cmd == BYTES) {
                            byte[] msg = new byte[din.readInt()];
                            din.read(msg, 0, msg.length);
                            ConnectionServer.getInstance().receive(msg, devAddr);
                        } else if (cmd == GOODBYE) {
                            if (VERBOSE) System.out.println ("CT: Disconnessione pulita");
                            ConnectionServer.getInstance().removeConnection(devAddr, ConnectionServer.NORMAL);
                        }
                    }
                } catch (EOFException ex) {
                    if (VERBOSE) {
                        System.out.println(Thread.currentThread().getName() + ": Client disconnected, closing thread");
                    }
                    cont = false;
                    if (VERBOSE) {
                        System.out.println("EOF");
                    }
                    if (!disconnecting) ConnectionServer.getInstance().removeConnection(devAddr, ConnectionServer.ABNORMAL);
                    break;
                } catch (IOException ex) {
                    LogManager.getInstance().makeLog(this.getClass(), "", "Input/Output error on link");
                }
            }
            //This methods will be called only when cont == false
            disconnecting = true;
            if (dout != null) {
                dout.close();
            }
            if (din != null) {
                din.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
            } 
            
            
    


    /**
     * This method sends a String Array to the connected device
     * @param data is the String Array to send
     * @return true if data has been sent
     */
    protected boolean send (String [] data) throws IOException
    {
        dout.writeByte(VECTORSTRING);
        dout.writeInt(data.length);
        for (int i = 0; i < data.length; i++)
        {
            dout.writeUTF(data[i]);
        }
        return true;
    }
        
    /**
    * This method sends a Byte Array to the connected device
    * @param data is the Byte Array to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (byte[] data) throws IOException
    {
        dout.writeByte(BYTES);
        dout.writeInt(data.length);
        dout.write(data, 0, data.length);
        return true;
    }  
    
    /**
    * This method sends an int Array to the connected device
    * @param data is the int to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (byte data) throws IOException
    {
        if (VERBOSE) System.out.println ("Sending byte: "+data);
        dout.writeByte(BYTE);
        dout.writeByte(data);
        dout.flush();
        return true;
    } 
    
    /**
    * This method sends an int Array to the connected device
    * @param data is the int to send
    * @return true if data was sent, false otherwise
    */
    protected boolean write (byte data) throws IOException
    {
        if (VERBOSE) System.out.println ("Sending raw byte: "+data);
        dout.writeByte(data);
        dout.flush();
        return true;
    } 
    /**
    * This method sends an int Array to the connected device
    * @param data is the int to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (int data) throws IOException
    {
        if (VERBOSE) System.out.println ("Sending int: "+data);
        dout.writeByte(INT);
        dout.writeInt(data);
        return true;
    } 
        
    /**
    * This method sends a boolean to the connected device
    * @param data is the boolean to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (boolean data) throws IOException
    {
        dout.writeByte(BOOLEAN);
        dout.writeBoolean(data);
        return true;
    }
        
    /**
    * This method sends a char to the connected device
    * @param data is the char to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (char data) throws IOException
    { 
        dout.writeByte(CHAR);
        dout.writeChar(data);
        return true;
    }

    /**
    * This method sends a String to the connected device
    * @param data is the String to send
    * @return true if data was sent, false otherwise
    */
    protected boolean send (String data) throws IOException
    {
        dout.write(STRING);
        dout.writeUTF(data);
        return true;
    }
    
   
    /**
     * Handles connection protocol
     * @return true if client logged in successfully, false otherwise
     */
    private boolean SayHello() throws IOException
    {
            /*  This is the protocol: client is expected to send the app to which
             *  it wants to connect and optionally a String Array of parameters.
             *  If app is not available, server responds with an error; same happens
             *  if client sent parameters not requested, didn't send requested
             *  parameters or if registration fails.
             */
            String appName = din.readUTF();
            if (VERBOSE) 
            {
                System.out.println("Application " + appName + " asks for connection");
            }
            if (conn == null) 
            {
                return false;
            }
            if (VERBOSE) 
            {
                System.out.println("Opening outgoing connection");
            }
            dout = conn.openDataOutputStream();
            if (!sm.isAppInstalled(appName)) 
            {
                if (VERBOSE) 
                {
                    System.out.println("Application " + appName + " is NOT installed on this server");
                }
                try 
                {
                    write(APP_NOT_AVAILABLE);
                    Thread.sleep(4000);
                    return false;
                } 
                catch (InterruptedException ex) 
                {
                    LogManager.getInstance().makeLog(this.getClass(), "", "Error while sleeping");
                }
            }
            ConnectionServer.getInstance().addConnection(appName, devAddr, this);
            if (VERBOSE) System.out.println ("Waiting for application to be ready");
            /*try 
            {
                sleep (2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }*/
            int c = 0; //counter
            while (!canContinue && c < 6) //Wait for the application to be ready (otherwise the next method raises a NullPointerException)
            {
                try 
                {
                    sleep (1000);
                    c++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if (!canContinue)
            {
                if (VERBOSE)
                    System.out.println ("Something has gone wrong, the application hasn't still started, closing thread");
                return false;
            }
            if (VERBOSE) System.out.println ("Application ready");
            if (RegistrationManager.getInstance().checkRegistrationFor(appName)) 
            {
            if (VERBOSE) System.out.println ("Application " + appName + " requires registration service");
                if (din.readByte() == VECTORSTRING) 
                {
                    String[] campi_registrazione = new String[din.readInt()];
                    for (int i = 0; i < campi_registrazione.length; i++) 
                    {
                        campi_registrazione[i] = din.readUTF();
                        if (VERBOSE) 
                        {
                            System.out.println("Field received " + campi_registrazione[i]);
                        }
                    }
                    if (!RegistrationManager.getInstance().manageRegistration(appName, devAddr, campi_registrazione))
                    {
                        if (VERBOSE) System.out.println ("Registration failed");
                        return false;
                    }    
                    if (VERBOSE) System.out.println("creo o resetto o ripristino una sessione per l'utente da ConnectionThread");
                    boolean ok = SessionManager.getInstance().manageSessionFor(devAddr, appName); //creo o resetto o ripristino una sessione per l'utente
                } 
                else 
                {
                    try 
                    {
                        dout.writeByte(SERVER_MESSAGE); //Client isn't sending registration fields but server needs them
                        dout.writeUTF("No registration data received");
                        if (VERBOSE) System.out.println ("No registration data received");
                        dout.flush();
                        sleep(2000);
                        disconnect();
                        return false;
                    } 
                    catch (InterruptedException ex) 
                    {
                        Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            authenticated = true;
            dout.writeByte(AUTH_OK);
            dout.flush();
            if (VERBOSE) 
            {
                System.out.println("Everything looks fine");
            }
            return true;
            } 
        
    
    /**
     * Method to disconnect and close thread 
     */
    protected void disconnect ()
    {
        if (disconnecting)
            return;
        try {
            if (VERBOSE) System.out.println ("Thread Disconnecting");
            disconnecting = true;
            if (timerPing != null) {
                timerPing.cancel();
            }
            cont = false;
            if (din != null) {
                din.close();
            }
            if (dout != null) {
                dout.close();
            }
            if (conn != null) {
                conn.close();
            }
            Thread.currentThread().interrupt();
        } 
       catch (IOException ex) {}
        
    }
    protected boolean onClosing()
    {
        if (server != null)
            {
                try 
                {
                    disconnect();
                    if (VERBOSE) 
                    {
                        System.out.println("Closing server");
                    }
                    server.close();
                    return true;
                } 
                catch (IOException ex) 
                {
                    return false;
                }
            }
        else return true;
    }
    
    /**
     * This class takes care of making sure that client is logging in a limited time
     */
    private class loginCheck extends TimerTask
    {
        /**
         * Runs the timer
         */
        public void run ()
        {
            if (!authenticated) 
            {
                if (VERBOSE) System.out.println ("Disconnecting by timeout");
                disconnect();
            }
        }
    }
}