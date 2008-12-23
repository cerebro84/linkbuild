/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jsoft.linkbuild.utility;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author cerebro84
 */
public class Conversions 
{
/**
         * 
         * @param h is the hashtable to flatten; its keys must be String.
         * @return an array containing hashtable's keys
         */
        public static String [] KeysToArrayString (Hashtable h)
        {
            String [] array = new String[h.size()];
            int i = 0;
            for (Enumeration<String> tmp = h.keys(); tmp.hasMoreElements();i++)
            {
            array[i]= tmp.nextElement() ;
            }
            return array;
        }
        
        public static Object [] KeysToArrayObject (Hashtable h)
        {
            Object [] array = new Object[h.size()];
            int i = 0;
            for (Enumeration<Object> tmp = h.keys(); tmp.hasMoreElements();i++)
            {
            array[i]= tmp.nextElement() ;
            }
            return array;
        }
        
        /**
         * 
         * @param h
         * @return an array containing all values in the Hashtable
         */
        public static String [] StringValuesToArray (Hashtable h)
        {
            return (String[])h.values().toArray(new String[0]);
        }
        
        public static Object[] ObjectValuesToArray (Hashtable h)
        {
            return h.values().toArray(new Object[0]);
        }
}
