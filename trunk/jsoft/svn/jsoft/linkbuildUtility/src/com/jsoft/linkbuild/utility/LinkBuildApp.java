/**
 * This interface describe a LinkBuild application. All Class that implements it have to override these methods.
 * Only this methods are called within the ThreadManager to communicate with the application.
 * 
 * All Classes that implements this interface must implements a constructor that take as parameter
 * the password associated to the application that it must manage.
 * 
 * Es.:
 * <pre>
 * public class ExampleApplication implements LinkBuildApp
 * {
 *      private int password;
 * 
 *      public ExampleApplication(int password)
 *      {
 *          this.password = password;
 *      }
 * 
 *      public boolean newUserRegistered(String IDUser)
 *      {
 *          return false;
 *      }
 *  
 *      public boolean userDisconnected(String IDUser)
 *      {
 *          return false;
 *      }
 *  
 *      public boolean userDisconnectedForPing(String IDUser)
 *      {
 *          return false;
 *      }
 *  
 *      public boolean userDisconnectedForSysBan(String IDUser, String message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean userDisconnectedForAppBan(String IDUser, String message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean userRegistrationFields(String IDUser, String[] fields)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean onClosing()
 *      {
 *          return false;
 *      }
 * 
 *      public boolean stringIncoming(String IDUser, String message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean stringArrayIncoming(String IDUser, String[] messages)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean booleanIncoming(String IDUser, boolean message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean integerIncoming(String IDUser, int message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean byteIncoming(String IDUser, byte message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean charIncoming(String IDUser, char message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean byteArrayIncoming(String IDUser, byte[] message)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean logIncoming(String log)
 *      {
 *          return false;
 *      }
 * 
 *      public boolean userSessionFields(String IDUser, Object[] fields)
 *      {
 *          return false;
 *      }
 * } 
 * </pre>
 * @author      Massimo Domenico Sammito
 */

package com.jsoft.linkbuild.utility;

public interface LinkBuildApp 
{
   /**
    * This method is called from the ThreadManager when a new user is registered for the application
    * @param IDUser         the ID Bluetooth of the user
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean newUserRegistered(String IDUser);
    
    /**
    * This method is called from the ThreadManager when a new user is regulary disconnected.
    * @param IDUser         the ID Bluetooth of the user
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userDisconnected(String IDUser);
    
    /**
    * This method is called from the ThreadManager when a new user is not regulary disconnected.
    * @param IDUser         the ID Bluetooth of the user
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userAbnormalDisconnected(String IDUser);
    
    /**
    * This method is called from the ThreadManager when a new user is disconnected because of a ping out.
    * @param IDUser         the ID Bluetooth of the user
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userDisconnectedForPing(String IDUser);
    
    /**
    * This method is called from the ThreadManager when a new user is disconnected because of system ban.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the message that describes why user was banned.
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userDisconnectedForSysBan(String IDUser, String message);
        
    /**
    * This method is called from the ThreadManager when a new user is disconnected because of an application ban.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the message that describes why user was banned.
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userDisconnectedForAppBan(String IDUser, String message);
    
   /**
    * This method is called from the ThreadManager when a new user is logged in. 
    * It is called always after to call newUserRegistered(String IDUser) and before to call 
    * userSessionFields(String IDUser, Object[] fields).
    * @param IDUser         the ID Bluetooth of the user
    * @param fields         the String array containing the fields of the registration
    * @return               true if method correctly elaborate the operations, false otherwise.
    * @see                  #newUserRegistered(String IDUser)
    */
    public boolean userRegistrationFields(String IDUser, String[] fields);
    
    /**
    * This method is called from ThreadManager before to close tha application, (request from Server or 
    * request from application by using requestForClosing )
    * @return               true if method correctly elaborate the operations, false otherwise.
    * @see                  com.jsfot.linkbuild.listenerAndServerLibrary.ThreadManager#requestForClosing(int password) 
    */
    public boolean onClosing();
    
   /**
    * This method is called from ThreadManager when a string is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the String message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean stringIncoming(String IDUser, String message);
    
   /**
    * This method is called from ThreadManager when an array of string is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param messages       the Array of String message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean stringArrayIncoming(String IDUser, String[] messages);
    
   /**
    * This method is called from ThreadManager when a boolean is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the boolean message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean booleanIncoming(String IDUser, boolean message);
    
   /**
    * This method is called from ThreadManager when a int is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the int message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean integerIncoming(String IDUser, int message);
    
   /**
    * This method is called from ThreadManager when a byte is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the byte message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean byteIncoming(String IDUser, byte message);
    
   /**
    * This method is called from ThreadManager when a char is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param message        the char message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean charIncoming(String IDUser, char message);
    
   /**
    * This method is called from ThreadManager when an array of byte is coming from a client.
    * @param IDUser         the ID Bluetooth of the user
    * @param messages       the Array of bytes message come from IDUser
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean byteArrayIncoming(String IDUser, byte[] messages);
    
   /**
    * This method is called from ThreadManager when a new log text is coming for the application.
    * @param log            the log incoming 
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean logIncoming(String log);
    
   /**
    * This method is called from ThreadManager when a session for an application's user was rescued.
    * It is called always after to call newUserRegistered(String IDUser) and after to call
    * userRegistrationFields(String IDUser, String[] fields)
    * @param IDUser         the ID Bluetooth of the user
    * @param fields         the array of Object that contains the fields represent the session
    * @return               true if method correctly elaborate the operations, false otherwise.
    */
    public boolean userSessionFields(String IDUser, Object[] fields);
}
