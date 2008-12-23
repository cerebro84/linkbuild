package tests;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jsoft.linkbuild.listenerAndServerLibrary.LogManager;
import com.jsoft.linkbuild.listenerAndServerLibrary.*;

/**
 *
 * @author sbrandollo
 */
public class LogTest 
{
    public LogTest()
    {
        
        LogManager lm = LogManager.getInstance();
        lm.addLogger(3000, LogManager.REGISTRATION);
        //lm.makeLog(this.getClass(), "QWERTYUI567890", "Ciao sono io");
    }
    public static void main(String[] args)
    {
        new LogTest();
    }
}