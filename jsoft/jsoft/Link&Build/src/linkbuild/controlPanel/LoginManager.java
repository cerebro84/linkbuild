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
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import linkbuild.FileManager;

/*login manager*/
public class LoginManager extends JFrame implements ActionListener
{
    private JTextField username;
    private JPasswordField password;
    private JLabel lUsername;
    private JLabel lPassword;
    private JButton ok;
    private JButton annulla;	
    private JPanel centerP;
    private JPanel southP;
    private Registration reg;
    private Intro intro;
    
    /*DEFINE*/
    private final static int ROOT = 0;
    private final static int ADMIN = 1;
    /*constructor*/
    public LoginManager ()
    {	
        super("LoginManager");
        setSize(250,125);
        setResizable(false);
        setLocation(400,300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        centerP = new JPanel();
        southP  = new JPanel();
        username = new JTextField("username", 13);
        password = new JPasswordField("password", 13);
        password.setEchoChar('•');
        username.selectAll();
        password.selectAll();
        lUsername = new JLabel("Username:");
        lPassword = new JLabel("Password:");
        ok = new JButton("Ok");
        annulla = new JButton("Annulla");
        //actionlistener
        ok.addActionListener(this);
        annulla.addActionListener(this);
        //components
        centerP.add(lUsername);
        centerP.add(username);
        centerP.add(lPassword);
        centerP.add(password);
        southP.add(ok);
        southP.add(annulla);

        setLayout(new BorderLayout());
        add(centerP,BorderLayout.CENTER);
        add(southP,BorderLayout.SOUTH);
        setVisible(true);
    }
    public void actionPerformed (ActionEvent e)
    {
        Object obj = e.getSource();
        if (obj == ok)
        {
            if(userLogin(username.getText(),new String(password.getPassword())))
                dispose();
            else
                System.out.println("Sorry, authorization failure..");
        }
        else
            if (obj == annulla) //control annulla button of this frame
                System.exit(0);
            else 
                if (obj == reg.ok)//control the ok button of the Registration Frame
                {
                    if(reg.saveAccount())
                    {
                        reg.dispose();
                        setVisible(true);
                    }
                }
                else
                    if (obj == reg.annulla)//control the annulla button of the Registration Frame
                    {
                       System.exit(0);
                    }
    }
    /*authorization control*/
    private boolean userLogin(String user, String pass) 
    {
        File rootFile = new File("Data"+File.separator+"root.dat");
        if(!rootFile.exists())
        {
            initialize();
            addSuperUser();
        }   
        if(userControl(user,pass,"root.dat"))//control if logged user is root
        {
           intro = new Intro(ROOT,user);//run the control panel application as root
           return true;
        }
        if(userControl(user,pass,"admin.dat"))
        {
            intro = new Intro(ADMIN, user);//run the application as admin user
            return true;
        }
        return false;
    }
    /*user verify*/
    private boolean userControl (String user, String pass, String dir)
    {
        User curUser = new User(user,pass);
        if (FileManager.fileIsEmpty("Data"+File.separator+dir) == 0)//control if the data file is empty
        {
            Object users[] = FileManager.objectList("Data"+File.separator+dir);
            for (int i = 0; i < users.length; i++)
            {
                if (((User)users[i]).equals(curUser))//control the currant user with the userlist
                {
                    System.out.println(user+": login success...");
                    return true;
                }
            }
        }
        return false;
    }
    /*create root account*/
    private void addSuperUser()
    {
            reg = new Registration("username","password","root.dat");
            reg.ok.addActionListener(this);
            reg.annulla.addActionListener(this);
            setVisible(false);
    }
    /*initialize all configuration*/
    private void initialize()
    {
        try
        {
            FileManager.createDirectory("Application");
            FileManager.createDirectory("Data");
            FileManager.createFile("Data"+File.separator+"root.dat");
            FileManager.createFile("Data"+File.separator+"admin.dat");
            FileManager.createFile("Data"+File.separator+"banning.txt");
            FileManager.createFile("Data"+File.separator+"AppList.txt");
            FileManager.createFile("Data"+File.separator+"AppWaitingList.txt");
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.exit(255);
        }
    }
}
/*GUI to make accounts*/
class Registration extends JFrame
{
    private JTextField username;
    private JPasswordField password;
    private JPasswordField passwordV;
    private JLabel lUsername;
    private JLabel lPassword;
    private JLabel lPasswordV;
    public JButton ok;
    public JButton annulla;

    private JPanel p;
    private JPanel centerP;
    private JPanel southP;
    private JPanel westP;

    private final String uPath;
    /*constructor*/
    public Registration (String user, String pass, String path)
    {
        super("User Registration");
        username   = new JTextField(user, 20);
        username.selectAll();
        password   = new JPasswordField(pass,20);
        password.selectAll();
        passwordV = new JPasswordField(20);
        passwordV.selectAll();
        password.setEchoChar('•');
        uPath = path;

        lUsername   = new JLabel("Username",JLabel.RIGHT);
        lPassword   = new JLabel("Password",JLabel.RIGHT);
        lPasswordV = new JLabel("Password verify",JLabel.RIGHT);

        ok       = new JButton("Ok");
        annulla = new JButton("Annulla");

        centerP = new JPanel();
        southP  = new JPanel();
        p         = new JPanel();

        setSize(200,150);
        setLocation(400,300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);

        centerP.setLayout(new GridLayout(3,0));
        centerP.add(lUsername);
        centerP.add(username);
        centerP.add(lPassword);
        centerP.add(password);
        centerP.add(lPasswordV);
        centerP.add(passwordV);
        southP.add(ok);
        southP.add(annulla);

        p.setLayout(new BorderLayout());	
        p.add(centerP,BorderLayout.CENTER);
        p.add(southP,BorderLayout.SOUTH);

        add(p);
        setVisible(true);
    }
    /*registration of new user*/
    public boolean saveAccount ()
    {
        String pass = new String(password.getPassword());	
        String passv = new String(passwordV.getPassword());
        if (!pass.equals(passv) || username.getText().equals("") || pass.equals(""))
        {
            return false;		
        }
        if (uPath.equals("root.dat"))
        {
            FileManager.writeObject("Data"+File.separator+uPath,new RootUser(username.getText(),pass));
            return true;
        }
        else
        {
            AdminUser newAdmin = new AdminUser(username.getText(), pass);
            if (controlUser("Data"+File.separator+"root.dat",newAdmin) && controlUser("Data"+File.separator+"admin.dat",newAdmin))
            {	
                FileManager.writeObject("Data"+File.separator+uPath,newAdmin);
                return true;     
            }
        }
        return false;
    }
    /*user exist control*/
    private boolean controlUser (String file_path, User newUser)
    {
        if(FileManager.fileIsEmpty(file_path) == 1)
            return true;
        Object []users = FileManager.objectList(file_path);
        for (int i = 0; i < users.length ; i++)
        {
            if (newUser.compareTo((User)users[i]))
            {
                System.out.println("user exists, plz choose an other username...");
                return false;
            }
        }
        return true;
    }
}