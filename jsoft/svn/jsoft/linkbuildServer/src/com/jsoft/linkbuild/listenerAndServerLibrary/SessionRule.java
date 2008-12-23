
package com.jsoft.linkbuild.listenerAndServerLibrary;

/**
 * This interface describe a Session Rule. All Class that implements it have to override these methods.
 * Only this methods are called within the SessionManager to manage the Session with the Rule that this
 * object represent.
 * 
 * All Classes that implements this interface must implements a constructor that take as parameter
 * the password associated to the application that it must manage.
 * 
 * Es.:
 * public class ExampleSessionRule implements SessionRule
 * {
 *      private int password;
 * 
 *      public ExampleSessionRule(int password)
 *      {
 *          this.password = password;
 *      }
 * 
 *      public int getPasswordForApp()
 *      {
 *          return this.password;
 *      }
 * 
 *      public Object[] buildFieldsForUser(String IDUser, String application)
 *      {
 *            Object[] fields;
 *            //codice per produrre l'array fields
 *            return fields;          
 *      }
 * 
 * } 
 * @author      Massimo Domenico Sammito
 */
public interface SessionRule 
{
    /**
     * This method return the password associated to the application. 
     * @return                  the password associated to the application.
     */
    public int getPasswordForApp(); 
    
    /**
     * This method is called to the SessionManager to build the fields for the application to manage the
     * session for the user.
     * @param IDUser                    the ID Bluetooth of the user 
     * @param application               the name of the application
     * @return                          the array of object represent the fields for the session.
     */
    public Object[] buildFieldsForUser(String IDUser, String application);

}
