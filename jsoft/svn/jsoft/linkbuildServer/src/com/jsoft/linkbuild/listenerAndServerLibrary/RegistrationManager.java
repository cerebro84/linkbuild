package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.security.AccessControlException;
import java.util.Hashtable;

/**
 * RegistrationManager helps the User Application to manage the registrations in the same applications.
 * The applicazions decides to add and remove Registration Service to its.
 * The RegistrationManager class is a singleton and its constructor is hidden to developer.
 * 
 * <pre>
 *    RegistrationManager rm = RegistrationManager.getInstance();
 *    rm.addRegistration(pw, new MyRule());     
 *    
 *    //See you RegistrationRule for more information of Rule interface
 * 
 * </pre>
 * @author      Christian Rizza
 */
public class RegistrationManager 
{
    private Hashtable<String, RegistrationRule> registration;
    private static RegistrationManager instance; //Singleton
    LogManager lm;
    
    /**
     * Class Contructor
     */
    private RegistrationManager()
    {
        registration=new Hashtable<String, RegistrationRule>();
        lm=LogManager.getInstance();
    }
    /**
     * Return a unique instance of RegistrationManager
     * @return RegitrazionManager instance
     */
    public static RegistrationManager getInstance()
    {
        if (instance==null)
        {
            instance = new RegistrationManager();
        }
        return instance;
    }
    /**
     * This methos is called from the applications, and it is used to activated 
     * RegistrationManager with settings the specific Registration Rules.
     * See the user manual to see more informtion to RegistrationManager
     * 
     * @param password                                      the password associated to the application that calls the method
     * @param rule                                          the rule implements by application
     * @return                                              true if redirect option was setted for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean addRegistration(int password, RegistrationRule rule) throws AccessControlException
    {
        if (rule==null) return false;
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if(!SettingsManager.getInstance().getSettingForApp(app_name).getRegistration())
            return false;
        
        if (!registration.contains(rule))
        {
            registration.put(app_name, rule);
            lm.makeLog(this.getClass(), app_name , "Added registration service for app "+app_name);
            return true;
        }
       
        return false;
    }
    /**
     * This methos is called from the applications, and it is used to remove 
     * Registration Rule and release Registration Service.
     * See the user manual to see more informtion to RegistrationManager
     * 
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true if servive was removed, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean removeRegistration(int password) throws AccessControlException
    {
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (registration.containsKey(app_name))
        {
            registration.remove(app_name);
            //lm.makeLog(this.getClass(), "id_blue", "Removed registration service");
            return true;
        }
        return false;
   }
    /**
     * This methos is called from the Library developer, and it is used to check
     * the Registration app by app_name
     * 
     * @param app_name          Name of application to check
     * @return                  return state of Registration for application
     */
   protected boolean checkRegistrationFor(String app_name)
   {
       return registration.containsKey(app_name);
   }
    /**
     * This methos is called from the Library developer, and it is used to verify
     * Registration and Registration Rule, written by App Admin
     * 
     * @param app_name          Name of application to check
     * @param address           Bluetooth Address
     * @param fields            Client's fields
     * @return                  return state of Registration for application
     */
   protected boolean manageRegistration(String app_name, String address, String[] fields)
   {
       if (checkRegistrationFor(app_name) && SettingsManager.getInstance().getSettingForSys().getRegistration()) //è richiesto il servizio
       {
           RegistrationRule rule = registration.get(app_name);
           ThreadManager.getInstance().notifyUserRegistrationFields(app_name, address, fields);
           return rule.acceptRegistration(address, fields);
       }
       else if(!SettingsManager.getInstance().getSettingForSys().getRegistration())
       {
           lm.makeLog(this.getClass(), address, "Il sistema ha disabilitato tutte le registrazioni");
           if (ConnectionServer.VERBOSE) System.out.println("Il sistema ha disabilitato tutte le registrazioni");
           return false;
       }
       return false;
   }
    /**
     * Method called when the library is shutdown mode.
     * 
     * @return              true, if closing are completed correctly, else otherwise
     */
   public static boolean onClosing()
   {
       return true;
   }
}