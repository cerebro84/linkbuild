/*
 * JSoft-Link&Bulid
 * 
 */
package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.util.*;
import java.security.*;
import java.io.*;
import javax.swing.*;
import com.jsoft.linkbuild.utility.*;
/**
 *
 * @author TheOne
 */
public class BanManager
{
    //Debugging constants
    public final static Boolean VERBOSE = true; //enables some System.out.prinln
   
    private static BanManager instance = null; // for singleton
    private Hashtable appTable;
    private Hashtable bUsers;
    private final String SYSPATH = "Data"+File.separator+"bannedUsers.dat";
    private final String APPPATH = "Application"+File.separator;
    
    /**
     * private constructor for initialize only once
     */
    private BanManager()
    {
        appTable = new Hashtable<String, ArrayList >();
        bUsers = new Hashtable<String, BannedUser>();
        FileManager.createFile(SYSPATH);
        if(FileManager.fileIsEmpty(SYSPATH) == 0)
        {/**load all banned users*/
            Object users[] = FileManager.objectList(SYSPATH);
            
            for(int i = 0; i < users.length; i++)
            {
                bUsers.put(((BannedUser)users[i]).getUser(),users[i]);
            }
        }
        /**load system banning rules*/
        Setting conf = SettingsManager.getInstance().getSettingForSys();
        BanningRule br[] = new BanningRule[1];
        br[0] = conf.getBan();
        this.addBanManagerFor("SYSTEM", br);
    }
    /**
     * 
     * @return return instanzied object BanManager
     */
    public static BanManager getInstance() 
    {
      if(instance == null)
      {
         instance = new BanManager();
      }
      return instance;
    }
    /**
     * add a banning rules list to an application identified by password
     * @param password the password to identify the application
     * @param rule an array of banning rules
     * @return return true if all is done
     * @throws java.security.AccessControlException
     */
    public boolean addBanManagerFor(int password, BanningRule rule[]) throws AccessControlException
    {
        String appName = SettingsManager.getInstance().getAppFromPassword(password);
   
        ArrayList rules;
        if(appTable.containsKey(appName))
            rules = (ArrayList) appTable.get(appName);
        else
            rules = new ArrayList();
        for(int i = 0; i < rule.length; i++)
            rules.add(rule[i]);
        appTable.put(appName, rules);
        return true;
    }
    /**
     * add banning rules for <appName> application
     * @param appName applicaton name to add banning rules
     * @param rule the list of banning rules for this application
     * @return return true if all is done
     */
    protected boolean addBanManagerFor(String appName, BanningRule rule[]) 
    {
        ArrayList rules;
        if(appTable.containsKey(appName))
            rules = (ArrayList) appTable.get(appName);
        else
            rules = new ArrayList();
        for(int i = 0; i < rule.length; i++)
            rules.add(rule[i]);
        appTable.put(appName, rules);
        return true;
    }
    /**
     * remove the banning rules for this application indentified by password
     * @param password an int to indentify the application
     * @return true if all is done
     * @throws java.security.AccessControlException
     */
    public boolean removeBanManagerFor(int password) throws AccessControlException
    {
        String appName = SettingsManager.getInstance().getAppFromPassword(password);
        
        if(appTable.containsKey(appName))
        {
            appTable.remove(appName);
            return true;
        }
      return false;
    }
    
    /**
     * remove the banning rules for this application indentified by password
     * @param appName the application name
     * @return true if all is done
     */
    protected boolean removeBanManagerFor(String appName) 
    {   
        if(appTable.containsKey(appName))
        {
            appTable.remove(appName);
            return true;
        }
      return false;
    }
    /**
     * set Banning rule for an application indentified by password
     * @param password an int to indentify the application
     * @param rule banning rules to be setted for the application
     * @return return true if all is done
     * @throws java.security.AccessControlException
     */
    public boolean setRuleForApp(int password, BanningRule rule) throws AccessControlException
    {
        String appName = SettingsManager.getInstance().getAppFromPassword(password);
        
        if(appTable.containsKey(appName))
        {
            ArrayList rules = (ArrayList)appTable.get(appName);
            rules.add(rule);
            appTable.put(appName, rules);
            return true;
        }
       return false;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, String sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, int sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, String[] sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, boolean sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, byte sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, char sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * manage banning for current user identified by <IDUser> 
     * @param IDUser user id, it may be bluetooth indentifier
     * @param application the application that user must be controlled for ban
     * @param sentence the sentence that user sent to the application
     * @return true if user pass all banning test, it's mean that user can sent the message
     */
    protected boolean manageBanningFor(String IDUser, String application, byte[] sentence)
    {
        ArrayList rules;
        Object bf[];
        boolean verdict = true;
        rules = (ArrayList)appTable.get("SYSTEM");
        bf = rules.toArray();
        for(int i = 0; i < bf.length; i++)
        {
            String result = ((BanningRule)bf[i]).sentenceScan(sentence);
            if(result.startsWith("false"))
            {
                verdict = false;
                banThisUser(IDUser, application, result);
            }
        }
        if(appTable.containsKey(application))
        {
            rules = (ArrayList)appTable.get(application);
            bf = rules.toArray();
            for(int i = 0; i < bf.length; i++)
            {
                String result = ((BanningRule)bf[i]).sentenceScan(sentence);
                if(result.startsWith("false"))
                {
                    verdict = false;
                    banThisUser(IDUser, application, result);
                }
            }
        }
        if(!verdict)
             ConnectionServer.getInstance().removeConnection(IDUser, ConnectionServer.getInstance().SYSBAN); 
        return verdict;
    }
    /**
     * method to know the reason for what the user is banned
     * @param IDUser user identifier
     * @return the reason for what user is banned (if he is banned)
     */
    protected String tellMeWhy(String IDUser)
    {
        if(bUsers.isEmpty())
            return "No one is banned!";
        int i = 0;
        
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            if(aux.getUser().equals(IDUser))
                return aux.getReasons();
            i++;
        }
        return IDUser+" isn't banned";
    }
    
    /**
     * control if user is banned, it can be an applicaton's ban or system
     * @param IDUser user identifier
     * @return return true if user is banned
     */
    //protected boolean isBanned(String IDUser)
    protected boolean isBanned(String IDUser)
    {
        if(bUsers.isEmpty())
            return false;
        if(bUsers.containsKey(IDUser))
            return true;
        return false;
    }
    
    /**
     * control if this user is benned from system
     * @param IDUser user identifier
     * @return true if the user is banned from system
     */
    protected boolean isSysBanned(String IDUser)
    {
        if(bUsers.isEmpty())
            return false;
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            if(aux.sysFlag)
                return true;
        }
        return false;
    }
    /**
     * control if this user is benned from <applicatoin>
     * @param IDUser user identifier
     * @return true if the user is banned from <application>
     */
    protected boolean isAppBanned(String IDUser, String application)
    {
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            if(aux.appFlag && aux.isBannedApp(application))
                return true;
        }
        return false;
    }
    /**
     * the first method that call when an uset want to connect to the library
     * @param IDUser user identifier
     * @param application application name
     * @return return true if this user can access
     */
    protected boolean couldAccess(String IDUser, String application)
    {
        boolean userControl = this.isSysBanned(IDUser) || this.isAppBanned(IDUser, application);
        if (VERBOSE) System.out.println("BanManager: utente bannato? "+userControl);
        if(userControl)
            return false;
        if(!appTable.containsKey(application))
            setRulesFor(application);
        if (VERBOSE) System.out.println("BanManager: a questo punto dico che l'utente può entrare");
        return true;
    }
    /**
     * remove the system ban for this user
     * @param IDUser user identifier
     * @return return true if all is done
     */
    protected boolean removeSysBan(String IDUser)
    {
        if(bUsers.isEmpty())
            return false;
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            if(aux.sysFlag)
            {
                aux.removeSys();
                bUsers.put(IDUser, aux);
                this.isStillBanned(IDUser);
                return true;
            }
        }   
        return false;
    }
    /**
     * remove the application ban for this user, (applicatoin is identified by the id password)
     * @param IDUser user identifier
     * @param password application identifier
     * @return return true if all is done
     */
    public boolean removeAppBan(int password, String IDUser) throws AccessControlException
    {
        String appName = SettingsManager.getInstance().getAppFromPassword(password);
        if(bUsers.isEmpty())
            return false;
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            if(aux.appFlag && aux.isBannedApp(appName))
            {
                aux.removeApp(appName);
                bUsers.put(IDUser, aux);
                this.isStillBanned(IDUser);
                return true;
            }
        }
        return false;
    }
    /**
     * add the banning rules of this application in to the hashtable
     * @param application: application name
     */
    private void setRulesFor(String application)
    {
        Setting conf = SettingsManager.getInstance().getSettingForApp(application);
        BanningRule br[] = new BanningRule[1];
        br[0] = conf.getBan();
        if (VERBOSE) System.out.println("BanManager: caricato il BanningRule per l'applicazione");
        this.addBanManagerFor(application, br);
    }
    /**
     * the methid to ban an user if he break some rules
     * @param IDUser: user identifier
     * @param appName: application name
     * @param reasons: reason for what this user is banned
     */
    private void banThisUser(String IDUser, String appName, String reasons)
    {
        String reas[] = reasons.split(":");
        if(bUsers.containsKey(IDUser))
        {
            BannedUser aux = (BannedUser)bUsers.get(IDUser);
            aux.setReason(appName,reas);
            bUsers.put(IDUser, aux);
            this.writeBanList();
            return;
        }
        BannedUser bannedUser= new BannedUser(IDUser);
        bannedUser.setReason(appName,reas);
        if(appName.equals("SYSTEM"))
        {
            if (VERBOSE) System.out.println ("Ban1: Disconnetto");
            bannedUser.sysFlag = true;
        }
        else
        {
            if (VERBOSE) System.out.println ("Ban2: Disconnetto");
            bannedUser.appFlag = true;
        }
        bUsers.put(IDUser, bannedUser);
        this.writeBanList();
    }
    /**
     * write banning users into the banning list
     */
    private void writeBanList()
    {
        BannedUser bu[] = new BannedUser[bUsers.size()];
        Enumeration bannedUsers = bUsers.elements();
        int i = 0;
        while(bannedUsers.hasMoreElements())
        {
            bu[i] = (BannedUser) bannedUsers.nextElement();
        }
        FileManager.writeObject(SYSPATH, bu);
    }
    /**
     * control if this user is still banned
     * @param IDUser: user identifier
     */
    private void isStillBanned(String IDUser)
    {
        BannedUser aux = (BannedUser)bUsers.get(IDUser);
        if(aux.isStillBanned())
        {
            bUsers.remove(IDUser);
            writeBanList();
        }
    }
    /**
     * all is ready to be close
     * @return true;
     */
    public static boolean onClosing()
    {
        return true;
    }
}
