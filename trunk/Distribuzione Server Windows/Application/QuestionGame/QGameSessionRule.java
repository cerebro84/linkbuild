import com.jsoft.linkbuild.listenerAndServerLibrary.*;
import com.jsoft.linkbuild.utility.*;
import java.util.Hashtable;


public class QGameSessionRule implements SessionRule
{

    private QuestionGame refQG;
    private Hashtable punti;
    
    public QGameSessionRule(QuestionGame ref)
    {
        this.refQG = ref;
        this.punti = new Hashtable<String, Integer>(); //IDUser,punti
    }
    
    public int getPasswordForApp()
    {
        return 4;
    }

    public Object[] buildFieldsForUser(String IDUser, String application) 
    {
        int a = this.refQG.getPunti(IDUser);
        Integer[] b = new Integer[1];
        b[0] = a;
        return b;
    }
    
    public void saveLastPoints(String IDUser)
    {
        int a = this.refQG.getPunti(IDUser);
        if(this.punti.containsKey(IDUser))
        {
            this.punti.remove(IDUser);
            this.punti.put(IDUser, new Integer(a));
        }
        else
        {
            this.punti.put(IDUser, new Integer(a));
        }
        
    }
    
    public int getLastPoints(String IDUser)
    {
        if(this.punti.get(IDUser) == null) return 0;
        return (Integer)(this.punti.get(IDUser));
    }
}
