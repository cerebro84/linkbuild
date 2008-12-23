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
public class User implements Serializable
{
    int username;
    int password;
    private String u;
    /**
     * abstract class for root user and admin users
     * @param user username
     * @param pass password
     */
    public User(String user, String pass)
    {
            username  = user.hashCode();
            password = pass.hashCode();
            u = user;
    }
    /**
     * 
     * @param obj user to compare
     * @return true if this.username == obj.username
     */
    public boolean compareTo(User obj)
    {
            if(obj.username == username)
                    return true;
            return false;
    }
    /**
     * 
     * @param obj = user to compare
     * @return return true if this object are some to obj
     */
    public boolean equals(User obj)
    {
            if (obj.username == username && obj.password == password)
                    return true;
            return false;
    }
    /**
     * 
     * @return username, mean the name of uset and not the hash code
     */
    public String getUsername()
    {
            return u;
    }
}
