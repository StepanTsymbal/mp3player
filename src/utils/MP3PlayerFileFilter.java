package utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MP3PlayerFileFilter extends FileFilter {

	private String fileExtension;
	private String fileDescription;
	
	public MP3PlayerFileFilter(String fileExtension, String fileDescription) {
		this.fileDescription = fileDescription;
		this.fileExtension = fileExtension;
	}
		
	@Override
	public boolean accept(File file) {
		// ��������� ������ �����, � ����� ����� � ����������� mp3
		return file.isDirectory() || file.getAbsolutePath().endsWith(fileExtension);
	}

	@Override
	public String getDescription() {
		// �������� ��� ������� mp3 ��� ������ � ���������� ����
		return fileDescription + " (*" + fileExtension + ")";
	}

}
