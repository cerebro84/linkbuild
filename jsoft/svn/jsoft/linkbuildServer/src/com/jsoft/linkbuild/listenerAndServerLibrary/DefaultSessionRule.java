/**
 * DefaultSessionRule is an implementation of a SessionRule.
 * It simply manage session for the user, by using some information.
 * <pre>
 *    DafaultSessionRule dsr = new DefaultSessionRule(229940303);
 * </pre>
 * @author      Massimo Domenico Sammito
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

import java.util.*;

public class DefaultSessionRule implements SessionRule
{

    private int password;
    private Hashtable numberOfConnection;
    
    /**
     * Create a new istance of DefaultSessionRule by associating it to the password of the application who has called it.
     * @param password                  the password associated to the application that has create this object and wich
     *                                  we want manage session for.
     */
    public DefaultSessionRule(int password)
    {
        this.password = password;
        this.numberOfConnection = new Hashtable<String,Integer>(); //IDUser / numero di connessioni
    }
    
    /**
     * This method builds the fields that represent a session.
     * It creates a String Array of length 6 where:
     * the index 0 contains: the start session time in milliseconds
     * the index 1 contains: the ID Bluetooth that represents the user
     * the index 2 contains: the name of the application used by user
     * the index 3 contains: the max time of non communication client/server | server/client for the application
     * the index 4 contains: the remaining time of non communication client/Server for the user.
     * the index 5 contains: the number of connection that has made the user since the server was bootstrap up.
     * 
     * It is called from SessionManager.
     * @param IDUser                     the ID Bluetooth associated to the user.
     * @param application                the name of application   
     * @return                           the String Array containing the session fields.   
     */
    public Object[] buildFieldsForUser(String IDUser, String application)
    {
        String[] campi= new String[6];
        campi[0] = ""+System.currentTimeMillis(); //tempo in millisecondi inizio sessione
        campi[1] = ""+IDUser; //id bluetooth dell'utente
        campi[2] = ""+application; //applicazione associata all'utente
        campi[3] = ""+SettingsManager.getInstance().getMaxTimePingForApp(getPasswordForApp()); //massimo tempo di non comunicazione fra client e server per l'applicazione associata alla password
        campi[4] = ""+SettingsManager.getInstance().getRemainingTimeBeforePingOut(getPasswordForApp(), IDUser); //tempo rimanente prima di essere disconnesso per ping out
        
        if(!this.numberOfConnection.containsKey(IDUser))
        {
            this.numberOfConnection.put(IDUser, new Integer(1));
        }
        else
        {
           int conn = (Integer)this.numberOfConnection.get(IDUser);
           conn += 1;
           this.numberOfConnection.remove(IDUser);
           this.numberOfConnection.put(IDUser, conn);
        }
        
        campi[5] = ""+this.numberOfConnection.get(IDUser);
                
        return campi;
    }

    /**
     * Return the password associated to the application that this rule manage.
     * It is called from SessionManager.
     * @return                          the password associated to the application that is managed by this rule.
     */
    public int getPasswordForApp()
    {
        return this.password;        
    }
    
    /**
     * Make some analysis by comparing the Object fields recevied from the SessionManager and some new paramether 
     * obtening istantly.
     * 
     * It is called from Application.
     * @param fields                    the String array containing the fields represent the session.
     * @return                          the String that describe info about Session.
     */
    public String makeNiceAnalysis(String[] fields)
    {
        String analisi = "";
        Long a = Long.parseLong(fields[0]);
        Long b = System.currentTimeMillis();
        Long c = b-a;
        analisi += "L'utente con ID bluetooth: "+fields[1]+
                " registrato all'applicazione: "+fields[2]+
                " si è connesso di nuovo all'applicazione per la: "+fields[5]+
                ". \n Ha iniziato la sessione al tempo: "+fields[0]+
                ". Ha ripristinato la sessione al tempo: "+b+
                ". Quindi è passato: "+c+
                " di tempo prima che la sessione venisse ripristinata.\n Il tempo massimo di non comunicazione fra il client e il server per l'applicazione in questione è: "+fields[3]+
                " e l'utente ha ancora a disposizione: "+fields[4]+"\n"; 
        return analisi;
    }
}
