
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author cerebro84
 */
class ReceiverThread implements Runnable
{
    StreamConnection conn = null;
    ClientLibrary form = null;

    public ReceiverThread(StreamConnection c, ClientLibrary f) 
    {
        conn = c;
        form = f;
    }

    public void run() 
    {
        DataInputStream din = null;
        try {
            din = new DataInputStream(conn.openInputStream());
            while (true) {
                String cmd = "";
                char c;
                while (((c = din.readChar()) > 0) && (c != '\n')) {
                    cmd = cmd + c;
                }
                form.do_alert(cmd, 4000); //TEMPORANEO: PASSERA' IL MESSAGGIO
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                din.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}