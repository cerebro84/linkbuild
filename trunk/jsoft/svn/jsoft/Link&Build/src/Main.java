/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import linkbuild.*;
import linkbuild.bluetooth.Discovery;
import linkbuild.controlPanel.*;
/**
 *
 * @author sbrandollo
 */
public class Main 
{

    public static void main(String[] args)
    {
        try
        {
            Discovery ds=new Discovery();
            ds.getDiscovered().toString();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
