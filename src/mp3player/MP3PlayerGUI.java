package mp3player;

//import java.awt.BorderLayout;
import java.awt.EventQueue;


//import javax.sound.sampled.AudioFileFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
//import javax.swing.border.BevelBorder;
//import javax.swing.border.LineBorder;

//import java.awt.Color;

import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
//import javax.swing.SwingUtilities;
import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//import javax.swing.border.SoftBevelBorder;
//import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;
//import javax.swing.plaf.SliderUI;
//import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.Component;

import javax.swing.ImageIcon;

import utils.FileUtils;
import utils.MP3PlayerFileFilter;
import utils.SkinUtils;

import java.awt.Toolkit;
//import java.awt.Dialog.ModalExclusionType;
//import java.awt.Window.Type;
import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import javax.swing.JTree;


//import javazoom.jl.player.Player;
import javazoom.jlgui.basicplayer.BasicController;
//import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import objects.MP3;
import objects.MP3Player;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

public class MP3PlayerGUI extends JFrame implements BasicPlayerListener{
	
    /**
	 * 
	 */
	
	private static Logger log = Logger.getLogger(MP3PlayerGUI.class);
	
	private static final long serialVersionUID = 1L;
	private static final String MP3_FILE_EXTENSION = "mp3";
    private static final String MP3_FILE_DESCRIPTION = "Файлы mp3";
    private static final String PLAYLIST_FILE_EXTENSION = "styopik";
    private static final String PLAYLIST_FILE_DESCRIPTION = "Файлы плейлиста";
    private static final String EMPTY_STRING = "";
//    private static final String INPUT_SONG_NAME = "введите имя песни";
    
    private static Long duration; // длительность песни в секундах
    private static int bytesLen; // размер песни в байтах
    private static long secondsAmount; // сколько секунд прошло с начала проигрывания

    // передвигается ли ползунок песни от перетаскивания (или от проигрывания) - используется во время перемотки
    private boolean movingFromJump = false;
    private boolean moveAutomatic = false;// во время проигрывании песни ползунок передвигается, moveAutomatic = true
    private double posValue = 0.0; // позиция для прокрутки
            
    MP3Player player = new MP3Player(this);
    
    private DefaultListModel mp3ListModel = new DefaultListModel();
    private FileFilter mp3FileFilter = new MP3PlayerFileFilter(MP3_FILE_EXTENSION, MP3_FILE_DESCRIPTION);
    private FileFilter playlistFileFilter = new MP3PlayerFileFilter(PLAYLIST_FILE_EXTENSION, PLAYLIST_FILE_DESCRIPTION);

	private JPanel contentPane;
	private JTextField txtSearch;
	
	private JList <?> lstPlayList = new JList<>();
	private JSlider slideProgress = new JSlider();
	private JSlider slideVolume = new JSlider();
	
	private int currentVolumeValue;
	
	JLabel labelSongName = new JLabel("...");
	
//	private BasicPlayer player = new BasicPlayer();
	
	private static JFileChooser fileChooser = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
				
//		log.info("123432142345");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MP3PlayerGUI frame = new MP3PlayerGUI();
					frame.setVisible(true);
					
					SkinUtils.changeSkin(frame, UIManager.getSystemLookAndFeelClassName());
					fileChooser.updateUI();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	/**
	 * Create the frame.
	 */
	public MP3PlayerGUI() {
		setTitle(" mp3 Player v1.0");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MP3PlayerGUI.class.getResource("/com/sun/javafx/scene/control/skin/caspian/images/vk-light.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 306, 642);
		
//		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Выбрать файл");
        fileChooser.setMultiSelectionEnabled(true);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
//		JLabel labelSongName = new JLabel("...");
		
//		JSlider slideVolume = new JSlider();
		slideVolume.setSnapToTicks(true);
		slideVolume.setMinorTickSpacing(5);
		slideVolume.setMaximum(200);
		
		JMenu menuFile = new JMenu("\u0424\u0430\u0439\u043B");
		menuBar.add(menuFile);
		
//		JList lstPlayList = new JList();
		lstPlayList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				
				// если нажали левую кнопку мыши два раза
				if(evt.getModifiers() == InputEvent.BUTTON1_MASK && evt.getClickCount() == 2){
					int[] indexPlayList = lstPlayList.getSelectedIndices();
					if(indexPlayList.length > 0){
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);
						player.play(mp3.getPath());
						player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
					}
				}
			}
		});
		
		JMenuItem menuOpenPlaylist = new JMenuItem("\u041E\u0442\u043A\u0440\u044B\u0442\u044C \u043F\u043B\u0435\u0439\u043B\u0438\u0441\u0442");
		menuOpenPlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileUtils.addFileFilter(fileChooser, playlistFileFilter);
				int result = fileChooser.showOpenDialog(contentPane);// result хранит результат: выбран файл или нет
				
				if(result == JFileChooser.APPROVE_OPTION){// если нажата клавиша YES или OK
					File selectedFile = fileChooser.getSelectedFile();
					DefaultListModel mp3ListModel2 = (DefaultListModel) FileUtils.deserialize(selectedFile.getPath());
					mp3ListModel = mp3ListModel2;
					lstPlayList.setModel(mp3ListModel2);
				}
			}
		});
		
		
		menuOpenPlaylist.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/open-icon.png")));
		menuFile.add(menuOpenPlaylist);
		
		JMenuItem menuSavePlaylist = new JMenuItem("\u0421\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u044C \u043F\u043B\u0435\u0439\u043B\u0438\u0441\u0442");
		menuSavePlaylist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				FileUtils.addFileFilter(fileChooser, playlistFileFilter);
				int result = fileChooser.showSaveDialog(contentPane);
				
				if(result == JFileChooser.APPROVE_OPTION){// если нажать OK или YES
					File selectedFile = fileChooser.getSelectedFile();
					
					if(selectedFile.exists()){// если такой файл уже существует
						
						int resultOverride = JOptionPane.showConfirmDialog(contentPane, "Файл Существует", "Перезаписать?", JOptionPane.YES_NO_CANCEL_OPTION);
						switch(resultOverride){
						case JOptionPane.NO_OPTION:
							actionPerformed(evt);// повторно открыть окно сохранения файла
							return;
						case JOptionPane.CANCEL_OPTION:
							fileChooser.cancelSelection();
							return;
						}
						
						//если нажал YES (согласился на перезапись), то плейлист перезаписывается
						fileChooser.approveSelection();
					}
					
					String fileExtension = FileUtils.getFileExtension(selectedFile);
					
					// имя файла (нужно ли добавлять расширение к имени файла при сохранении)
					String fileNameForSave = (fileExtension != null && fileExtension.equals(PLAYLIST_FILE_EXTENSION)) ? selectedFile.getPath() : selectedFile.getPath() + "." + PLAYLIST_FILE_EXTENSION;
				
					FileUtils.serialize(mp3ListModel, fileNameForSave);
				}				
			}
		});
		menuSavePlaylist.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/save_16.png")));
		menuFile.add(menuSavePlaylist);
		
		JSeparator menuSeparator = new JSeparator();
		menuSeparator.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		menuFile.add(menuSeparator);
		
		JMenuItem menuExit = new JMenuItem("\u0412\u044B\u0445\u043E\u0434");
		menuExit.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/exit.png")));
		menuFile.add(menuExit);
		
		JMenu menuPrefs = new JMenu("\u0421\u0435\u0440\u0432\u0438\u0441");
		menuBar.add(menuPrefs);
		
		JMenu menuChangeSkin = new JMenu("\u0412\u043D\u0435\u0448\u043D\u0438\u0439 \u0432\u0438\u0434");
		menuChangeSkin.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/gear_16.png")));
		menuPrefs.add(menuChangeSkin);
		
		JMenuItem menuSkin2 = new JMenuItem("\u0421\u043A\u0438\u043D 1");
		menuSkin2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				SkinUtils.changeSkin(contentPane, new NimbusLookAndFeel());
				fileChooser.updateUI();
			}
		});
		menuChangeSkin.add(menuSkin2);
		
		JMenuItem menuSkin1 = new JMenuItem("\u0421\u043A\u0438\u043D 2");
		menuSkin1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SkinUtils.changeSkin(contentPane, UIManager.getSystemLookAndFeelClassName());
				fileChooser.updateUI();
			}
		});
		menuChangeSkin.add(menuSkin1);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelSearch.setBounds(15, 15, 261, 45);
		contentPane.add(panelSearch);
		panelSearch.setLayout(null);
		
		txtSearch = new JTextField();
		txtSearch.setName("");
		txtSearch.setToolTipText("");
		txtSearch.setBounds(10, 14, 132, 20);
		panelSearch.add(txtSearch);
		txtSearch.setColumns(15);
		
//		JList lstPlayList = new JList();
		
		JButton btnSearch = new JButton("\u041D\u0430\u0439\u0442\u0438");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String searchStr = txtSearch.getText();
				
				// если в поиске ничего не ввели - выйти из метода и не производить поиск
				if(searchStr == null || searchStr.trim().equals(EMPTY_STRING)){
					return;
				}
				
				// все индексы объектов, найденных по поиску, будут храниться в коллекции
				ArrayList<Integer> mp3FindedIndexes = new ArrayList<Integer>();
				
				// проходим по коллекции и ищем соответствия имен песен со строкой поиска
				for(int i = 0; i < mp3ListModel.size(); i++){
					MP3 mp3 = (MP3) mp3ListModel.getElementAt(i);
					
					// поиск вхождения строки в название песни без учета регистра букв
					if(mp3.getName().toUpperCase().contains(searchStr.toUpperCase())){
						mp3FindedIndexes.add(i); // найденный индекс добавляем в коллекцию			
					}
				}
				
				// коллекцию индексов сохраняем в массив
				int[] selectIndexes = new int[mp3FindedIndexes.size()];
				
				if(selectIndexes.length == 0){// если не найдено ни одной песни, удовлетворяющей условию поиска
					JOptionPane.showMessageDialog(contentPane, "Поиск по строке \'" + searchStr + "\' не дал результатов");
					txtSearch.requestFocus();
					txtSearch.selectAll();
					return;
				}
				
				// преобразовать коллекцию в массив, т.к. метод для выделениястрок в JList работает только с массивом
				for(int i = 0; i < selectIndexes.length; i++){
					selectIndexes[i] = mp3FindedIndexes.get(i).intValue();
				}
				
				// выделить в плейлисте найденные песни по массиву индексов, найденных ранее
				lstPlayList.setSelectedIndices(selectIndexes);
				
			}
		});
		btnSearch.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/search_16.png")));
		btnSearch.setBounds(152, 13, 99, 23);
		panelSearch.add(btnSearch);
		
		JPanel panelMain = new JPanel();
		panelMain.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelMain.setBounds(15, 71, 261, 511);
		contentPane.add(panelMain);
		panelMain.setLayout(null);
		
		JButton btnPrevSong = new JButton("");
		btnPrevSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int prevIndex = lstPlayList.getSelectedIndex() - 1;
					
					if(prevIndex == - 1 && (lstPlayList.getModel().getSize() != 0)){
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(lstPlayList.getModel().getSize() - 1);
						player.play(mp3.getPath());
						player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
						lstPlayList.setSelectedIndex(lstPlayList.getModel().getSize() - 1);
					}else if(prevIndex != - 1 && (lstPlayList.getModel().getSize() != 0)){
						lstPlayList.setSelectedIndex(prevIndex);
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(prevIndex);
						player.play(mp3.getPath());
						player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
					}
			}
		});
		btnPrevSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/prev-icon.png")));
		btnPrevSong.setBounds(20, 446, 36, 36);
		panelMain.add(btnPrevSong);
		
		JToggleButton tglbtnVolume = new JToggleButton("");
		tglbtnVolume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (tglbtnVolume.isSelected()){
		            currentVolumeValue = slideVolume.getValue();
		            slideVolume.setValue(0);
		        }else{
		            slideVolume.setValue(currentVolumeValue);
		        }
			}
		});
		tglbtnVolume.setSelectedIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/mute.png")));
		tglbtnVolume.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/speaker.png")));
		tglbtnVolume.setBounds(10, 412, 23, 23);
		panelMain.add(tglbtnVolume);
		
//		JSlider slideVolume = new JSlider();
		slideVolume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
				
				if(slideVolume.getValue() == 0){
					tglbtnVolume.setSelected(true);
				}else {
					tglbtnVolume.setSelected(false);
				}
			}
		});
		slideVolume.setBounds(41, 412, 210, 23);
		panelMain.add(slideVolume);
		
		JButton btnPlaySong = new JButton("");
		btnPlaySong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] indexPlayList = lstPlayList.getSelectedIndices();// получаем индексы выбранных песен
				
				if(indexPlayList.length > 0){// если выбрали хотя бы одну песню
					MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);// находим первую выбранную песню (т.к. все нельзя проиграть)
					player.play(mp3.getPath());
					player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
					//System.out.println(mp3.getPath());
				}
				
//				playFile();
			}
		});
		btnPlaySong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/Play.png")));
		btnPlaySong.setBounds(66, 446, 36, 36);
		panelMain.add(btnPlaySong);
		
		JButton btnPauseSong = new JButton("");
		btnPauseSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.pause();
			}
		});
		btnPauseSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/Pause-icon.png")));
		btnPauseSong.setBounds(110, 446, 36, 36);
		panelMain.add(btnPauseSong);
		
		JButton btnStopSong = new JButton("");
		btnStopSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.stop();
			}
		});
		btnStopSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/stop-red-icon.png")));
		btnStopSong.setBounds(156, 446, 36, 36);
		panelMain.add(btnStopSong);
		
		JButton btnNextSong = new JButton("");
		btnNextSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nextIndex = lstPlayList.getSelectedIndex() + 1;
					if(nextIndex <= lstPlayList.getModel().getSize() - 1 && (lstPlayList.getModel().getSize() != 0)){
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(lstPlayList.getSelectedIndex() + 1);// находим первую выбранную песню (т.к. все нельзя проиграть)
						player.play(mp3.getPath());
						player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
						lstPlayList.setSelectedIndex(lstPlayList.getSelectedIndex() + 1);
					}else if (nextIndex > lstPlayList.getModel().getSize() - 1 && (lstPlayList.getModel().getSize() != 0)){
						lstPlayList.setSelectedIndex(0);
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(0);// находим первую выбранную песню (т.к. все нельзя проиграть)
						player.play(mp3.getPath());
						player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
					}
			}
		});
		btnNextSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/next-icon.png")));
		btnNextSong.setBounds(202, 446, 36, 36);
		panelMain.add(btnNextSong);
		
//		JToggleButton tglbtnVolume = new JToggleButton("");
//		tglbtnVolume.setSelectedIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/mute.png")));
//		tglbtnVolume.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/speaker.png")));
//		tglbtnVolume.setBounds(10, 356, 23, 23);
//		panelMain.add(tglbtnVolume);
		
//		JSlider slideVolume = new JSlider();
//		slideVolume.setBounds(41, 356, 210, 23);
//		panelMain.add(slideVolume);
		
		JSeparator jSeparator2 = new JSeparator();
		jSeparator2.setOrientation(SwingConstants.VERTICAL);
		jSeparator2.setBounds(131, 11, 15, 30);
		panelMain.add(jSeparator2);
		
		JButton btnAddSong = new JButton("");
		btnAddSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileUtils.addFileFilter(fileChooser, mp3FileFilter);// устанавливаем FileFilter
				int result = fileChooser.showOpenDialog(contentPane);// result хранит результат: выбран файл или нет
				
				if(result == JFileChooser.APPROVE_OPTION){// если нажата клавиша OK или YES

					File[] selectedFiles = fileChooser.getSelectedFiles();
					// перебираем все выделенные файлы для добавления в плейлист
					for(File file : selectedFiles){
						MP3 mp3 = new MP3(file.getName(), file.getPath());
						mp3ListModel.addElement(mp3);
					}
				}
			}
		});
		btnAddSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/plus_16.png")));
		btnAddSong.setBounds(10, 11, 35, 30);
		panelMain.add(btnAddSong);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 52, 241, 293);
		panelMain.add(scrollPane_1);
		
//		JList lstPlayList = new JList();
		lstPlayList.setModel(mp3ListModel);
		scrollPane_1.setViewportView(lstPlayList);
		
		JButton btnDeleteSong = new JButton("");
		btnDeleteSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] indexPlayList = lstPlayList.getSelectedIndices();// получаем индексы выбранных песен
				
				if(indexPlayList.length > 0){// если выбрали хотя бы одну песню
					ArrayList<MP3> mp3ListForRemove = new ArrayList<MP3>();// сначала сохраняем все mp3 для удаления в отдельную коллекцию
					for(int i = 0; i < indexPlayList.length; i++){// удаление всех выбранных песен из плейлиста
						// метод getElementAt возвращает Object, поэтому надо привести типы
						MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[i]);
						mp3ListForRemove.add(mp3);
					}
					
					// удаляем mp3 в плейлисте
					for(MP3 mp3 : mp3ListForRemove){
						mp3ListModel.removeElement(mp3);
					}
				}
			}
		});
		btnDeleteSong.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/remove_icon.png")));
		btnDeleteSong.setBounds(55, 11, 35, 30);
		panelMain.add(btnDeleteSong);
		
		JButton btnSelectPrev = new JButton("");
		btnSelectPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lstPlayList.getSelectedIndex() > 0){
					lstPlayList.setSelectedIndex(lstPlayList.getSelectedIndex() -1);
				}else if(lstPlayList.getSelectedIndex() == 0){
					lstPlayList.setSelectedIndex(lstPlayList.getModel().getSize() - 1);
				}
			}
		});
		btnSelectPrev.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/arrow-up-icon.png")));
		btnSelectPrev.setBounds(216, 11, 35, 30);
		panelMain.add(btnSelectPrev);
		
		JButton btnSelectNext = new JButton("");
		btnSelectNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int nextIndex = lstPlayList.getSelectedIndex() + 1;
				if(nextIndex <= lstPlayList.getModel().getSize() - 1){// если не вышли за пределы плейлиста
					lstPlayList.setSelectedIndex(nextIndex);	
				}else if(nextIndex == lstPlayList.getModel().getSize()){
					lstPlayList.setSelectedIndex(0);
				}
			}
		});
		btnSelectNext.setIcon(new ImageIcon(MP3PlayerGUI.class.getResource("/images/arrow-down-icon.png")));
		btnSelectNext.setBounds(171, 11, 35, 30);
		panelMain.add(btnSelectNext);
		
//		JLabel labelSongName = new JLabel("...");
		labelSongName.setFont(new Font("Tahoma", Font.BOLD, 12));
		labelSongName.setBounds(10, 356, 241, 23);
		panelMain.add(labelSongName);
		slideProgress.setMaximum(1000);
		
		slideProgress.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
		        if (slideProgress.getValueIsAdjusting() == false) {
		            if (moveAutomatic == true) {
		                moveAutomatic = false;
		                posValue = slideProgress.getValue() * 1.0 / 1000;
		                processSeek(posValue);
		            }
		        } else {
		            moveAutomatic = true;
		            movingFromJump = true;
		        }
			}
		});
		
//		JSlider slideProgress = new JSlider();
		slideProgress.setValue(0);
		slideProgress.setBounds(10, 382, 241, 23);
		panelMain.add(slideProgress);
		
//		JScrollPane scrollPane_1 = new JScrollPane();
//		scrollPane_1.setBounds(10, 52, 241, 293);
//		panelMain.add(scrollPane_1);
		
//		JList lstPlayList = new JList();
//		lstPlayList.setModel(mp3ListModel);
//		scrollPane_1.setViewportView(lstPlayList);
	}



	@Override
	public void opened(Object o, Map map) {
		
        //        еще один вариант определения mp3 тегов
//      AudioFileFormat aff = null;
//      try {
//          aff = AudioSystem.getAudioFileFormat(new File(o.toString()));
//      } catch (UnsupportedAudioFileException ex) {
//          Logger.getLogger(MP3PlayerGUI.class.getName()).log(Level.SEVERE, null, ex);
//      } catch (IOException ex) {
//          Logger.getLogger(MP3PlayerGUI.class.getName()).log(Level.SEVERE, null, ex);
//      }

		// определить длину песни и размер файла
		duration = (long) Math.round((((Long) map.get("duration")).longValue()) / 1000000);
		bytesLen = (int) Math.round(((Integer) map.get("mp3.length.bytes")).intValue());
		
//		System.out.println(map);
		
		// если есть mp3 тег для имени - берем его, если нет - вытаскиваем название из имени файла
		String songName = map.get("title") != null ? map.get("title").toString() : FileUtils.getFileNameWithoutExtension(new File(o.toString()).getName());
		
		// если длинное название - укоротить его
		if(songName.length() > 30){
			songName = songName.substring(0, 30) + "...";
		}
		
		labelSongName.setText(songName);
	}

    private boolean selectNextSong() {
        int nextIndex = lstPlayList.getSelectedIndex() + 1;
        if (nextIndex <= lstPlayList.getModel().getSize() - 1) {// если не вышли за пределы плейлиста
            lstPlayList.setSelectedIndex(nextIndex);
            return true;
        }
        return false;
    }
    
    private void playFile() {
        int[] indexPlayList = lstPlayList.getSelectedIndices();// получаем выбранные индексы(порядковый номер) песен
        if (indexPlayList.length > 0) {// если выбрали хотя бы одну песню
            MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);// находим первую выбранную песню (т.к. несколько песен нельзя проиграть одновременно
            player.play(mp3.getPath());
            player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
        }

    }
    
    private void processSeek(double bytes) {
        try {
            long skipBytes = (long) Math.round(((Integer) bytesLen).intValue() * bytes);
            player.jump(skipBytes);;
        } catch (Exception e) {
            e.printStackTrace();
            movingFromJump = false;
        }

    }

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map proporties) {
		
		float progress = -1.0f;
		
		if((bytesread > 0) && ((duration >0))){
			progress = (bytesread * 1.0f) / (bytesLen * 1.0f);
		}
		
		// сколько секунд прошло
		secondsAmount = (long) (duration * progress);
		
		if(duration != 0){
			if(movingFromJump == false){
				slideProgress.setValue(((int) Math.round(secondsAmount * 1000 / duration)));
//				slideProgress.setValue(((int) Math.round(progress * 100)));
//				System.out.println(slideProgress.getValue() + "   " + secondsAmount + "   " + duration + "   " + progress + "   " + bytesread + "   " + bytesLen);
			}
		}
	}


	@Override
	public void setController(BasicController bc) {

	}

	
	@Override
	public void stateUpdated(BasicPlayerEvent bpe) {

		int state = bpe.getCode();
		
		if(state == BasicPlayerEvent.PLAYING){
			movingFromJump = false;
		}else if(state == BasicPlayerEvent.SEEKING){
			movingFromJump = true;
		}else if(state == BasicPlayerEvent.EOM){
			if(selectNextSong()){
				playFile();
			}
		}
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
