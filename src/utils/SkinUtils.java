package utils;

//import gui.MP3PlayerGui;

import java.awt.Component;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import javax.swing.*;

import mp3player.MP3PlayerGUI;

public class SkinUtils {
	
	private static Logger log = Logger.getLogger(SkinUtils.class);

    public static void changeSkin(Component comp, LookAndFeel laf) {
        try {
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MP3PlayerGUI.class.getName()).log(Level.WARN, null, ex);
        }

        SwingUtilities.updateComponentTreeUI(comp);
        
    }
    
    public static void changeSkin(Component comp, String laf) {
        try {
            try {
                UIManager.setLookAndFeel(laf);
            } catch (ClassNotFoundException ex) {
            	log.info(ex.getMessage());
            } catch (InstantiationException ex) {
            	log.info(ex.getMessage());
            } catch (IllegalAccessException ex) {
            	log.info(ex.getMessage());
            }
        } catch (UnsupportedLookAndFeelException ex) {
            log.info("4");
        }

        SwingUtilities.updateComponentTreeUI(comp);
        
    }
}
