package com.jsoft.linkbuild.utility;

import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * FileManager helps the User Application and the Sys Admin to manage all Filesystem 
 * access by unique class
 * The FileManager class is a static class and all methods can be called everywhere.
 * 
 * <pre>
 *    FileManager.createFile("/home/user/helloWorld.txt");      (Unix Type)
 *    FileManager.createFile("C:\hellloWorld.txt");             (Win/DOS Type)
 *    FileManager.createDirectory("/home/user/dirname");        (Unix Type)
 *    FileManager.createFile("C:\dirname");                     (Win/DOS Type)
 * 
 * </pre>
 * @author      Christian Rizza
 */
public class FileManager 
{
    /**
     * Create a directory in specific path to FileSystem.
     * @param dir_path      directory path to create.
     * @return              true, if directroy was created, else otherwise.
     */
    public static boolean createDirectory (String dir_path)
    {
        try
        {
            File directory = new File(dir_path);
            if(!directory.exists())
            {
                directory.mkdir();
                return true;
            }
            return false;
        }
        catch (Exception e)
	{
            System.out.println(e);
            return false;
        }
    }
    /**
     * Delete a single file specificated by file_path.
     * @param file_path     file path to delete
     * @return              true, if file was deleted, else otherwise.
     */
    public static boolean deleteFile (String file_path)
    {
        File f = new File(file_path);
        return f.delete();
    }
    /**
     * Delete a specific directory and its sub-directories
     * @param dir   directory path to delete
     * @return      true, if directroy was deleted, else otherwise.
     */
    public static boolean deleteDirectory (String dir)
    {
        File d = new File(dir);
        if ((!d.isDirectory()) || (!d.exists()))
            return false;
        
        // Inizio svotando la cartella
        if (!d.delete())
        {
            String[] lista_file = d.list();
            for (int i=0;i<lista_file.length;i++)
            {
                File f = new File(d.getAbsolutePath()+File.separator+lista_file[i]);
                if (f.isDirectory())
                {
                    deleteDirectory(f.getAbsolutePath());
                }
                else
                {
                    deleteFile(f.getAbsolutePath());
                }
            }
            return d.delete();
        }
        return false;
    }
    /**
     * Create an empty file specificicated by file_path.
     * 
     * @param file_path     file path to delecreate file
     * @return              true, if file was created, else otherwise.
     */
    public static boolean createFile (String file_path)
    {

        try
        {
            File new_file = new File(file_path);
            if(!new_file.exists())
            {
                new_file.createNewFile() ;
            }
            return new_file.exists();
        }
        catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
    }
    public static Object readObject(String file_path)
    {
        Object[] temp = objectList(file_path);
        return temp[0];
    }
    /**
     * Return number of objects contained file.
     * 
     * @param file_path     file path to applicate this methods
     * @return              number of object counted
     */
    public static int countObject (String file_path)
    {
        int count = 0;
        ObjectInputStream  file = null;
        try
	{
            file = new ObjectInputStream(new FileInputStream (file_path));
            try
            {
                while (true)
                {
                    file.readObject();
                    count++;
                }
            }
            catch(EOFException e)
            {
                file.close();
            }
            return count;
        }
	catch(Exception e)
	{
            System.out.println(e);
            return -1;
	}
    }
    /**
     * Return a list of Objects cointained file and return an array
     * 
     * @param file_path     file path to applicate this methods
     * @return      Array of Objects
     */
    public static Object[] objectList (String file_path)
    {
        try
        {
            ObjectInput in_stream = new ObjectInputStream(new FileInputStream (file_path));
            Object[] array = new Object[countObject(file_path)];
        
            for (int i=0;i<array.length;i++)
            {
                array[i]=in_stream.readObject();
            }
            return array;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }
    /**
     * Append a new object in at the end of file
     * 
     * @param file_path     file path to applicate this methods
     * @param obj           Object to append
     * @return              true, if Object was appened, else otherwise.
     */
    public static boolean appendObject (String file_path, Object obj)
    {
        ObjectInputStream  in_stream;
        ObjectOutputStream out_stream;
        int n =countObject(file_path);
        Object[] objects = new Object[n];
        try
        {
            in_stream = new ObjectInputStream(new FileInputStream (file_path));
            for (int i=0;i<n;i++)
            {    
                objects[i]=in_stream.readObject();
            }
            out_stream = new ObjectOutputStream(new FileOutputStream (file_path));
            for (int i=0;i<n;i++)
            {    
                out_stream.writeObject(objects[i]);
            }
            out_stream.writeObject(obj);
            in_stream.close();
            out_stream.close();
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
    }
    /**
     * Write object in File
     * 
     * @param file_path     file path to applicate this methods
     * @param obj           Object to append
     * @return      true, if Object was written, else otherwise.
     */
    public static boolean writeObject (String file_path, Object obj)
    {
        File file=new File(file_path);
        
            try
            {
                ObjectOutputStream  stream = new ObjectOutputStream(new FileOutputStream (file_path));
                stream.writeObject(obj);
                stream.close();
                return true;
            }
            catch(Exception e)
            {
                System.out.println(e);
                return false;
            }
    }
    /**
     * write object list
     * 
     * @param file_path     file path to applicate this methods
     * @param obj           Object array to append in file
     * @return      true, if Array Objects was written, else otherwise.
     */
    public static boolean writeObject (String file_path, Object []obj)
    {
        try
        {
            ObjectOutputStream  stream = new ObjectOutputStream(new FileOutputStream (file_path));
            for (int i = 0; i < obj.length; i++)
                stream.writeObject(obj[i]);
            stream.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return false;
        }
    }
    /**
     * Copy file by string path to string path
     * 
     * @param file_in     source file
     * @param file_out     destination file
     * @return      true, if copy was completed without errors, else otherwise.
     */
    public static boolean copyFile (String file_in, String file_out)
    {
        try
	{             
            File f_in   = new File(file_in);
            File f_out  = new File(file_out);
            
            InputStream in = new FileInputStream(f_in);
            OutputStream out = new FileOutputStream(f_out);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return false;
        }
    }
    /**
     * Copy directory and sub-diretory to destinanion path
     * 
     * @param dir_in            source directory
     * @param dir_out           destination directory
     * @return      true, if copy was completed without errors, else otherwise.
     */
    public static boolean copyDirectory (String dir_in, String dir_out)
    {
        File f_in   = new File(dir_in);
        File f_out  = new File(dir_out);
     
        if ((!f_out.isDirectory() && f_out.exists()) || !f_in.exists())
            return false;
        
        if (!f_out.isDirectory())
            f_out.mkdir();

        String[] lista_file=f_in.list();
        for (int i=0;i<lista_file.length;i++)   //Start the copy
        {
            File f = new File(f_in.getAbsolutePath()+File.separator+lista_file[i]);
            if (f.isDirectory())
            {
                copyDirectory(f.getAbsolutePath(), f_out.getAbsolutePath()+File.separator+f.getName());
            }
            else
            {
                copyFile(f_in.getAbsolutePath()+File.separator+f.getName(), f_out.getAbsolutePath()+File.separator+f.getName());
            }
        }
        return true;
    }
    /**
     * Move file_in to file_out
     * 
     * @param file_in       source file
     * @param file_out      destination file
     * @return      true, if move was completed without errors, else otherwise.
     */
    public static boolean moveFile (String file_in, String file_out)
    {
        if (copyFile(file_in, file_out))
            return deleteFile(file_in);
        return false;
    }
    /**
     * Move directory and sub-diretory to destinanion path
     * 
     * @param dir_in    source directory
     * @param dir_out   destination directory
     * @return      true, if move was completed without errors, else otherwise.
     */
    public static boolean moveDirectory (String dir_in, String dir_out)
    {
        if (copyDirectory(dir_in, dir_out))
            return deleteDirectory(dir_in);
        return false;
    }
    /**
     * Rename file from old_name to new_name
     * 
     * @param old_name
     * @param new_name 
     * @return      true, if file was renamed, else otherwise.
     */
    public static boolean renameFile (String old_name, String new_name)
    {
        return moveFile(old_name, new_name);
    }
    /**
     * Rename directory and sub-directory from old_name to new_name
     * @param old_name 
     * @param new_name 
     * @return      true, if directory was renamed, else otherwise.
     */
    public static boolean renameDirectory (String old_name, String new_name)
    {
        return moveDirectory(old_name, new_name);
    }
    /**
     * Rename file or directory from old_name to new_name
     * @param old_name 
     * @param new_name 
     * @return      true, if file was renamed, else otherwise.
     */
    public static boolean rename (String old_name, String new_name)
    {
        File f=new File(old_name);
        if (f.exists() &&  f.isDirectory())
        {
            return moveDirectory(old_name, new_name);
        }
        else if (f.exists() && !f.isDirectory())
        {
            return moveFile(old_name, new_name);
        }
        return false;
    }
    /**
     * Write file from specific context
     * 
     * @param file_path         file you will write
     * @param context           context you will write in file
     * @return      true, if file was written, else otherwise.
     */
    public static boolean writeFile (String file_path, String context)
    {
        try
	    {
		    RandomAccessFile fileIn = new RandomAccessFile(file_path,"rwd");
		    fileIn.seek(fileIn.length());
		    fileIn.writeBytes(context+"\n");
		    
		    fileIn.close();
		    return true;
	    }
	    catch(Exception e)
	    {
		    System.out.println(e);
		    return false;
	    }
    }
    /**
     * Write file from specific context array
     * @param file_path         file you will write
     * @param context           context you will write in file
     * @return      true, if file was written, else otherwise.
     */
    public static boolean writeFile (String file_path, String[] context)
    {
        try
	    {
                FileWriter fw = new FileWriter(file_path);
                for(int i = 0; i < context.length; i++)
                    fw.write(context[i]+"\n");
                fw.flush();
                fw.close();
                return true;
	    }
	    catch(Exception e)
	    {
                System.out.println(e);
                return false;
	    }
    }
    /**
     * Read file from specific file_path
     * @param file_path     file you will read
     * @return      String array
     */
    public static String[] readFile (String file_path)
    {
		long n = countFileLine(file_path) ;
		if(n < 0)
			return null;
		String s[] = new String[(int)n];
		int i = 0;	
		try
		{
			RandomAccessFile fileIn = new RandomAccessFile(file_path,"r");
			String str;
			while((str = fileIn.readLine()) != null)
			{
				s[i] = str;
				i++;
			}
			fileIn.close();
			return s;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
    }
    /**
     * Return the number of line in file.
     * @param file_path file you will count
     * @return      long number.
     */
    public static long countFileLine (String file_path)
    {
		long n = 0;
		try
		{
			RandomAccessFile fileIn = new RandomAccessFile(file_path,"r");
			while(fileIn.readLine() != null)
			{
				n++;
			}
			fileIn.close();
			return n;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return -1;
		}
    }
    /**
     * Search a specific string line in specific file
     * @param file_path         file where you search 
     * @param line              search line
     * @return      true, if string was matched, else otherwise.
     */
    public static boolean searchLine (String file_path, String line)
    {
		try
		{
			RandomAccessFile fileIn = new RandomAccessFile(file_path,"r");
			String s;
			while((s = fileIn.readLine()) != null)
			{
				if(s.equals(line))
					return true;
			}
			fileIn.close();
			return false;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return false;
		}
    }
    /**
     * <pre>
     * Check if file is empty.
     * Return:
     *   1 - File is empty
     *   0 - File is non-empty
     *  -1 - File not exist
     * </pre>
     * @param file_path 
     * @return      int, state of file description
     */
    public static int fileIsEmpty(String file_path)
    {
        try
        {
            FileInputStream stream_in = new FileInputStream(file_path);
            while (stream_in.read()==-1)
                return 1;  //E' vuoto
            return 0;   //E' Pieno
        }
        catch (Exception e)
        {
            System.out.println(e);
            return -1; //non esite.
        }
    }
    /**
     * Append string in file rappresented by file_path
     * @param file_path 
     * @param line 
     * @return      true, if string was appened, else otherwise.
     */
    public static boolean appendLine(String file_path, String line)
    {
        try
	{
            return writeFile(file_path, line);
	}
	catch(Exception e)
	{
            return false;
	}
    }
    /**
     * <pre>
     * List files and directories inside a directory; flags can be:
     * 0 = only list files
     * 1 = only list directories
     * other values = list everything
     * </pre>
     * 
     * @param _dir 
     * @param flags 
     * @return      File Array, List of file contained directory
     */
    public static File [] listDirectory (String _dir, int flags)
    {
        File dir   = new File(_dir);
        File [] lista;
        switch (flags)
        {
            case 0:
                lista=dir.listFiles(new FilterForFile());
            case 1:
                lista = dir.listFiles(new FilterForDirectory());
            default:
                lista = dir.listFiles();
        }
        return lista;
    }
    /**
     * List both files and directories inside a directory
     * 
     * @param _dir 
     * @return      File Array, List of file contained directory
     */
    public static File [] listDirectory (String _dir)
    {
        return listDirectory(_dir,2);
    }
    /**
     * File Selecter by graphical chooser
     * 
     * @param ext 
     * @return      File selected
     */
    public static File selectFile(String ext)
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FilterForChooser(ext));
        
        int risp = fc.showOpenDialog((JFrame)null);
        
        
        if (risp==JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        else
            return null;
            
    }
}


/**
 * FilterForChoose helps FileManager to select file and directory by a graphical
 * chooser.
 * Thi Filter describe and set the chooser for select java application but its 
 * can be extends to other file type
 * 
 * <pre>
 *    JFileChooser fc = new JFileChooser();
 *    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
 *    fc.setFileFilter(new FilterForChooser(ext));
 * </pre>
 * 
 * @author      Christian Rizza
 */
class FilterForChooser extends javax.swing.filechooser.FileFilter
{
    private String ext;
    public FilterForChooser(String ext)
    {
        this.ext = ext;
    }
    public boolean accept(File f)
    {
        String str= f.getName().toLowerCase();
        return str.endsWith(ext) || f.isDirectory();
    }
    public String getDescription()
    {
        return "Java Application" + ext;
    }
}


/**
 * 
 * 
 * @author giuseppe
 * 
 * 
 * Gets directories out of files
 */
class FilterForDirectory implements FileFilter
{
/**
 * Gets directories out of files
 * @param pathname
 * @return
 */
        
    public boolean accept(File pathname) 
    {
        return(pathname.isDirectory());
    } 
}

/**
 * 
 * @author giuseppe
 * 
 * Gets files out of directories
 */
class FilterForFile implements FileFilter       
{
    public boolean accept(File pathname) 
    {
        return (!pathname.isDirectory());
    } 
}
