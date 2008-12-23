/**
 * ThreadManager creates and maintains threads.
 * It Starts and activate the ListenerLibrary component.
 * It Provides procedures to communicate qith the Applications.
 * <pre>
 *    ThreadManager th = ThreadManager.getInstance();
 * </pre>
 * @author      Massimo Domenico Sammito
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.io.*;
import java.util.Hashtable;
import com.jsoft.linkbuild.utility.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.*;
import javax.management.loading.*;

public class ThreadManager
{
    private static ThreadManager instance = null; // per il singleton
    private Hashtable activeThreads;
    private MLet classLoader;
    private final static boolean VERBOSE = true;
    
    /**
    * To Construct ThreadManager. (Could not used)
    * of the ListenerLibrary.  
    */
    private ThreadManager()
    {
      activeThreads = new Hashtable<String, Class>(); //Contiene ID thread e l'APPLICAZIONE associata
      classLoader = new MLet();
                
      startMainListener();  
    }
   
    /**
     * Return the Singleton instance for the ThreadManager
     * @return      The instance of ThreadManager
     */
    public static ThreadManager getInstance() 
    {
      if(instance == null)
      {
         instance = new ThreadManager();
      }
      return instance;
    }
   
   /**
    * Lunch the MainListener.
    */
    private void startMainListener()
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            MainListener mn;

            public void run() 
            {
                final SwingWorker worker = new SwingWorker()
                {
                    public Object construct()
                    {
                        mn = MainListener.getInstance();
                        return null;
                    }
                };
                worker.start();  //required for SwingWorker 3
            }
        });
    }
     
    /**
     * Start a new application in a separated thread and notify the first user to the application.
     * This method associate a password to the application and notify both SettingsManager and the application
     * about it. This password will be used from Application to access to some critical method in security mode.
     * @param className     the Class Name that identify the Application.
     * @param IDuser        the ID Bluetooth that identify the user.
     */
    protected void startApplication(String className, String IDuser)
    {
       final String str = className;
       final String id = IDuser;
       
       java.awt.EventQueue.invokeLater(new Runnable()
       {
          public void run() 
          {
             final SwingWorker worker = new SwingWorker()
             {
                public Object construct()
                {
                    try
                    {
                        if (VERBOSE) System.out.println("Inizio procedura avvio classe");
                        File file = new File("Application" + File.separator + str + File.separator);
                        int randInt = (int) Math.random() * 600;
                        String password = str + randInt;
                        if (VERBOSE) System.out.println("Creata la password");
                        URL url = file.toURI().toURL();
                        classLoader.addURL(url);
                        if (VERBOSE) System.out.println("Aggiunta l'url al classloader");
                        Class cla = (Class) classLoader.loadClass(str); //provo a caricare la classe con il nome className
                        if (VERBOSE) System.out.println("Caricata la classe?" + cla.getName());
                        Class[] parametriFormali = {int.class};
                        Object[] parametriAttuali = {password.hashCode()}; //do al costruttore una password univoca per accedere a certi metodi
                        SettingsManager.getInstance().setAppPassword(str, password.hashCode());
                        SettingsManager.getInstance().registerServerApp(str);
                        if (VERBOSE) System.out.println("Imposto in esecuzione");
                        SettingsManager.getInstance().setAppRunning(str, true);
                        if (VERBOSE) System.out.println("Ottengo la LinkBuildApp");
                        if (VERBOSE) System.out.println("Costruttore: " + cla.getConstructor(parametriFormali));
                        LinkBuildApp lba = (LinkBuildApp) cla.getConstructor(parametriFormali).newInstance(parametriAttuali); //ATTENZIONE la classe dev'essere pubblica altrimenti non va
                        if (VERBOSE) System.out.println("Imposto il thread come attivo");
                        activeThreads.put(str, new ThreadTracker(this, lba)); //associo alla classe un id univoco del thread
                        if (VERBOSE) System.out.println("Il thread è attivo? " + activeThreads.get(str));
                        //comunico all'applicazione che IDuser e un suo utente.
                        boolean notificato = notifyNewUser(str, id);
                        if (VERBOSE) System.out.println("Ho notificato all'app?" + notificato);
                        SettingsManager.getInstance().getAssociatedProcess(id).canContinue = true;
                    }
                    catch (InstantiationException ex) 
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IllegalAccessException ex) 
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IllegalArgumentException ex)
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (InvocationTargetException ex) 
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (NoSuchMethodException ex)
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (SecurityException ex)
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (ClassNotFoundException ex)
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    catch (MalformedURLException ex)
                    {
                            Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return false;
                }
             };
             worker.start();  //required for SwingWorker 3
          }
       });
    }
    
    /**
     * Notify a new user to the application.
     * @param application   the Application Name
     * @param IDUser        the ID Bluetooth that identify the user.
     * @return              true if new user incoming was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyNewUser(String application, String IDUser)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.newUserRegistered(IDUser);
    }
    
    /**
     * Notify the normal disconnection of an user to the application.
     * @param application       the Application Name       
     * @param IDUser            the ID Bluetooth that identify the user.
     * @return                  true if the user disconnection was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyDisconnessionUser(String application, String IDUser)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userDisconnected(IDUser);
    }
    
    /**
     * Notify the abnormal disconnection of an user to the application.
     * @param application       the Application Name       
     * @param IDUser            the ID Bluetooth that identify the user.
     * @return                  true if the user disconnection was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyDisconnessionUserAbnormal(String application, String IDUser)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userAbnormalDisconnected(IDUser);
    }
    
    /**
     * Notify the disconnection of an user to the application, because of he was ping out from the server.
     * @param application       the Application Name       
     * @param IDUser            the ID Bluetooth that identify the user.
     * @return                  true if the user disconnection was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyDisconnessionUserPing(String application, String IDUser)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userDisconnectedForPing(IDUser);
    }
    
    /**
     * Notify the disconnection of an user to the application, because of he was banned out from all entire server.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user.
     * @param message           a String that describe the reasons for the ban.           
     * @return                  true if the user banning was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyDisconnessionUserSysBan(String application, String IDUser, String message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userDisconnectedForSysBan(IDUser, message);
    }
    
    /**
     * Notify the disconnection of an user to the application, because of he was banned out from only this application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user.
     * @param message           a String that describe the reasons for the ban.           
     * @return                  true if the user banning was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyDisconnessionUserAppBan(String application, String IDUser, String message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userDisconnectedForAppBan(IDUser, message);
    }
    
    /**
     * Notify a String to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the String to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyStringToApp(String application, String IDUser, String message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.stringIncoming(IDUser, message);
    }
    
    /**
     * Notify a String Array to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param messages          the String Array to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyStringArrayToApp(String application, String IDUser, String[] messages)
    {
       LinkBuildApp app = getAppFromName(application);
       return app.stringArrayIncoming(IDUser, messages); 
    }
    
    /**
     * Notify a Boolean to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the Boolean to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyBooleanToApp(String application, String IDUser, Boolean message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.booleanIncoming(IDUser, message);
    }
    
    /**
     * Notify am Integer to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the Integer to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyIntegerToApp(String application, String IDUser, int message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.integerIncoming(IDUser, message);
    }
    
    /**
     * Notify a Byte to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the Byte to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyByteToApp(String application, String IDUser, byte message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.byteIncoming(IDUser, message);
    }
    
    /**
     * Notify a Char to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the Char to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyCharToApp(String application, String IDUser, char message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.charIncoming(IDUser, message);
    }
    
    /**
     * Notify a Byte Array to the application.
     * @param application       The Application Name       
     * @param IDUser            the ID Bluetooth that identify the user that has sent the message.
     * @param message           the Byte Array to notify.           
     * @return                  true if the message was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyByteArrayToApp(String application, String IDUser, byte[] message)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.byteArrayIncoming(IDUser, message);
    }
    
    /**
     * Notify a log message to the application that use the redirect option of the LogManager
     * @param application       The Application Name  
     * @param log               The log message produced by LogManager
     * @return                  true if the log was being notifyed, false otherwise.
     */
    protected synchronized boolean notifyLogToApp(String application, String log)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.logIncoming(log);
    }
    
    /**
     * Notify the Registration Fields to the application.
     * @param application       The Application Name  
     * @param IDUser            the ID Bluetooth that identify the user wich fields are refering to.
     * @param fields            the String array that contains all fields required to identification for the application.
     * @return                  true if the fields were being notifyed, false otherwise.
     */
    protected synchronized boolean notifyUserRegistrationFields(String application, String IDUser, String[] fields)
    {
        LinkBuildApp app = getAppFromName(application);
        return app.userRegistrationFields(IDUser, fields);
    }
    
    /**
     * Notify the user session rescue to the application.
     * @param application       The Application Name  
     * @param IDUser            the ID Bluetooth that identify the user wich fields are refering to.
     * @param fields            the String array that contains all fields required to represent the session for the application.
     * @return                  true if the fields were being notifyed, false otherwise.
     */
    protected synchronized boolean notifyUserSessionRescue(String application, String IDUser, Object[] fields)
    {
       LinkBuildApp app = getAppFromName(application);
       return app.userSessionFields(IDUser, fields); 
    }
    
    /**
     * Notify the exit command to all application in active threads.
     * @return                  true if all application are closed, false otherwise.
     */
    private boolean notifyClosingToAllApp()
    {
        String[] applications = Conversions.KeysToArrayString(activeThreads);
        boolean returnValue = false;
        
        for(int i=0; i<applications.length; i++)
        {
            returnValue = notifyClosingToApp(applications[i]);
            System.out.println("L'applicazione "+applications[i]+" è stata chiusa?: "+returnValue);
            if(!returnValue)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * An application can call this method to request to be closed to the Library. 
     * @param password                                      the integer that identify the application who calls tis method.
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean requestForClosing(int password) throws AccessControlException
    {
        String appAssociated = SettingsManager.getInstance().getAppFromPassword(password);
        if(VERBOSE) System.out.println("Richiesta la chiusura dell'applicazione da parte dell'applicazione: "+appAssociated);
        String[] appUser = SettingsManager.getInstance().getConnectedUsers(appAssociated);
        for(int i=0; i<appUser.length; i++)
        {
            if(VERBOSE) System.out.println("Disconnetto l'utente: "+appUser[i]);
            ThreadManager.getInstance().requestForUserDisconnection(password, appUser[i]);
        }
        
        notifyClosingToApp(appAssociated);
        return true;
    }
    
    /**
     * An application can call this method to request for an user disconnection to the Library.
     * The user must be registered for that application.
     * @param password                                      the integer that identify the application who calls tis method.
     * @param IDUser                                        the ID Bluetooth that identify the user that application wants disconnet.
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct, or when the IDUser
     *                                                      is not registered for the application that password represent.
     */
    public void requestForUserDisconnection(int password, String IDUser) throws AccessControlException
    {
        if(IDUser == null) return;
        if (VERBOSE)
            System.out.println ("TM: Richiesta disconnessione");
        if(SettingsManager.getInstance().couldAllowToManageUser(password, IDUser))
            ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().NORMAL);
    }
    
    /**
     * Notify the exit command to the application.
     * @param application       The Application Name  
     * @return                  true if the exit command was being notifyed, false otherwise.
     */
    protected boolean notifyClosingToApp(String application)
    {
        return stopThread(application);
    }
    
    /**
     * Get the LinkBuildApp Object associated to the application.
     * @param application       the Application Name  
     * @return                  the LinkBuildApp object for the application.
     */
    protected LinkBuildApp getAppFromName(String application)
    {
        return ((ThreadTracker)(activeThreads.get(application))).getClassAssociated();
    }
    
    /**
     * Get the SwingWorker thread associated to the application
     * @param application       the Application Name  
     * @return                  the SwingWorker thread for the application.
     */
    protected SwingWorker getThreadFromName(String application)
    {
        return ((ThreadTracker)(activeThreads.get(application))).getThreadAssociated();
    }
    
    /**
     * It is called when the Library has to be power off so it can nicely close
     * the ThreadManager and all object, threads and application that it mange.
     * @return                  true if all is closed and perfect destroyed, false otherwise.
     */
    protected static boolean onClosing()
    {
        if (instance == null)
            return true;
        else
        {
            return instance.notifyClosingToAllApp(); 
        }
    }
        
    /**
     * It is called to stop a thread and close se application tha it manage.
     * @param application       the Application Name  
     * @return                  true if the threas was being correctly stopped, false otherwise.
     */
    private boolean stopThread(String application)
    {
        SwingWorker worker = getThreadFromName(application);
        LinkBuildApp app = getAppFromName(application);
        boolean readyToClose = app.onClosing();
        
        worker.interrupt();
        activeThreads.remove(application);
        return !SettingsManager.getInstance().setAppRunning(application, false);
    }
 }

class ThreadTracker
{
    private SwingWorker swk;
    private LinkBuildApp lba;
    
    /**
     * Create a new ThreadTracker object.
     * @param thread            The SwingWorker that ThreadTracker must manage.
     * @param application       the Application Name  
     */
    public ThreadTracker(SwingWorker thread, LinkBuildApp application)
    {
        this.swk = thread;
        this.lba = application;
    }
    
    /**
     * Get the thread that the ThreadWorker manage.
     * @return                  the SwingWorker thread managed by ThreadWorker.
     */
    public SwingWorker getThreadAssociated()
    {
        return this.swk;
    }
    
    /**
     * Get the LinkBuildApp object that the ThreadWorker manage.
     * @return                  the LinkBuildApp object managed by ThreadWorker.
     */
    public LinkBuildApp getClassAssociated()
    {
        return this.lba;
    }
}


