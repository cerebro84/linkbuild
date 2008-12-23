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
public class AdminUser extends User implements Serializable
{
    /**
     * the admin users class
     * @param user username   
     * @param pass password
     */
    public AdminUser(String user, String pass)
    {
            super(user,pass);
    }
}
