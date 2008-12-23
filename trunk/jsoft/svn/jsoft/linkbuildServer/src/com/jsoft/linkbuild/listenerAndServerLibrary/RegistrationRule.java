package com.jsoft.linkbuild.listenerAndServerLibrary;

/**
 * This interface describe a Regitration Rule.
 * The metod to accpet registration is acceptRegistration(), this method will must implement for manage
 * the process registration.
 * 
 * Example:
 * 
 * <pre>
 * class Rule implements RegistrationRule
 * {
 *  String[] fields;
 *  String favorite_address;
 *  int max_fields = 2;
 *  public boolean checkLength(String[] fields)
 *  {
 *      return (fields.length==max_fields);
 *  }
 *  public String[] getFields()
 *  {
 *      return fields;
 *  }
 *  public void setFields(String[] fields)
 *  {
 *      this.fields=fields;
 *  }
 *  public boolean acceptRegistration(String address, String[] remote_fields) 
 *  {
 *      if (checkLength(remote_fields) && address=favorite_address)
 *          return true;
 *      return false;
 *  }
 *}
 * </pre>
 * 
 * @author      Christian Rizza
 */
public interface RegistrationRule 
{    
    /**
     * This method will must implements by application writer.
     * See the example
     * @param fields                    Client's fields
     * @return                          true, if method rules was passed
     */
    public boolean checkLength(String[] fields);
    /**
     * This method will must implements by application writer.
     * See the example
     * 
     * @return fields
     */
    public String[] getFields();
    /**
     * This method will must implements by application writer.
     * See the example
     * 
     * @param fields                    Client's fields
     */
    public void setFields(String[] fields);
    
    
    /**
     * Thi metod is called by RegistrationManager to verify registration procedure.
     * 
     * @param address                   User's Bluetooth
     * @param remote_fields             The remote fields on the client
     * @return                          true, if registration was completed, false otherwise
     */
    public boolean acceptRegistration(String address, String[] remote_fields);
}
