package com.echeloneditor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.echeloneditor.os.OsConstants;

public class FileUtil {
	public static long lines = 0;

	public static long countLines(File dir) throws Exception {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				countLines(file);
			}
		} else {
			lines += countLine(dir);
		}
		return lines;
	}

	public static long countLine(File file) throws Exception {
		long num = 0;
		FileReader fs = new FileReader(file);
		BufferedReader bis = new BufferedReader(fs);
		while (bis.readLine() != null) {
			num += 1;
		}
		fs.close();
		bis.close();
		return num;
	}

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

		return fileNameExt.equalsIgnoreCase(file.getName()) ? fileNameExt : fileNameExt.substring(1, fileNameExt.length());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File(OsConstants.DEFAULT_USER_DIR);
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			System.out.println(FileUtil.getFileNameExt(fileList[i]));
			System.out.println(FileUtil.getFileNameExtNoDot(fileList[i]));
		}

	}

}
