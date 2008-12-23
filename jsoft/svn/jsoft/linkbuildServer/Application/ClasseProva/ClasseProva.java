/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jsoft.linkbuild.utility.*;
import javax.swing.*;

/**
 *
 * @author Inuyasha
 */
public class ClasseProva extends JFrame implements LinkBuildApp
{
    public ClasseProva()
    {
       setTitle("Prova");
       setBounds(200,170,470,300);
       setVisible(true);
    }
    
    public boolean newUserRegistered(String IDUser)
    {
        JOptionPane.showMessageDialog(null,"Inserito nuovo utente: "+IDUser);
        return true;
    }
    
    public boolean userDisconnected(String IDUser)
    {
        return true;
    }
}
