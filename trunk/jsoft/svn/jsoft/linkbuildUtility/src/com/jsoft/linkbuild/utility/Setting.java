/*
 * JSoft-Link&Bulid
 * 
 */
package com.jsoft.linkbuild.utility;

import java.io.*;
/**
 * 
 * @author TheOne
 */
public class Setting implements Serializable
{
    private boolean logFlag;
    private boolean sessFlag;
    private boolean regFlag;
    private boolean debugFlag;
    private int maxClient;
    private int maxAppUsers;
    private int inacTime;
    private BanConf bf;
    /**
     * this object store all setting of the system or all setting of an application
     */
    public Setting ()
    {
        bf = new BanConf();
        logFlag = false;
        sessFlag = false;
        regFlag = false;
        debugFlag = false;
        maxClient = 0;
        maxAppUsers = 0;
        inacTime = 0;
    }
    /**
     * 
     * @param flag turn on/off log system
     */
    public void setLog (boolean flag)
    {
        logFlag = flag;
    }
    /**
     * 
     * @param flag turn on/off sessions management system
     */
    public void setSession (boolean flag)
    {
        sessFlag = flag;
    }
    /**
     * 
     * @param flag turn on/off registration system
     */
    public void setRegistration (boolean flag)
    {
        regFlag = flag;
    }
    /**
     * 
     * @param n max clients number can connect at simultaneously
     */
    public void setClientsNumber (int n)
    {
        maxClient = n;
    }
    /**
     * 
     * @param n max clients number can be registered for an application
     */
    public void setAppUsersNumber (int n)
    {
        maxAppUsers = n;
    }
    /**
     * 
     * @param n max time an client can be inactive
     */
    public void setInactivityTime (int n)
    {
        inacTime = n;
    }
    /**
     * 
     * @param flag turn on/off debug system
     */
    public void setDebug (boolean flag)
    {
        debugFlag = flag;
    }
    /**
     *bannig rules  
     * @param b BanConf object
     */
    public void setBan (BanConf b)
    {
        bf = b;
    }
    /**
     * 
     * @return information of Log System
     */
    public boolean getLog ()
    {
        return logFlag;
    }
    /**
     * 
     * @return information of Sessions Management System
     */
    public boolean getSession ()
    {
        return sessFlag;
    }
    /**
     * 
     * @return information of Registration System
     */
    public boolean getRegistration ()
    {
        return regFlag;
    }
    /**
     * 
     * @return information of Debug System
     */
    public boolean getDebug()
    {
        return debugFlag;
    }
    /**
     * 
     * @return max clients can access simultaneously
     */
    public int getClientsNumber ()
    {
        return maxClient;
    }
    /**
     * 
     * @return max number can be register for an application
     */
    public int getAppUsersNumber ()
    {
        return maxAppUsers;
    }
    /**
     * 
     * @return max time an user can be inactive in second
     */
    public int getInactivityTime ()
    {
        return inacTime;
    }
    /**
     * 
     * @return max time an user can be inactive in mili seconds
     */
    public long getInterruptionTime()
    {
        return inacTime * 1000;
    }
    /**
     * 
     * @return banning configuration object
     */
    public BanConf getBan ()
    {                
        return bf;
    }
}
