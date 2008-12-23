import com.jsoft.linkbuild.listenerAndServerLibrary.*;
import com.jsoft.linkbuild.utility.*;

public class MySimpleRule implements RegistrationRule
{

    public MySimpleRule()
    {
        
    }
    
    public boolean checkLength(String[] fields)
    {
        return false;
    }

    public String[] getFields() 
    {
        String[] a = new String[10];
        return a;
    }

    public void setFields(String[] fields)
    {

    }

    public boolean acceptRegistration(String address, String[] remote_fields)
    {
        if ((remote_fields[0].length() <= 8))
            return true;
        else
            return false;
    }

}
