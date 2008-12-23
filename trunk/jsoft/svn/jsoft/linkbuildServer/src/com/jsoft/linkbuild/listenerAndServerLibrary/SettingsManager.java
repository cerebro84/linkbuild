/*
 * @author Angelo Giuseppe De Michele
 * This class stores settings and provides methods to access them
 */
package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.security.*;
import com.jsoft.linkbuild.utility.*;

public class SettingsManager 
{
    private final static boolean VERBOSE = ConnectionServer.VERBOSE; //Debug purpose
    private final static String path = "Data"+File.separator;
    private final static String applicationPath = "Application"+File.separator;
    private static SettingsManager instance = null; // for the singleton
    private Hashtable   installed_apps, available_apps, new_apps, registered_apps,
                        all_active_connections, processes, last_communications, 
                        passForApp, logged_apps, running_apps;
    private Setting systemSetting;
    private LogManager lm; //For convienence
    
    /**
    * Creates a new SettingsManager
    */
    private SettingsManager() 
    {
        // Initializes Logging
        lm = LogManager.getInstance();
        if (VERBOSE) System.out.println ("SM Constructor");

        //Initializes variables
        installed_apps          = new Hashtable<String, Boolean>(); //NAME,APPROVED
        available_apps          = new Hashtable<String, String>();
        new_apps                = new Hashtable<String, String>();
        registered_apps         = new Hashtable<String, Hashtable>();
        all_active_connections  = new Hashtable<String, String>(); //Contiene INDIRIZZO del dispositivo e APPLICAZIONE a cui esso connesso
        processes               = new Hashtable<String, ConnectionThread>(); // Contiene INDIRIZZO e THREAD
        last_communications     = new Hashtable<String, Long> (); // Contiene INDIRIZZO e TIMESTAMP
        passForApp              = new Hashtable<Integer, String>(); //String è il nome dell'applicazione, Integer è la password associata
        logged_apps             = new Hashtable<String, Boolean>(); //Contiene il il nome dell'aplicazione e lo stato del servizio.
        running_apps            = new Hashtable<String, Object>(); //Contiene il nome dell'applicazione e NULL

        //Loads configuration
        loadConfiguration();
        

    }
    
    /**
     * This method instantiate a new SettingsManager or returns
     * the existing one if it has already been created.
     * @return the SettingsManager
     */
    public static SettingsManager getInstance() 
    {
      if(instance == null) 
      {
         instance = new SettingsManager();
      }
      return instance;
    }
    
    /**
    * This method loads configuration;
    */
    private void loadConfiguration() 
    {
        installed_apps = findInstalledApps();
        available_apps = findAvailableApps();
        new_apps = findNewApps(); // This goes last
        systemSetting = (Setting)((FileManager.objectList(path+"setting.dat"))[0]);
    }
    
    /**
     * Useful to update hashtables
     */
    protected void reloadConfiguration()
    {
        if (VERBOSE) System.out.println ("Reloading configuration");
        loadConfiguration();
    }
    
    /**
    * Looks for apps in default directory
    * @return an hashtable<String,Boolean> containing name of the app and if it has been approved
    */
    protected Hashtable findInstalledApps() 
    {
        // Inserts installed and approved apps
        Hashtable ia = new Hashtable<String,Boolean>();
        if (FileManager.fileIsEmpty(path+"AppList.txt") == 0)
            {
                String appList[] = FileManager.readFile(path+"AppList.txt");
                for (String app : appList)
                {
                    ia.put(app,true);
                }
            }
        
        // Inserts installed but still not approved apps
        if (FileManager.fileIsEmpty(path+"AppWaitingList.txt") == 0)
            {
                String appList[] = FileManager.readFile(path+"AppWaitingList.txt");
                for (String app : appList)
                {
                    ia.put(app,false);
                }
            }
        
        return ia;
    }
    
    /**
    * Looks for apps in the Application dir that haven't been installed yet
    * @return an Hashtable containing apps names and paths
    */
    protected Hashtable findAvailableApps()
    {
     Hashtable <String,String> _avApps = new Hashtable<String,String>();
     File[] apps = FileManager.listDirectory("Application", 1);
     if (apps != null) {
         for (File app : apps) {
             _avApps.put(app.getName(),app.getPath());
         }
     }
     return _avApps;
 }
    
    /**
    * Looks for new apps.
    * @return It returns AVAILABLE\INSTALLED apps where \ means difference.
    */
    protected Hashtable findNewApps()
    {
        Hashtable _newapps = new Hashtable<String,String>();
        String app;
        Enumeration<String> tmp = available_apps.keys();
        while (tmp.hasMoreElements())
        {
            if (!installed_apps.containsKey(app = tmp.nextElement())) 
            {
                _newapps.put(app,"");
            }
        }
        return _newapps;
    }
   
    /**
     * Get the Setting object associated to the Application
     * @param application       the application name
     * @return                  the Setting Object
     * @author                  Massimo Sammito
     */
    protected Setting getSettingForApp(String application)
    {
       return (Setting)((FileManager.objectList(applicationPath+File.separator+application+File.separator+"setting.dat"))[0]);
    }
    
    /**
     * Get the Setting object associated to the System
     * @return                  the Setting Object
     * @author                  Massimo Sammito
     */
    protected Setting getSettingForSys()
    {
        return this.systemSetting;
    }
            
    /**
    * Method to get new apps
    * @param refresh is used to force a new search (and not using hashtable) when true
    * @return An array of names of the available apps
    */
    public String [] getNewApps(boolean refresh) 
    {
        if (refresh) findNewApps();
        return Conversions.KeysToArrayString(new_apps);
    }
    
    /**
    * Method to get available apps
    * @param refresh is used to force a new search (and not using hashtable) when true
    * @return An array of names of the available apps
    */
    public String [] getAvailableApps(boolean refresh) 
    {
       if (refresh) findAvailableApps();
       return Conversions.KeysToArrayString(available_apps);
    }
    
    /**
     * Get the max time of non communication server/client for the application
     * @param application           the name of the application.
     * @return                      the max time of non communication server/client.
     * @author                      Massimo Sammito 
     */
    protected long getMaxTimePingForApp(String application)
    {
        return this.getSettingForApp(application).getInterruptionTime();        
    }
    
    /**
     * Get max time of non communication server/client for the application represented by password
     * @param password                                  the password that rappresent the application
     * @return                                          the max time of non communication server/client.
     * @throws java.security.AccessControlException     It is sent when the password is null 
     *                                                  or not exist an application associate to the password.
     * @author                                          Massimo Sammito 
     */
    public long getMaxTimePingForApp(int password) throws AccessControlException
    {
        return this.getSettingForApp(this.getAppFromPassword(password)).getInterruptionTime();
    }
    
    /**
    * Method to get a list of installed apps
    * @param refresh is used to force a new search (and not using hashtable) when true
    * @return An array of names of the installed apps (approved or not)
    */
    public String [] getInstalledApps(boolean refresh)
    {
            if (refresh) findInstalledApps();
            return Conversions.KeysToArrayString(installed_apps);
    }
    
    /**
    * Utility to get an array containing all the keys of an Hashtable which value is equal to the specified one
    * @param condition is the value to compare to
    * @param toCheck is the hashtable to analyze
    * @return
    */
    private String [] getBoolValues(boolean condition, Hashtable toCheck)
    {
            if (toCheck.containsValue(condition))
            {
                String [] array = new String[toCheck.size()];
                int t=0; //trues counter
                String appName;
                Enumeration<String>iterator = toCheck.keys();
                while (iterator.hasMoreElements())
                {
                    if (toCheck.get(appName = iterator.nextElement()) == (Boolean)condition)
                    {
                        array[t] = appName;
                        t++;
                    }
                }
                return Arrays.copyOf(array, t); //Truncates array    
            }
            else return new String[0];
        }
    
    /**
    * Uses getBoolValue to find which installed_apps have been approved
    * @return a String array containing all approved apps
    */
    public String [] getApprovedApps (boolean refresh)
    {
            if (refresh) findInstalledApps();
            return getBoolValues (true,installed_apps);
        }
    
    /**
    * Uses getBoolValue to find which installed_apps haven't been approved
    * @return a String array containing all unapproved apps
    */
        
    public String [] getNotApprovedApps (boolean refresh)
    {
            if (refresh) findInstalledApps();
            return getBoolValues (false,installed_apps);
        }
        
    /**
    * Method to register a server app
    * @return True if the application has been registered, False otherwise or 
    * if the application has been already registered. If true, creates a new 
    * Hashtable to store application settings.
    */
    protected boolean registerServerApp(String appName) 
    {
        if (installed_apps.containsKey(appName) && !(registered_apps.containsKey(appName)))
        {

            if (VERBOSE) System.out.println ("Registering");
            Hashtable appSettings = new Hashtable <String,Object>();
            appSettings.put("timeRegistered",System.currentTimeMillis());
            Hashtable tmp = new Hashtable<String,Thread>();
            appSettings.put("connectedThreads", tmp);
            registered_apps.put(appName, appSettings);
            if (VERBOSE) System.out.println (appName + " registered");
            return (true);
        }
        else
        {
            lm.makeLog(this.getClass(), "", "application not registered");
            return (false);
        }
    }
    
    /**
     * Method to logically set an app as running or not running
     * @param appName is the app to set the running state of
     * @param state is the running state we want to set
     * @return the new running state of app
     */
    protected boolean setAppRunning (String appName, boolean state)
    {
        if (state == true) 
        {
            if (VERBOSE) System.out.println ("SM: Setting " + appName + "as running");
            if (VERBOSE) System.out.println ("SM: Was it running before? "+ running_apps.containsKey(appName));
            running_apps.put(appName, new Object());
            return running_apps.containsKey(appName);
        }
        else
        {
            if (VERBOSE) System.out.println ("SM: Removing " + appName + "from running applications");
            if (VERBOSE) System.out.println ("SM: Was it running? "+ running_apps.containsKey(appName));
            running_apps.remove(appName);
            registered_apps.remove(appName);
            Enumeration listOfKeys = passForApp.keys();
            while (listOfKeys.hasMoreElements()) //Deletes password
            {
                int password = (Integer)listOfKeys.nextElement();
                if (passForApp.get(password).equals(appName))
                {
                    System.out.println("tento di rimuovere "+appName+" con password "+password);
                    passForApp.remove(password);
                    break;
                }
            }  
            return running_apps.containsKey(appName);
        }
    }
    
    /**
     * Checks if an application is running
     * @param appName is the application to check running state
     * @return true if application is running, false otherwise
     */
    public boolean isAppRunning (String appName)
    {
        if (appName == null) return false;
        return (running_apps.containsKey(appName));
        
    }
    
    /**
     * Set the password for the application. it could be called only one time for application.
     * It is called from ThreadManager when a new application is started and associate the password with
     * the application.
     * @param appName           the Application Name       
     * @param password          the password to be associated to the application.
     * @return                  true if the method associate the password to the application, false otherwise
     * @author Massimo Sammito
     */ 
    protected boolean setAppPassword(String appName, int password)
    {
        if(!passForApp.containsKey(appName))
        {
            if (VERBOSE) System.out.println ("SettingsMananager: imposto la password per "+appName);
            passForApp.put(password, appName);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Get the password for the application. It is called from all methods that wants to know wich application
     * is associated to the password.
     * @param password                                      the password associated to the application.
     * @return                                              the application name
     * @throws java.security.AccessControlException         It is sent when the password is null 
     *                                                      or not exist an application associate to the password.
     * @author Massimo Sammito
     */
    protected String getAppFromPassword(int password) throws AccessControlException
    {
        String appAssociated = (String)passForApp.get(password);
        if(appAssociated != null && !appAssociated.equals(""))
            return appAssociated;
        else
        {
           if (VERBOSE) System.out.println ("Lancio l'eccezione da getAppFromPassword: "+appAssociated);
           throw new AccessControlException("Permission Denied");
        }
    }
    
    /**
     * It simply reply if the the user is registered to the application associated to the password.
     * @param password                                    the password that represent the application.
     * @param address                                     the ID Bluetooth of the user.  
     * @return                                            true if the user is registered to the application associated to the password,
     *                                                    false otherwise.  
     * @throws java.security.AccessControlException       It is sent when the password is null 
     *                                                    or not exist an application associate to the password. 
     * @author                                            Massimo Sammito
     */
    protected boolean couldAllowToManageUser(int password, String address) throws AccessControlException
    {
        if (address == null)
        {
            lm.makeLog(this.getClass(), "", "couldAllowToManageUsers: null user");
            return false;
        }
        String appAssociated = SettingsManager.getInstance().getAppFromPassword(password);
        if (VERBOSE) System.out.println ("SM: Checking if I can manage user " + address);
        String appUsedFromUser = SettingsManager.getInstance().getApplicationUsedByUser(address);
        if(appAssociated == null || appUsedFromUser == null || appAssociated.equals("") || appUsedFromUser.equals(""))
        {
            if (VERBOSE) System.out.println ("Lancio l'eccezione da couldAllowToManageUser: appAssociated: "+appAssociated+"  appUsedFromUser: "+appUsedFromUser);
            throw new AccessControlException("Permission Denied");
        }
        else 
            return (appAssociated.equals(appUsedFromUser));


    }
    
    /**
     * Return debug option for logging by root
     * @return boolean
     * 
     * @author Christian
     */
    protected boolean getRootDebugOption()
    {
        return this.systemSetting.getDebug();
    }
   
    /**
     * Checks if an app is installed
     * @param appName is the name of the application
     * @return true if installed, false otherwise
     */
    public boolean isAppInstalled (String appName)
    {
        if (appName == null) return false;
        return (installed_apps.containsKey(appName));
    }
    
    public boolean isLoggedApp(String app_name)
    {
        if (app_name == null) return false;
	if (logged_apps.containsKey(app_name))
            return ((Boolean)logged_apps.get(app_name)).booleanValue();
	return false;
    }
    
    /**
     * Checks if an application has been approved
     * @param appName
     * @return true if application has been approved, false otherwise
     */
    public boolean isAppApproved (String appName)
    {
        if (appName == null) return false;
        return (Boolean)installed_apps.get(appName);
    }
    
    /**
     * Checks if an application is available
     * @param appName is the name of the application to check availability
     * @return true if available, false otherwise
     */
    public boolean isAppAvailable (String appName)
    {
        if (appName == null) return false;
        return (available_apps.contains(appName));
    }
    
    /**
     * Checks if a given application has been copied in the Application folder
     * @param appName is the name of the application to check existence
     * @return true if existing, false otherwise
     */
    public boolean doesAppExist (String appName)
    {
        return (new_apps.contains(appName));
    }
    
    /**
     * Method to store connection data
     * @param app is the app to which the client is connected
     * @param address is client bluetooth address
     * @param process is the ConnectionThread managing communication
     * @return true if connection has been added, false otherwise.
     */
    protected boolean addConnection(String app, String address, ConnectionThread process)
    {
        if (VERBOSE) System.out.println ("Numero utenti connessi all'app"+app+": "+ +instance.getConnectedUsersNumber(app));
        if (VERBOSE) System.out.println ("Numero utenti connessi globali: "+instance.getConnectedUsers());
        if (VERBOSE) System.out.println ("Numero utenti massimi globali: "+instance.getSettingForSys().getClientsNumber());
        if (VERBOSE) System.out.println ("Numero utenti massimi app: "+instance.getSettingForApp(app).getClientsNumber());
        
        if 
        (
        (isAppApproved(app)) && 
        (instance.getConnectedUsers() < instance.getSettingForSys().getClientsNumber()) &&
        (instance.getConnectedUsersNumber(app) < instance.getSettingForApp(app).getClientsNumber())
        )
        {    
            all_active_connections.put(address, app);
            last_communications.put(address, System.currentTimeMillis());
            processes.put(address,process);
            if(VERBOSE) System.out.println("SettingsManager: L'applicazione "+app+" è approvata");
            return true;
        }
        lm.makeLog(this.getClass(), address, "Connection refused because requested application hasn't been approved or max user number exceeded");
        return false;
    }
    
    /**
     * Method to get the process that communicates with a specified address
     * @param address is the client bluetooth address
     * @return the ConnectionThread that manages the connection.
     */
    protected ConnectionThread getAssociatedProcess (String address)
    {
        return (ConnectionThread)processes.get(address);
    }
    
    /**
     * Method to get a list of all connected addresses
     * @return a String Array of bluetooth IDs.
     */
    protected String [] getConnectedAddresses ()
    {
        return Conversions.KeysToArrayString(all_active_connections);
    }
    
    /**
     * Method to get a list of all active applications
     * @return a String Array of applications names.
     */
    protected String [] getConnectedApps ()
    {
        return Conversions.StringValuesToArray (all_active_connections);
    }
    
    /**
     * Method to check if a user is connected in this moment
     * @param address is the user bluetooth address
     * @return true if user is connected, false otherwise
     */
    public  boolean isUserConnectedAtServer (String address)
    {
        if (address == null) return false;
        return (all_active_connections.containsKey(address));
    }
    
    /**
     * Method to logically remove a connection
     * @param address is the client address
     * @return true if it has been removed, false otherwise
     */
    protected boolean removeConnection (String address)
    {
        if (VERBOSE) System.out.println ("Remove connection chiamato");
        boolean res = true;
        if (all_active_connections.remove(address) == null) res = false;
        if (processes.remove(address) == null) res = false;
        if (last_communications != null) 
        {
            last_communications.remove(address);
        }
        return res;
    }
    
    /**
     * Method to update last communication time
     * @param address is the client address
     */
    protected void updateLastCommunication (String address)
    {
        last_communications.put (address,System.currentTimeMillis());
    }
    
    /**
     * Method to get the last communication time. 
     * @param address is the client address
     * @return a long measuring the difference, in milliseconds, between the last communication time and midnight, January 1, 1970 UTC
     */
    protected long getTimeLastCommunication(String address)
    {
        Long ret = (Long)last_communications.get(address);
        if (ret == null)
        {
            if (VERBOSE) System.out.println ("last_comm ha restituito null");
            ret = (Long)((Hashtable)registered_apps.get(getApplicationUsedByUser(address))).get("timeRegistered");
            if (VERBOSE) System.out.println ("ret vale: "+ret);
        }
        if (ret != null)
        {
            return ret;
        }
        else
            lm.makeLog(this.getClass(),"Unable to find any communication","");
        return System.currentTimeMillis();
            
    }
    
    /**
     * Get the remaining time before the user have to be disconnected from server because of Ping Out
     * @param password                                    the password that represent the application.
     * @param address                                     the ID Bluetooth of the user. 
     * @return                                            the remaining time in milliseconds  
     * @throws java.security.AccessControlException       It is sent when the password is null 
     *                                                    or not exist an application associate to the password. 
     * @author Massimo Sammito
     */
    public long getRemainingTimeBeforePingOut(int password, String address) throws AccessControlException
    {
        if(couldAllowToManageUser(password, address))
        {
            return ((getMaxTimePingForApp(getAppFromPassword(password)))-(System.currentTimeMillis()-getTimeLastCommunication(address)));
        }
        else return -1L;
    }
    
    /**
     * Method called when closing is requested
     * @return true if everything has been done
     */
    protected static boolean onClosing ()
    {
        if (instance == null)
            return true;
        else
            return true;
    }
    
    /**
     * Method to get a list of running applications
     * @return a String Array containing all running applications
     */
    public String [] getRunningApps ()
    {
        return Conversions.KeysToArrayString (running_apps);
    }
    
    /**
     * Method to get a list of users connected to an application
     * @param app is the application of which we want to know connected users
     * @return a String Array containing the users IDs
     */
    protected String [] getConnectedUsers (String app)
    {
        String [] ret = new String [all_active_connections.size()];
        int i = 0;
        String s;
        for (Enumeration<String> tmp = all_active_connections.keys(); tmp.hasMoreElements();)
            {
                s = tmp.nextElement();
                if ((all_active_connections.get(s)).equals(app))
                {
                        ret [i] = s;
                        i++;
                }
            }
        return Arrays.copyOf(ret, i);
    }
    
    /**
     * Returns the number of users connected to the library
     * @return that number
     */
    protected int getConnectedUsers ()
    {
        if (VERBOSE) System.out.println ("Connected users number: " + all_active_connections.values().size());
        return all_active_connections.values().size();
    }
    
    /**
     * Returns the number of users connected to an application
     * @param app is the application
     * @return that number
     */
    protected int getConnectedUsersNumber (String app)
    {
        if (VERBOSE)
            for (String user : instance.getConnectedUsers(app))
                System.out.println ("utente connesso a "+app+" "+user);
        return (getConnectedUsers(app)).length;
    }
    
    /**
     * Get the application used by the user.
     * @param address               The ID Bluetooth associated to the user.
     * @return                      The application used by the user, or null if
     *                              there is not an application who manage this user.
     * @author Massimo Sammito 
     */
    protected String getApplicationUsedByUser(String address)
    {
        String ret = (String)(all_active_connections.get(address));
        return ret;
    }
}