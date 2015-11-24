package com.echeloneditor.os;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.echeloneditor.utils.Config;

public class OsConstants {
	public static String OS = System.getProperty("os.name");
	public static String DEFAULT_FILE_SEPARATOR = System.getProperty("file.separator");
	public static String DEFAULT_FILE_ENCODE = Config.getValue("CONFIG", "defaultCharset");
	public static int DEFAULT_BUFFER_SIZE = Integer.parseInt(Config.getValue("CONFIG", "ioBuffer")) << 20;// M
	public static String DEFAULT_USER_DIR = System.getProperty("user.dir");
	public static int BIG_FILE_READ_UNIT_SIZE = Integer.parseInt(Config.getValue("CONFIG", "bigFileReadUnitSize"));
	public static long BIG_FILE_SIZE = Integer.parseInt(Config.getValue("CONFIG", "bigFileSize"));
	public static FileSystemView fsv = FileSystemView.getFileSystemView();

	public static boolean isLinux() {
		return OS.indexOf("linux") >= 0;
	}

	public static boolean isMacOS() {
		return OS.indexOf("Mac") >= 0|| OS.indexOf("mac") >= 0 ;
	}

	public static boolean isMacOSX()
	{
		return OS.indexOf("Mac") >= 0 && OS.indexOf("OS") > 0 && OS.indexOf("X") > 0;
	}

	public static boolean isWindows()
	{
		return OS.indexOf("windows") >= 0||OS.indexOf("Windows")>= 0;
	}

	public static void main(String[] args) {
		System.out.println(OsConstants.fsv.getHomeDirectory().getPath());
		System.out.println(OsConstants.fsv.getSystemTypeDescription(new File(".")));
		System.out.println(OsConstants.fsv.getSystemDisplayName(new File(".")));
		System.out.println(System.getProperties());
		System.out.println(OS);
	}
}
