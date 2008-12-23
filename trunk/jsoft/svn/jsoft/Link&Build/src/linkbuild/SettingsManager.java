/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linkbuild;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author giuseppe
 * 
 * This class keeps settings and provides methods to access them
 */
public class SettingsManager 
{
    private final static String BASEPATH = "/home/giuseppe/dev/jsoft/svn/jsoft/Link&Build/ControlTool"; //solo scopo di test
    private final static String SETTINGSFILE = BASEPATH+"data" + File.separator + "settings.dat";
    private static SettingsManager instance = null; // per il singleton
    private Hashtable<String, String> installed_apps, registered_apps, available_apps, new_apps;
/**
 * Creates a new SettingsManager
 */
    protected SettingsManager() 
    {
        //Initialize variables
        installed_apps = new Hashtable<String, String>();
        available_apps = new Hashtable<String, String>();
        new_apps = new Hashtable<String, String>();
        registered_apps = new Hashtable<String, String>();
        
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
      if(instance == null) {
         instance = new SettingsManager();
      }
      return instance;
    }
/**
 * This method loads configuration;
 */
    private void loadConfiguration() 
    {
        if (new File(SETTINGSFILE).exists()) 
        {
            available_apps = (Hashtable)FileManager.objectList(SETTINGSFILE)[0];
        }
        installed_apps = findInstalledApps();
        new_apps = findNewApps();
    }
/**
 * Looks for apps in default directory
 * @return A LinkedList of found apps.
 */
 private Hashtable findInstalledApps() 
 {
        Hashtable ia = new Hashtable<String,String>();
        File[] apps = FileManager.listDirectory(BASEPATH+File.separator+"Applications", 1);
        if (apps != null) {
            for (File app : apps) 
            {
                ia.put(app.getName(),app.getPath());
            }
        }
        return ia;
    }
 /**
  * Looks for new apps.
  * @return A LinkedList of New Apps (in any)
  */
    private Hashtable findNewApps()
    {
        Hashtable _newapps = new Hashtable<String,String>();
        String app;
        for (Enumeration<String> tmp = installed_apps.keys(); tmp.hasMoreElements();)
        {
            if (!available_apps.contains(app = tmp.nextElement())) 
            {
                _newapps.put(app,installed_apps.get(app));
            }
        }
        return _newapps;
    }
   
/**
 * Just a getter
 * @return Hashtable containing new apps
 */
    public Hashtable getNewApps() 
    {
        return new_apps;
    }
/**
 * Just a getter
 * @return An array of names of the available apps
 */
    public Hashtable getAvailableApps() 
    {
       return available_apps;
    }
/**
 * Just a getter
 * @return An array of names of the installed apps
 */
    public Hashtable getInstalledApps()
    {
        return installed_apps;
    }
    /**
 * Just a setter
 * @return True if the application has been registered, False otherwise.
 */
    
    public boolean registerApp(String appName, String appPath) 
    {
        if (available_apps.contains(appName))
        {
            registered_apps.put(appName, appPath);
            return (true);
        }
        else return (false);
    }
}
