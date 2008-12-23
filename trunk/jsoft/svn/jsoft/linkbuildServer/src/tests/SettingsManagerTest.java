package tests;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.jsoft.linkbuild.listenerAndServerLibrary.*;

/**
 *
 * @author cerebro84
 */
public class SettingsManagerTest 
{
    public static void main (String [] args)
    {
        SettingsManager SM = SettingsManager.getInstance();
        System.out.println ("Applicazioni Installate:");
        printArray (SM.getInstalledApps(false));
        System.out.println ("Applicazioni approvate:");
        printArray (SM.getApprovedApps(false));
        System.out.println ("Applicazioni NON approvate:");
        printArray (SM.getNotApprovedApps(false));
        System.out.println ("Applicazioni Disponibili:");
        printArray (SM.getAvailableApps(false));
        System.out.println ("Applicazioni Nuove:");
        printArray (SM.getNewApps(false));
        System.out.println ("Applicazioni Installate (refresh):");
        printArray (SM.getInstalledApps(true));
//        SM.registerServerApp("prova");
  //      SM.setAppRunning("prova", true);
        System.out.println (SM.isAppRunning("prova"));
    //    SM.setAppRunning("prova", false);
        System.out.println (SM.isAppRunning("prova"));

    }
    static private void printArray (String[] tmp)
    {
        for (int i = 0; i < tmp.length; i++)
        {
            System.out.println (tmp[i]);
        }
    }
}
