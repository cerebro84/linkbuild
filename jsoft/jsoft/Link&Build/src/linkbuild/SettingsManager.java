/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package linkbuild;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author giuseppe
 * 
 * This class keeps settings and provides methods to access them
 */
public class SettingsManager 
{
    private final static String BASEPATH = "/home/giuseppe/dev/jsoft/svn/jsoft/Development/ControlTool"; //solo scopo di test
    private final static String SETTINGSFILE = BASEPATH+"data" + File.separator + "settings.dat";
    private static SettingsManager instance = null;
    private LinkedList available_apps, installed_apps, new_apps, registered_apps;
    private Hashtable<String, LinkedList> settings;
/**
 * Creates a new SettingsManager
 */
    protected SettingsManager() 
    {
        //Initialize variables
        settings = new Hashtable<String, LinkedList>();
        installed_apps = new LinkedList();
        available_apps = new LinkedList();
        new_apps = new LinkedList();
        registered_apps = new LinkedList();
        
        //Load and settle stored configuration
        loadConfiguration();
        setConfiguration();

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
            available_apps = (LinkedList) FileManager.objectList(SETTINGSFILE)[0];
        }
        installed_apps = findInstalledApps();
        new_apps = findNewApps();
    }
/**
 * Looks for apps in default directory
 * @return A LinkedList of found apps.
 */
 private LinkedList findInstalledApps() 
 {
        LinkedList ia = new LinkedList();
        // System.out.println ("DEBUG: "+ BASEPATH+File.separator+"Applications");
        File[] apps = FileManager.listDirectory(BASEPATH+File.separator+"Applications", 1);
        if (apps != null) {
            // System.out.println ("Debug: apps != null");
            for (File app : apps) 
            {
                ia.add(app.getName());
            }
        }
        return ia;
    }
 /**
  * Looks for new apps.
  * @return A LinkedList of New Apps (in any)
  */
    private LinkedList findNewApps()
    {
        LinkedList new_apps = new LinkedList();
        for (String app : (String[]) installed_apps.toArray(new String[1])) 
        {
            if (!available_apps.contains(app)) 
            {
                new_apps.add(app);
            }
        }
        return new_apps;
    }
    /**
     * Sets hashtable
     */

    private void setConfiguration() 
    {     
        settings.put("Installed apps", installed_apps);    
        settings.put("Available apps", available_apps);
        settings.put("New apps", new_apps);
        settings.put("Registered apps",registered_apps);
    }
/**
 * Just a getter
 * @return An array of names of the new apps
 */
    public String[] getNewApps() 
    {
        if (available_apps != null)
            return (String[]) new_apps.toArray(new String[1]);
        else
            return new String [0];
    }
/**
 * Just a getter
 * @return An array of names of the available apps
 */
    public String[] getAvailableApps() 
    {
       return (String[]) available_apps.toArray(new String[1]);
    }
/**
 * Just a getter
 * @return An array of names of the installed apps
 */
    public String[] getInstalledApps()
    {
        return (String[]) installed_apps.toArray(new String[1]);
    }
    /**
 * Just a setter
 * @return True if the application has been registered, False otherwise.
 */
    
    public boolean registerApp(String appname) 
    {
        if (available_apps.contains(appname))
        {
            settings.get("Registered apps").add(appname);
            return (true);
        }
        else return (false);
    }
}
