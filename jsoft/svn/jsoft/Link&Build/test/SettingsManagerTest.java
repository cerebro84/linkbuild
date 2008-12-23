
import linkbuild.SettingsManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author giuseppe
 */
public class SettingsManagerTest 
{
    public static void main (String [] args)
    {
        SettingsManager SM = SettingsManager.getInstance();
        System.out.println(SM.getAvailableApps());
    }
}
