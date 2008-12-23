/**
 * StartListener is the Main Class called by user to 
 * start and activate the ListenerLibrary component.
 * @author      Massimo Domenico Sammito
 */

package com.jsoft.linkbuild.listenerAndServerLibrary;

public class StartListener 
{
    /**
    * main starts the ThreadManager and lunch the bootstrap
    * of the ListenerLibrary.  
    * @param args   it must be null
    * @see          ThreadManager 
    */
    public static void main(String[] args)
    {
        ThreadManager th = ThreadManager.getInstance();
    }   
}
