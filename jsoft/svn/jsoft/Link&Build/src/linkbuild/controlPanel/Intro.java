/*
 * JSoft-Link&Bulid
 * 
 */

package linkbuild.controlPanel;

/**
 *
 * @author TheOne
 */
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
/*
 * Introdution Window
 * 
 */
public class Intro extends JWindow implements ActionListener
{
    private MainControl mc;
    private String username;
    private int mode;
    int x;
    int y;
    int max = 0;
    IntroPanel p;
    Timer t;
    
    public Intro(int mode_, String user)
    {
        mode = mode_;
        username = user;
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        x = d.width/2-(231/2);
        y = d.height/2-(147/2);
        t = new Timer(1000, this);
        setLocation(x,y);
        p = new IntroPanel(tk);
        add(p);
        setSize(231,147);
        this.setAlwaysOnTop(true);
        setVisible(true);
        t.start();
    }
    public void actionPerformed(ActionEvent e)
    {
        if(max > 0)
        {
            t.stop();
            mc = new MainControl(mode,username);
            dispose();
        }
        else
            max++;
        
    }
}
class IntroPanel extends JPanel
{
    Toolkit tk;
    Image im;
    public IntroPanel(Toolkit t)
    {
        tk = t;
        try
        {
            im = t.getImage("Images"+File.separator+"linknbuild.png");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        repaint();
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(im, 0, 0, this);
    }
}
