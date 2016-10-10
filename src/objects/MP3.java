package objects;

import java.io.Serializable;

import utils.FileUtils;

public class MP3 implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String path;
	
	public MP3(String name, String path){
		this.name = name;
		this.path = path;
	}

	@Override
	// для корректного отображения объекта mp3 при добавлении в плейлист
	public String toString(){
		return FileUtils.getFileNameWithoutExtension(name);
	}
	
	public String getName(){
		return name;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
}
