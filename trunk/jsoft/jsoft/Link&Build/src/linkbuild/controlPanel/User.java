/*
 * JSoft-Link&Bulid
 * 
 */

package linkbuild.controlPanel;

/**
 *
 * @author TheOne
 */

import java.io.*;
/*common user*/
class User implements Serializable
{
	int username;
	int password;
	private String u;
	public User(String user, String pass)
	{
		username  = user.hashCode();
		password = pass.hashCode();
		u = user;
	}
        /*compare current user with [obj] user, only with username*/
	public boolean compareTo(User obj)
	{
		if(obj.username == username)
			return true;
		return false;
	}
        /*compare current user with [obj] user*/
	public boolean equals(User obj)
	{
		if (obj.username == username && obj.password == password)
			return true;
		return false;
	}
        /*return the username*/
	public String getUsername()
	{
		return u;
	}
        /*set password of current user*/
	public void setPassword(String pass)
	{
		password = pass.hashCode();
	}
}
/*admin user class*/
class AdminUser extends User implements Serializable
{
	public AdminUser(String user, String pass)
	{
		super(user,pass);
	}
}
/*root user class*/
class RootUser extends User implements Serializable
{
	public RootUser(String user, String pass)
	{
		super(user,pass);
	}
}