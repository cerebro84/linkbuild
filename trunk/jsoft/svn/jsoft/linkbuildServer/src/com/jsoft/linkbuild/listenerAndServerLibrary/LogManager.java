package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.io.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.*;
import java.security.*;
import com.jsoft.linkbuild.utility.*;

/**
 * LogManager creates and maintains the unique Logger for the applications.
 * The applicazions decides to add and remove logger in single class with 
 * opportune method.
 * The LogManager class is a singleton and its constructor is hidden to developer.
 * 
 * <pre>
 *    LogManager lm = LogManager.getInstance();
 * </pre>
 * @author      Christian Rizza
 */
public class LogManager
{
    private static LogManager instance =null;    
    private final static String SYS_LOG = "Data"+File.separator+"sys.log";
    private Hashtable<String, Hashtable> logged;
    private Logger log = Logger.getLogger("MANAGER");
    private Handler hd=null;
    private String APP_LOG="Application";
    
    //Costanti
    public static final String SESSION = "SessionManager";
    public static final String BANNING = "BanningManager";
    public static final String REGISTRATION = "RegistrationManager";
    
    /**
     * Class Contructor
     */
    private LogManager()
    {
        try
        {
            logged = new Hashtable<String, Hashtable>();
            log.setLevel(Level.parse("ALL"));
            hd = new FileHandler(SYS_LOG, true);
            hd.setFormatter(new logFormat());
            log.addHandler(hd);
        }
        catch (IOException ex)
        {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SecurityException ex)
        {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Return a unique instance of LogManager
     * @return LogManager instance
     */
    public static LogManager getInstance()
    {
        if (instance==null)
        {
            instance = new LogManager();
        }
        return instance;
    }
    /**
     * This methos is called from the applications, and it is used to set 
     * Redirect options. See the user manual to see more informtion to Redirect Option Service
     * 
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true if redirect option was setted for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean setRedirect(int password) throws AccessControlException
    {
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (ConnectionServer.VERBOSE) System.out.println("app_name di setRedirect() "+ app_name);
        if (isRedirect(password))
            return false;
        
        if (logged.get(app_name)==null) return false;
        
        logged.get(app_name).put("Redirect", true);
        return true;
    }
    
    /**
     * This methos is called from the applications, and it is used to remove 
     * Redirect options. See the user manual to see more informtion to Redirect Option Service
     * 
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true if redirect option was removed for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean removeRedirect(int password) throws AccessControlException
    {
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (!isRedirect(password))
            return false;
        
        if (ConnectionServer.VERBOSE) System.out.println("Setto il refirect in LM");
        logged.get(app_name).remove("Redirect");
        return true;
    }
/**
     * This methos is called from the applications, and it is used to get a state of
     * Redirect options. See the user manual to see more informtion to Redirect Option Service
     * 
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true if redirect option was setted for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean isRedirect(int password) throws AccessControlException
    {
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (logged.get(app_name)!=null)
            return logged.get(app_name).containsKey("Redirect");
        return false;
    }
    
    private boolean isRedirect(String app_name)
    {
        if (app_name==null) return false;
        return logged.get(app_name).containsKey("Redirect");
    }
    
    /**
     * This methos is called from the applications, and it is used to add 
     * one Logger into System Class Library identified by Option Service
     * See the user manual to see more information for LogManager
     * 
     * @param password                                      the password associated to the application that calls the method
     * @param opt                                           the option service rappresent a System Class Library and it refeared by Contants
     * @return                                              true if LogManager was added a Logger for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean addLogger(int password, String opt) throws AccessControlException
    {
        if (opt==null) return false;
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if(!SettingsManager.getInstance().getSettingForApp(app_name).getLog())
            return false;
        
        if (!logged.containsKey(app_name))
        {
            
            Hashtable<String, String> tmp = new Hashtable<String, String>();
            tmp.put("Service", opt);
            logged.put(app_name, tmp);
            return true;
        }
        else if (logged.get(app_name).contains(opt))
        {
            return false;
        }
        else
        {
            logged.get(app_name).put(opt, app_name);
            return true;
        }
    }
    
    /**
     * This methos is called from the applications, and it is used to remove 
     * All Logger on All System Class Library and detach all service logger by Library
     * See the user manual to see more information for LogManager
     * 
     * @param password                                      the password associated to the application that calls the method
     * @return                                              true is all Logger was dictivated, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean removeAllLogger(int password) throws AccessControlException
    {
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (logged.containsKey(app_name))
        {
            logged.remove(password);
            return true;
        }
       
        return false;
    }
    /**
     * This methos is called from the applications, and it is used to remove 
     * one Option Service Logger into System Class Library
     * See the user manual to see more information for LogManager
     * 
     * @param password                                      the password associated to the application that calls the method
     * @param opt                                           the option service rappresent a System Class Library and it refeared by Contants
     * @return                                              true if LogManager was removed a Option Service Logger for application, false otherwise
     * @throws java.security.AccessControlException         It is sent when the password is null, or not correct.
     */
    public boolean removeLogger(int password, String opt) throws AccessControlException
    {
        if (opt==null) return false;
        String app_name = SettingsManager.getInstance().getAppFromPassword(password);
        if (logged.containsKey(app_name) && logged.get(app_name).isEmpty())
        {
            removeAllLogger(password);
        }
        else if (logged.containsKey(app_name) && logged.get(app_name).contains(opt))
        {
            logged.get(app_name).remove(opt);
        }
        
        return false;
    }
    /**
    * This methos is called from the Library writer, and it is used to write 
    * a log session on system path
    * 
    * @param _class         Class to will be logged
    * @param address        Bluetooth address
    * @param message        Message to will be write
    */
    protected void makeLog(Class _class, String address, String message)
    {
        if (_class==null) 
        {
            log.severe("LogManager - Class not found");
            return ;
        }
        if (_class!=SettingsManager.class && SettingsManager.getInstance().getRootDebugOption())
        {
            writeLog(_class.getSimpleName(), address, message);
        }
        else if (_class==SettingsManager.class)
        {
            writeLog(_class.getSimpleName(), address, message);
        }
        
        //Controllo se Ã¨ attivo il servizio di log.
        String app_name=SettingsManager.getInstance().getApplicationUsedByUser(address);
        if (app_name != null && logged.containsKey(app_name))
        {
            if (!SettingsManager.getInstance().isLoggedApp(app_name)) //Se l'applicazione nn Ã¨ loggabile 
                return ;
            if (isRedirect(app_name))
            {
                ThreadManager.getInstance().notifyLogToApp(app_name, message);
            }
            else    //Scrivo sul file.
            {
                try
                {
                    Handler hd_app = new FileHandler(APP_LOG + File.separator + app_name+"app.log", true);
                    hd_app.setFormatter(new logFormat());
                    log.addHandler(hd_app);
                    log.info(_class + " " + message);
                    log.removeHandler(hd_app);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (SecurityException ex)
                {
                    Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    /**
     * This methos is called from the applications, and it is used to write 
     * log messagge in local log path by Applications.
     * See the user manual to see more information for LogManager
     *      
     * @param _class            class where thi method was called.
     * @param password          the password associated to the application that calls the method
     * @param message           message written in log file.
     */
    public void makeLog(Class _class, int password, String message)
    {
        if (_class==null || message==null)
                return ;
        try
        {
            String app_name = SettingsManager.getInstance().getAppFromPassword(password);
            Handler hd_app = new FileHandler(APP_LOG + File.separator + app_name+"app.log", true);
            hd_app.setFormatter(new logFormat());
            log.addHandler(hd_app);
            log.info(_class + " " + message);
            log.removeHandler(hd_app);
        }
        catch (IOException ex)
        {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SecurityException ex)
        {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *  Metodo interno oer gestire la scrittura su files.  
     * 
     */
    private void writeLog(String class_name, String id_blue, String message)
    {
        if (FileManager.fileIsEmpty(SYS_LOG) == -1)
        {
            FileManager.createFile(SYS_LOG);
        }
        log.severe(class_name + " " + id_blue + " " + message);
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


class logFormat extends Formatter
{
    public logFormat()
    {
        super();
    }
    public String format(LogRecord record)
    {
        StringBuffer sb = new StringBuffer();
	
	// Get the date from the LogRecord and add it to the buffer
        Date date = new Date(record.getMillis());
	sb.append(date.toString());
	sb.append(" ");
        
        // Get the level name and add it to the buffer
        if (record.getLevel().getName().equals("SEVERE"))
            sb.append("root");
        else
            sb.append("user");
	sb.append(" ");
        
	// Get the formatted message (includes localization 
	// and substitution of paramters) and add it to the buffer
	sb.append(formatMessage(record));
	sb.append("\n");

        return sb.toString();
    }
}