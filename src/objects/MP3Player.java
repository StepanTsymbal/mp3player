package objects;

import java.io.File;
//import java.util.logging.Level;
//import java.util.logging.Logger;


import org.apache.log4j.Logger;

//import utils.SkinUtils;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

// класс для проигрывания mp3 файла
public class MP3Player {
	
	private static Logger log = Logger.getLogger(MP3Player.class);
	
	private BasicPlayer player = new BasicPlayer();
	
	private String currentFileName; // текущая песня
	private double currentVolumeValue; // текущий уровень звука
	
	public MP3Player(BasicPlayerListener listener){
		player.addBasicPlayerListener(listener);
	}
	
	public void play(String fileName){
		
		try{
			// если включают ту же песню, которая была на паузе
			if(currentFileName != null && currentFileName.equals(fileName) && player.getStatus() == BasicPlayer.PAUSED){
				player.resume();
				return;
			}
			
			currentFileName = fileName;
			player.open(new File(fileName));
			player.play();
//			log.info("123432142345");
			player.setGain(currentVolumeValue); // устанавливает уровень звука
						
		}catch (BasicPlayerException ex){
			log.info(ex.getMessage());
		}
	}
	
	public void stop(){
		
		try{
			player.stop();
		}catch(BasicPlayerException ex){
			log.info(ex.getMessage());
		}
	}
	
	public void pause(){
		
		try{
			player.pause();
		}catch(BasicPlayerException ex){
			log.info(ex.getMessage());
		}
	}
	
	public void setVolume(int currentValue, int maximumValue){
		
		try{
			this.currentVolumeValue = currentValue;
			
			if(currentValue == 0){
				player.setGain(0);
			}else{
				player.setGain(calcVolume(currentValue, maximumValue));
			}
		}catch(BasicPlayerException ex){
			log.info(ex.getMessage());
			}
		}
	
	// подсчитать значение звука исходя из значений компонента регулировки звука (JSlider)
	private double calcVolume(int currentValue, int maximumValue){
		
		currentVolumeValue = (double) currentValue / (double) maximumValue;
		return currentVolumeValue;
	}

	public void jump(long bytes) {

		try{
			player.seek(bytes);
			player.setGain(currentVolumeValue); // устанавливаем уровень звука
		}catch(BasicPlayerException ex){
			log.info(ex.getMessage());
		}
		
	}

}
