/*
 * JSoft-Link&Bulid
 * 
 */
package com.jsoft.linkbuild.utility;

import java.io.*;
import java.util.TreeSet;
/**
 * 
 * @author TheOne
 */
public class BanConf implements BanningRule, Serializable
{
    private boolean floodFlag;
    private boolean wordFlag;
    private boolean option1;
    private boolean option2;
    private FloodingRule fr;
    private WordControlRule wcr;
    /**
     * this object contain the standard banning rules
     */
    public BanConf ()
    {
        floodFlag = false;
        wordFlag = false;
        option1 = false;
        option2 = false;
        fr = new FloodingRule();
        wcr = new WordControlRule();
    }
    /**
     * 
     * @param flag turn on/off flooding control rule
     */
    public void setFloodingControl (boolean flag)
    {
        floodFlag = flag;
    }
    /**
     * 
     * @param flag turn on/off words control rule
     */
    public void setWordControl (boolean flag)
    {
        wordFlag = flag;
    }
    public void setOpt1 (boolean flag)
    {
        option1 = flag;
    }
    public void setOpt2 (boolean flag)
    {
        option2 = flag;
    }
    /**
     * 
     * @return true if flooding rule is turned on
     */
    public boolean getFloodingControl ()
    {
        return floodFlag; 
    }
    /**
     * 
     * @return true if wordcontrol is turned on
     */
    public boolean getWordControl ()
    {
        return wordFlag;
    }
    public boolean getOpt1 ()
    {
        return option1;
    }
    public boolean getOpt2 ()
    {
        return option2;
    }
    /**
     * 
     * @return the information of this object
     */
    public String toString()
    {
        String str = "Flood: "+floodFlag+"\nWordControl: "+wordFlag+"\nOption1: "+option1+"\nOption2: "+option2;
        return str;
    }
    /**
     * 
     * @param sentence the array string to scan
     * @return true if pass all test
     */
    public String sentenceScan(String[] sentence)
    {
        String result = "";
        boolean flag = true;
        for(int i = 0; i < sentence.length; i++)
        {
            if(floodFlag && fr.sentenceScan(sentence[i]))
            {
                flag = false;
                result +=":Ban For Flooding";
            }
            if(wordFlag && wcr.sentenceScan(sentence[i]))
            {
                flag = false;
                result +=":Ban For Not Allowed Words";
            }
            if(option1)
                flag = flag;
            if(option2)
                flag = flag;
            
        }
        return flag+result;
    }
    public String sentenceScan(String sentence){return "";}
    public String sentenceScan(boolean sentence){return "";}
    public String sentenceScan(int sentence){return "";}
    public String sentenceScan(byte sentence){return "";}
    public String sentenceScan(char sentence){return "";}
    public String sentenceScan(byte[] sentence){return "";}
}

class FloodingRule implements Serializable
{
    static public boolean sentenceScan (String sentence)
    {
        if (sentence == null || sentence.equals("") || sentence.length() < 9)
            return false;
        char charArray[] = sentence.toCharArray();
        int i=0;
        while (i < charArray.length-1 && charArray.length - i > 9)
        {
            if(charArray[i] == charArray[i+1] && charArray.length - i > 9)
            {
                int j = i+2;
                while(j<charArray.length && charArray[i] == charArray[j])
                {
                    j++;
                }
                if(j-i > 9)
                    return true;
                i = j;
            }
            i++;
        }
        return false;
    }
}
class WordControlRule implements Serializable
{
    private static TreeWords treeWords = new TreeWords();
    static public boolean sentenceScan (String sentence)
    {
        if(sentence == null || sentence.equals(""))
            return false;
        if (treeWords.isEmpty())
            return false;
        String words[] = sentence.split(" ");
        int i=0;
       
        while (i < words.length)
        {
            if(treeWords.contains(words[i].toLowerCase()))
                return true;
            i++;
        }
        return false;
    }
}
class TreeWords extends TreeSet
{
    private static  String dicPath ="Data"+File.separator+"words.txt";
    public TreeWords()
    {
        super();
        setTree();
    }
    private void setTree()
    {
        if(this.isEmpty())
        {
            if(FileManager.fileIsEmpty(dicPath) == 1)
                return;
            String lista[] = FileManager.readFile(dicPath);
            for(int i = 0; i < lista.length; i++)
            {
                this.add(lista[i]);
            }
        }
    }
}