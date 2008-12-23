/**
 * SessionManager creates and maintains Sessions for the applications.
 * It add and remove a session service, manage the session for the users and 
 * rescue open sessions. 
 * 
 * <pre>
 *    SessionManager sesMan = sesMan.getInstance();
 * </pre>
 * @author      Massimo Domenico Sammito
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.io.*;
import java.util.Hashtable;
import com.jsoft.linkbuild.utility.*;
import java.security.*;

public class SessionManager 
{
    //Debugging constants
    public final static Boolean VERBOSE = true; //enables some System.out.prinln
   
    private static SessionManager instance = null; // per il singleton
    private Hashtable activeAppSession, activeUserSession;
    
    /**
     * Create a new instance of SessionManager (could not used)
     */
    private SessionManager()
    {
        activeAppSession = new Hashtable<String, SessionRule>(); //Applicazione/SessionRule Associato
        activeUserSession = new Hashtable<String, Hashtable<String,Object[]>>(); //Utente/Oggetti Mantenuti per quell'utente che rappresentano la sessione
    }
    
    /**
     * Return the Singleton instance for the SessionManager
     * @return      The instance of SessionManager
     */
    public static SessionManager getInstance() 
    {
      if(instance == null)
      {
         instance = new SessionManager();
      }
      return instance;
    }
   
    /**
     * This methos is called from the applications, and it is used to add a Session Service for the
     * application representend by password. The session was managed by the the SessionRule passed in.
     * @param password                                      the password associated to the application that calls the method
     * @param rule                                          the SessionRule used to manage sessions.
     * @return                                              true if the service was added for the application, false otherwise.
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean addSession(int password, SessionRule rule) throws AccessControlException
    {
        if(rule == null) return false;
        String application = SettingsManager.getInstance().getAppFromPassword(password);
        if(!SettingsManager.getInstance().getSettingForApp(application).getSession())
            return false;
        
        if(!activeAppSession.containsKey(application))
        {
            activeAppSession.put(application, rule);
            return true;
        }
       
        return false;
    }
    
    /**
     * Remove the session service for the application represented by password.
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true if the service was removed for the application, false otherwise.
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean removeSession(int password) throws AccessControlException
    {
        String application = SettingsManager.getInstance().getAppFromPassword(password);
        if(activeAppSession.containsKey(application))
        {
            activeAppSession.remove(application);
            String[] usersOfApp = SettingsManager.getInstance().getConnectedUsers(application);
            String[] usersUseSession = Conversions.KeysToArrayString(activeUserSession);
            
            for(int i=0; i<usersUseSession.length; i++)
            {
                for(int j=0; j<usersOfApp.length; j++)
                {
                    if(usersUseSession[i].equals(usersOfApp[j]))
                    {
                        ((Hashtable)(activeUserSession.get(usersUseSession[i]))).remove(application);
                        break;
                    }
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Set a new SessionRule for the application associated to the password. Now all users registered to this 
     * application will be session managed with this new SessionRule.
     * @param password                                      the password associated to the application that calls the method
     * @param rule                                          the SessionRule used to manage sessions.
     * @return                                              true if the rule was changed for the application, false otherwise.
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean setRuleForApp(int password, SessionRule rule) throws AccessControlException
    {
        if(rule == null) return false;
        String application = SettingsManager.getInstance().getAppFromPassword(password);
        if(activeAppSession.containsKey(application))
        {
            activeAppSession.remove(application);
            activeAppSession.put(application, rule);
            return true;
        }
       
        return false;
    }
    
    /**
     * This methos is called from ConnectionServer or ConnectionThread to manage session for the user of an application.
     * This method could create a new session for the user, rescue a session for the user, reset the session or do nothing
     * if the service for the application is not active. And all of it automatically.
     * @param IDUser                the ID Bluetooth represent the user we want to manage session for.
     * @param application           the name of the application used by user.
     * @return                      true if the session was rescued, false otherwise.
     */
    protected boolean manageSessionFor(String IDUser, String application)
    {
        if(activeAppSession.containsKey(application)) //è attivo il servizio di Session per l'applicazione?
        {
            if (VERBOSE) System.out.println("è attivo il servizio di Session per l'applicazione "+application);
            
            if(activeUserSession.containsKey(IDUser) && ((Hashtable)(activeUserSession.get(IDUser))).containsKey(application)) //c'è già una sessione aperta per l'utente
            {
                if (VERBOSE) System.out.println("c'è già una sessione aperta per l'utente "+IDUser);
                //ripristino della sessione
                if(SettingsManager.getInstance().isAppRunning(application))
                {
                    Object[] fields = (Object[])(((Hashtable)(activeUserSession.get(IDUser))).get(application));
                    boolean c = ThreadManager.getInstance().notifyUserSessionRescue(application, IDUser, fields);
                    makeNewSessionForUser(IDUser, application,(SessionRule)(activeAppSession.get(application)));
                    if (VERBOSE) System.out.println("ripristino la sessione per l'utente "+IDUser);
                    return c;
                }
                else
                {
                    //anche se l'applicazione non è in running devo cmq resettare la sessione eventualmente aperta
                    makeNewSessionForUser(IDUser, application,(SessionRule)(activeAppSession.get(application)));
                    if (VERBOSE) System.out.println("resetto la sessione per l'utente "+IDUser);
                    return false; //l'applicazione non è in running quindi non si deve ripristinare nulla
                }
            }
            else //l'utente si collega per la prima volta all'applicazione
            {
               if (VERBOSE) System.out.println("l'utente "+IDUser+" si collega per la prima volta all'applicazione "+application);
               makeNewSessionForUser(IDUser, application,(SessionRule)(activeAppSession.get(application)));
               return false;
            }
        }
        else
        {
            if (VERBOSE) System.out.println("non è attivo il servizio di Session per :"+application);
            return false;
        }
    }
    
    /**
     * create a new session for a specific user.
     * @param IDUser                    the ID Bluetooth associated to the user.
     * @param application               the name of the application used by user.
     * @param rule                      the SessionRule used by application.
     */
    private void makeNewSessionForUser(String IDUser, String application, SessionRule rule)
    {
        if(activeUserSession.containsKey(IDUser) && ((Hashtable)(activeUserSession.get(IDUser))).containsKey(application))
        {
            ((Hashtable)(activeUserSession.get(IDUser))).remove(application);
            ((Hashtable)(activeUserSession.get(IDUser))).put(application, rule.buildFieldsForUser(IDUser, application));
        }
        else if(activeUserSession.containsKey(IDUser) && !((Hashtable)(activeUserSession.get(IDUser))).containsKey(application))
        {
            ((Hashtable)(activeUserSession.get(IDUser))).put(application, rule.buildFieldsForUser(IDUser, application));
        }
        else
        {
            Hashtable t = new Hashtable<String, Object[]>();
            t.put(application, rule.buildFieldsForUser(IDUser, application));
            activeUserSession.put(IDUser, t);
        }
   }
}

