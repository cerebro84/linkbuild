/*
 * JSoft-Link&Bulid
 * 
 */
package com.jsoft.linkbuild.utility;

/**
 *
 * @author TheOne
 */
public interface BanningRule 
{
    /*the return string must include <true/false>:<reason>. each reason must be
     *separated by ":". example- false:Ban For anagalabishtin maimatini ai ai ai: Ban For jejejeje */
    public String sentenceScan(String sentence);
    public String sentenceScan(String[] sentence);
    public String sentenceScan(boolean sentence);
    public String sentenceScan(int sentence);
    public String sentenceScan(byte sentence);
    public String sentenceScan(char sentence);
    public String sentenceScan(byte[] sentence);
    

}
