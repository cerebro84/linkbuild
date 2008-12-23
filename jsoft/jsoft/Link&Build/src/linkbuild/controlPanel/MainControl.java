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
import linkbuild.FileManager;
import java.io.*;
/*main control frame*/
class MainControl extends javax.swing.JFrame implements ActionListener
{
	JTabbedPane control;
	
	JPanel tabPanel;
	JPanel centerP;
	JPanel southP;
	JPanel northP;
	
	//UserPanel uPanel;
        UserPanel uPanel;
	ApplicationPanel aPanel;
        BanningPanel bPanel;
        private JLabel rIcon;
        private JLabel lIcon;
        
	private JButton exit;
        /*constructor*/
	public MainControl (int mode, String username)
	{
            super("Control Panel");
            setSize(500,300);
            setLocation(400,300);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            tabPanel = new JPanel();
            centerP = new JPanel();
            centerP.setLayout(new GridLayout());
            southP = new JPanel();
            southP.setLayout(new FlowLayout(FlowLayout.RIGHT));
            northP = new JPanel();
            northP.setLayout(new BorderLayout());
            if(mode == 0)//gui for root
            {
                uPanel = new UserPanel();
                aPanel = new ApplicationPanel();
                bPanel = new BanningPanel();
            }
            else//gui for admins
            {
                uPanel = new UserPanelAdmin(username,mode);
                aPanel = new ApplicationPanelAdmin();
            }
            exit = new JButton("Exit");
            exit.addActionListener(this);
            southP.add(exit);
            rIcon = new JLabel(new ImageIcon("Images"+File.separator+"jsoft.png"));
            lIcon = new JLabel(new ImageIcon("Images"+File.separator+"linknbuild_mini.png"));
            northP.add(lIcon,BorderLayout.WEST);
            northP.add(rIcon,BorderLayout.EAST);
            control = new JTabbedPane();
            control.addTab("User Manager",uPanel);
            control.addTab("Application Manager", aPanel);
            control.addTab("Banning Manager", bPanel);
            centerP.add(control);
            setLayout(new BorderLayout());
            add(centerP, BorderLayout.CENTER);
            add(southP,BorderLayout.SOUTH);
            add(northP, BorderLayout.NORTH);
            setVisible(true);
	}
	public void actionPerformed (ActionEvent e)
	{
            Object  obj = e.getSource();
            if ((JButton)obj == exit)//abort the control panel
            {
                System.exit(0);
            }
        }
}
/*user panel for root*/
class UserPanel extends JPanel implements ActionListener
{
    /*user components*/
    JButton addUser;
    JButton userList; 
    JButton delUser;
    Registration reg;
    JPanel buttonPanel;
    ShowInfo showPanel;
    /*constructor*/
    public UserPanel ()
    {
        setLayout(new BorderLayout());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,0));
        showPanel = new ShowInfo();
        addUser = new JButton("Add New User");
        addUser.addActionListener(this);
        buttonPanel.add(addUser);              
        delUser = new JButton("Delete User");
        delUser.addActionListener(this);
        buttonPanel.add(delUser);
        userList = new JButton("User List");
        userList.addActionListener(this);
        buttonPanel.add(userList);
        add(buttonPanel, BorderLayout.WEST);
        add(showPanel);
    }
    public void actionPerformed (ActionEvent e)
    {
        Object  obj = e.getSource();
	if ((JButton)obj == addUser) //add new user (only root can add a new user)
        {
            newUser();
            setVisible(false);
        }
        else
            if((JButton)obj == delUser)
            {
                deleteUser();
            }
            else
                if((JButton)obj == userList)
                {
                    userList();
                }
        //reg frame component check 
        else
            if ((JButton)obj == reg.ok)
            {
                if(reg.saveAccount())
                {
                    System.out.println("L'account è stato aggiunto con successo...");
                    reg.dispose();
                }
                else
                    System.out.println("Registrazione del nuovo utente è fallito...");
            }
            else
                if((JButton)obj == reg.annulla)
                {
                    reg.dispose();
                }
    }
    /*Registration of a new account*/
    private void newUser ()
    {
        reg = new Registration("username", "password","admin.dat");
        reg.ok.addActionListener(this);
        reg.annulla.addActionListener(this);
    }
    /*
     *method used to delete user by username...
     * 
     */
    private void deleteUser ()
    {
        String uName = JOptionPane.showInputDialog ("Enter the username.");
        if (FileManager.fileIsEmpty("Data"+File.separator+"admin.dat") == 0)
        {
            Object  obj[] = FileManager.objectList("Data"+File.separator+"admin.dat");
            deleteUser(obj,uName);
        }
        else 
            System.out.println("File is empty...");
    }
    /*
     * Control if the username is present in the list..
     * 
     */
    private void deleteUser (Object obj[],String user)
    {
        boolean flag = false;
        int posUser = 0;
        int i = 0;
        while (!flag && i < obj.length)
        {
            if (((User)obj[i]).getUsername().equals(user))
            {
                flag = !flag;
                posUser = i;
            }
            i++;
        }
        if (!flag)
        {
            System.out.println("User not found...");
            return;
        }
        deleteUser(obj,posUser);

    }
    /*
     * if the user is present in the list this method delete that user...
     * 
     */
    private void deleteUser (Object obj[],int pos)
    {
        Object objToWrite[] = new Object[obj.length-1];
        int j = 0;
        for(int i = 0; i < obj.length; i++)
        {
            if (i != pos)
            {
                objToWrite[j] = obj[i];
                j++;
            }
        }
        FileManager.writeObject("Data"+File.separator+"admin.dat", objToWrite);
        System.out.println("User deleted...");    
    }
    /*
     * show the admin users list
     *
     */
    private void userList ()
    {
        if (FileManager.fileIsEmpty("Data"+File.separator+"admin.dat") == 0)
        {
            if(showPanel.flag)
                showPanel.delList();
            else
            {
                Object  obj[] = FileManager.objectList("Data"+File.separator+"admin.dat");
                String[] data = new String[obj.length];
                for(int i = 0; i < obj.length; i++)
                    data[i] = ((User)(obj[i])).getUsername();
                showPanel.setList(data);
            }
        }
        else
            System.out.println("user file is empty...");
    }
}
/*user panel for admin user with limided tasks*/
class UserPanelAdmin extends UserPanel
{
    /*constructor*/
    public UserPanelAdmin (String uName, int mode_)
    {
        super();
        buttonPanel.remove(addUser);
        buttonPanel.remove(delUser);
    }
}
/*application manager panel for root*/
class ApplicationPanel extends JPanel implements ActionListener
{
    /*application components*/
    JButton addApp;
    JButton appList; 
    JButton delApp;
    JPanel buttonPanel;
    ShowInfo showPanel;
    protected String appListDir;
    /*constructor*/
    public ApplicationPanel ()
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,0));
        showPanel = new ShowInfo();
        setLayout(new BorderLayout());
        addApp = new JButton("Add New Application");
        addApp.addActionListener(this);
        buttonPanel.add(addApp);              
        delApp = new JButton("Delete Application");
        delApp.addActionListener(this);
        buttonPanel.add(delApp);
        appList = new JButton("Applications List");
        appList.addActionListener(this);
        buttonPanel.add(appList);
        add(buttonPanel, BorderLayout.WEST);
        add(showPanel);
        appListDir = "AppList.txt";
                
    }
    public void actionPerformed (ActionEvent e)
    {
        Object  obj = e.getSource();
	if ((JButton)obj == addApp)
        {
            if (copyApplication(FileManager.selectFile(".class")))
            {
                System.out.println("L'applicazione è stata registrata con successo...");
            }
            else
                System.out.println("La registrazione è fallita...");
        }
        else
            if ((JButton)obj == appList)
            {
                listAvailableApp();
            }
            else
                if ((JButton)obj == delApp)
                {
                    deleteApplication();
                }
    }
    /*
     *copy the application in to the application directory
     *
     */
    private boolean copyApplication (File f)
    {
        if (f == null)
            return false;
        String fName = f.getName();
        File appDir = new File("Application"+File.separator+fName.substring(0,fName.length()-6));
        if  (appDir.exists())
        {
            System.out.println("Application è presente");
            return false;
        }
        FileManager.copyDirectory(f.getParent(), "Application"+File.separator+fName.substring(0,fName.length()-6));
        FileManager.writeFile("Data"+File.separator+appListDir,fName.substring(0,fName.length()-6));
        return true;
    }
    /*
     *show to user available applications list
     *
     */
    private void listAvailableApp ()
    {
        if(showPanel.flag)
            showPanel.delList();
        else
        {
            if (FileManager.fileIsEmpty("Data"+File.separator+"AppList.txt") == 0)
            {
                String appList[] = FileManager.readFile("Data"+File.separator+"AppList.txt");
                JList dataList = new JList(appList);
                showPanel.setList(appList);
            }
        }
    }
    private void deleteApplication()
    {
        if (FileManager.fileIsEmpty("Data"+File.separator+"admin.dat") == 0)
        {
            String appName = JOptionPane.showInputDialog ("Enter application name to delete.");
            File appFolder = new File("Application"+File.separator+appName);
            if(appFolder.exists() && appFolder.isDirectory())
            {
                FileManager.deleteDirectory("Application"+File.separator+appName);
                String fileList[] = FileManager.readFile("Data"+File.separator+"AppList.txt");
                deleteApplication(fileList,appName);
            }
            else
                System.out.println("Application not found in the list...");
        }
        else 
            System.out.println("File is empty...");
    }
    private void deleteApplication(String appList[],String appName)
    {
        String newAppList[] = new String[appList.length-1];
        int j = 0;
        for (int i = 0; i < appList.length; i++)
        {
            if(!appList[i].equals(appName))
            {
                newAppList[j] = appList[i];
                j++;
            }
        }
        for(int i = 0;  i < newAppList.length ; i++)
            System.out.println(newAppList[i]);
        FileManager.writeFile("Data"+File.separator+"AppList.txt", newAppList);
    }
}
/*application manager panel for admin user with limited tasks*/
class ApplicationPanelAdmin extends ApplicationPanel
{
    /*constructor*/
    public ApplicationPanelAdmin()
    {
        super();
        appListDir = "AppWaitingList.txt";
        buttonPanel.remove(delApp);
    }
}
class BanningPanel extends JPanel implements ActionListener
{
    JPanel checkBPanel;
    JPanel buttonPanel;
    /*banning panel components*/
    JButton createBRules;
    JCheckBox flooding;
    JCheckBox wordControl;
    JCheckBox option1;
    JCheckBox option2;
    JCheckBox option3;
    JCheckBox option4;
    /*constructor*/
    public BanningPanel ()
    {
        checkBPanel = new JPanel();
        buttonPanel = new JPanel();
        checkBPanel.setLayout(new GridLayout(4,0));
        createBRules = new JButton("Create Banning Rules");
        createBRules.addActionListener(this);
        flooding = new JCheckBox("Flooding Control");
        wordControl = new JCheckBox("Words Control");
        option1 = new JCheckBox("option1");
        option2 = new JCheckBox("option2");
        option3 = new JCheckBox("option3");
        option4 = new JCheckBox("option4");
        checkBPanel.add(flooding);
        checkBPanel.add(wordControl);
        checkBPanel.add(option1);
        checkBPanel.add(option2);
        checkBPanel.add(option3);
        checkBPanel.add(option4);
        buttonPanel.add(createBRules);
        setLayout(new BorderLayout());
        add(checkBPanel,BorderLayout.WEST);              
        add(buttonPanel,BorderLayout.EAST);              
    }
    public void actionPerformed (ActionEvent e)
    {
        Object  obj = e.getSource();
	if ((JButton)obj == createBRules)
            createBanningRules();
    }
    private void createBanningRules()
    {
        
    }
}
/*class to show infomation*/
class ShowInfo extends JPanel
{
    JScrollPane jsp;
    boolean flag;
    public ShowInfo()
    {
        setLayout(new GridLayout());
        jsp = new JScrollPane();
        add(jsp);
        flag = false;
        
    }
    public void setList(String str[])
    {
        jsp.setViewportView(new JList(str));  
        add(jsp);
        flag = !flag;
    }
    public void delList()
    {
        jsp.setViewportView(null);  
        add(jsp);
        flag = !flag;
    }
}