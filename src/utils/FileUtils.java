package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;


import objects.MP3Player;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;


//import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileUtils {
	
	private static Logger log = Logger.getLogger(FileUtils.class);
	
	// удалить текущий файл-фильтр и установить новый переданный
	public static void addFileFilter(JFileChooser jfc, FileFilter ff){
		jfc.removeChoosableFileFilter(jfc.getFileFilter());
		jfc.setFileFilter(ff);
		jfc.setSelectedFile(new File(""));// удалить последнее имя открываемого/созраняемого файла
	}

	public static String getFileNameWithoutExtension(String fileName) {
		File file = new File(fileName);
		int index = file.getName().lastIndexOf('.');
		if(index > 0 && index <= file.getName().length() -2){
			return file.getName().substring(0, index);
		}
	
		return "noname";
	}
	
	// получить расширение файла
	public static String getFileExtension(File f){
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		
		if(i > 0 && i < s.length() - 1){
			ext = s.substring(i+1).toLowerCase();
		}
		
		return ext;
	}

	public static void serialize(Object obj, String fileName) {
		try{
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			fos.close();
		} catch (IOException ex){
			log.info(ex.getMessage());
		}
	}

	public static Object deserialize(String fileName) {
		try{
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream oin = new ObjectInputStream(fis);
			Object ts = (Object) oin.readObject();
			
//			buff.mark((int)file.length());
//			oin.mark((int) );
			
			fis.close();
			return ts;
			
		} catch(ClassNotFoundException | IOException ex){
			log.info(ex.getMessage());
		}
		
		return null;
	}

}
