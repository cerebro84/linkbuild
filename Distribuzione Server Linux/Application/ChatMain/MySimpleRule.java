/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jsoft.linkbuild.utility.*;
import com.jsoft.linkbuild.listenerAndServerLibrary.*;

/**
 *
 * @author Pitagora
 */
public class MySimpleRule implements RegistrationRule
{

    public MySimpleRule()
    {
        
    }
    
    public boolean checkLength(String[] fields)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getFields() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFields(String[] fields)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean acceptRegistration(String address, String[] remote_fields)
    {
        if ((remote_fields[0].length() <= 8))
            return true;
        else
            return false;
    }

}
