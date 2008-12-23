/*
 * JSoft-Link&Bulid
 * 
 */
package com.jsoft.linkbuild.utility;

import java.io.*;
import java.util.*;
/**
 * 
 * @author TheOne
 */
public class BannedUser implements Serializable
{
    private String user;
    private Hashtable reason;
    public boolean sysFlag;
    public boolean appFlag;
    /**
     * 
     * @param name username, it may be the bluetooth id
     */
    public BannedUser(String name)
    {
        user = name;
        reason = new Hashtable<String, ArrayList >();
        sysFlag = false;
        appFlag = false;
    }
    /**
     * set ban's reason for this user
     * @param app application name
     * @param cause reason of ban
     */
    public void setReason(String app, String cause)
    {
        appFlag = true;
        ArrayList reas = new ArrayList();
        reas.add(cause);
        reason.put(app, reas);
        
    }
    /**
     * set ban's reasons for this user
     * @param app application name
     * @param cause reasons of ban
     */
    public void setReason(String app, String[] cause)
    {
        appFlag = true;
        ArrayList reas = new ArrayList();
        for(int i = 1; i < cause.length; i++)
            reas.add(cause[i]);
        reason.put(app, reas);
    }
    /**
     * return the username, it may be bluetooth id
     * @return user;
     */
    public String getUser()
    {
        return user;
    }
    /**
     * control if this user is banned for <app> application
     * @param app application name
     * @return return true if this user is banned for <app> application
     */
    public boolean isBannedApp(String app)
    {
        if(reason.containsKey(app))
            return true;
        return false;
    }
    /**
     * remove ban for <app> application
     * @param app applicatoin name
     */
    public void removeApp(String app)
    {
        System.out.println(reason.remove(app));
    }
    /**
     * remove the system ban for this user
     */
    public void removeSys()
    {
        sysFlag = false;
        reason.remove("SYSTEM");
    }
    /**
     * 
     * @return true if this user is still banned
     */
    public boolean isStillBanned()
    {
        return reason.isEmpty();
    }
    /**
     * 
     * @return all reasons for what this user is banned
     */
    public String getReasons()
    {
        String s = "Reasons:\n";
        Enumeration apps = reason.keys();
        Enumeration cause = reason.elements();
        while (apps.hasMoreElements())
        {
            s+=(String)apps.nextElement();
            if(cause.hasMoreElements())
            {
                Object str[] = ((ArrayList) cause.nextElement()).toArray();
                for(int i = 0; i < str.length;i++)
                    s+=", "+str[i];
            }
            s+="\n";
        }
        return s;
    }
}