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
public class RootUser extends User implements Serializable
{
    private boolean debug_option=true;
    /**
     * root user object
     * @param user username
     * @param pass password
     */
    public RootUser(String user, String pass)
    {
            super(user,pass);
    }
    /**
     * 
     * @return ?
     */
    public boolean getDebugOption()
    {
        return debug_option;
    }
}