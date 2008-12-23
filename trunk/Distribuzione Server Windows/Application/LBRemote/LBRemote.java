
import com.jsoft.linkbuild.listenerAndServerLibrary.LogManager;
import com.jsoft.linkbuild.utility.LinkBuildApp;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 *
 * @author cerebro84
 */
public class LBRemote implements LinkBuildApp
{
    //Communication constants
    public final static byte LEFT = 1;
    public final static byte UP = 2;
    public final static byte RIGHT = 4;
    public final static byte DOWN = 8;
    public final static byte FAST = 16;
    public final static byte LEFT_CLICK = 32;
    public final static byte RIGHT_CLICK = 33;
    public final static byte SCROLL_DOWN = 34;
    public final static byte SCROLL_UP = 35;
    
    //Other constants
    public final static int PX = 10;
    public final static int FACTOR = 4; //for fast movements
    private int password;
    private Robot robot;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {}
    public LBRemote (int pw)
    {
        password = pw;
        try 
        {
            robot = new Robot();
        } 
        catch (AWTException ex) 
        {
            LogManager.getInstance().addLogger(password, LogManager.SESSION);
        }
    }

    public boolean newUserRegistered(String IDUser) {return true;}

    public boolean userDisconnected(String IDUser) {return true;}

    public boolean userAbnormalDisconnected(String IDUser) {return true;}

    public boolean userDisconnectedForPing(String IDUser) {return true;}

    public boolean userDisconnectedForSysBan(String IDUser, String message) {return true;}

    public boolean userDisconnectedForAppBan(String IDUser, String message) {return true;}

    public boolean userRegistrationFields(String IDUser, String[] fields) {return true;}

    public boolean onClosing() 
    {
        return true;
    }

    public boolean stringIncoming(String IDUser, String message) {return true;}
    
    public boolean stringArrayIncoming(String IDUser, String[] messages) {return true;}

    public boolean booleanIncoming(String IDUser, boolean message) {return true;}

    public boolean integerIncoming(String IDUser, int message) 
    {return true;}

    public boolean byteIncoming(String IDUser, byte message) 
    {
        switch (message)
        {
            case LEFT:
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-PX, MouseInfo.getPointerInfo().getLocation().y);
                break;
            case RIGHT:
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+PX, MouseInfo.getPointerInfo().getLocation().y);
                break;
            case UP:
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y-PX);
                break;
            case DOWN:
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y+PX);
                break;
            case (LEFT+UP):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-PX, MouseInfo.getPointerInfo().getLocation().y-PX);
                break;
            case (LEFT+DOWN):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-PX, MouseInfo.getPointerInfo().getLocation().y+PX);
                break;
            case (RIGHT+UP):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+PX, MouseInfo.getPointerInfo().getLocation().y-PX);
                break;
            case (RIGHT+DOWN):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+PX, MouseInfo.getPointerInfo().getLocation().y+PX);
                break;
                
            case (LEFT+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y);
                break;
            case (RIGHT+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y);
                break;
            case (UP+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y-(PX*FACTOR));
                break;
            case (DOWN+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y+(PX*FACTOR));
                break;
            case (LEFT+UP+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y-(PX*FACTOR));
                break;
            case (LEFT+DOWN+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x-(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y+(PX*FACTOR));
                break;
            case (RIGHT+UP+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y-(PX*FACTOR));
                break;
            case (RIGHT+DOWN+FAST):
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x+(PX*FACTOR), MouseInfo.getPointerInfo().getLocation().y+(PX*FACTOR)); 
                break;
            case LEFT_CLICK:
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case RIGHT_CLICK:
                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            case SCROLL_DOWN:
                robot.mouseWheel(3);
                break;
            case SCROLL_UP:
                robot.mouseWheel(-3);
                break;
        }
        return true;    
    }

    public boolean charIncoming(String IDUser, char message) {return true;}

    public boolean byteArrayIncoming(String IDUser, byte[] messages) {return true;}

    public boolean logIncoming(String log) {return true;}

    public boolean userSessionFields(String IDUser, Object[] fields) {return true;}

}
