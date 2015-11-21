package com.echeloneditor.utils;

import java.io.File;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.os.OsConstants;

public class FileUtil {

	public static String getFileNameExt(String filePath) {
		return getFileNameExt(new File(filePath));
	}

	public static String getFileNameExtNoDot(String filePath) {
		return getFileNameExtNoDot(filePath);
	}

	public static String getFileNameExt(File file) {
		String fileName = file.getName();
		if (file.isFile()) {
			int dotPos = fileName.lastIndexOf(".");
			return dotPos > 0 && dotPos < fileName.length() - 1 ? fileName.substring(dotPos, fileName.length()) : fileName;
		}
		return fileName;
	}

	public static String getFileNameExtNoDot(File file) {
		String fileNameExt = getFileNameExt(file);
		
		return fileNameExt.equalsIgnoreCase(file.getName())?fileNameExt:fileNameExt.substring(1, fileNameExt.length());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file=new File(OsConstants.DEFAULT_USER_DIR);
		File[] fileList=file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(FileUtil.getFileNameExt(fileList[i]));
			System.out.println(FileUtil.getFileNameExtNoDot(fileList[i]));
		}
		
	}

}
