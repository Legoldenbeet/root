package com.echeloneditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	/**
	 * 压缩文件file成zip文件zipFile
	 * 
	 * @param file
	 *            要压缩的文件
	 * @param zipFile
	 *            压缩文件存放地方
	 * @throws Exception
	 */
	public static void zip(File file, File zipFile) throws Exception {
		ZipOutputStream output = null;
		try {
			output = new ZipOutputStream(new FileOutputStream(zipFile));
			output.setEncoding("utf-8");
			output.setFallbackToUTF8(true);
			output.setLevel(-1);
			// 顶层目录开始
			zipFile(output, file, "");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}

	/**
	 * 压缩文件为zip格式
	 * 
	 * @param output
	 *            ZipOutputStream对象
	 * @param file
	 *            要压缩的文件或文件夹
	 * @param basePath
	 *            条目根目录
	 * @throws IOException
	 */
	private static void zipFile(ZipOutputStream output, File file, String basePath) throws IOException {
		FileInputStream input = null;
		try {
			// 文件为目录
			if (file.isDirectory()) {
				// 得到当前目录里面的文件列表
				File list[] = file.listFiles();
				basePath = basePath + (basePath.length() == 0 ? "" : "/") + file.getName();
				// 循环递归压缩每个文件
				for (File f : list)
					zipFile(output, f, basePath);
			} else {
				// 压缩文件
				basePath = (basePath.length() == 0 ? "" : basePath + "/") + file.getName();
				Debug.log.debug(basePath);
				output.putNextEntry(new ZipEntry(basePath));
				input = new FileInputStream(file);
				int readLen = 0;
				byte[] buffer = new byte[1024 * 8];
				while ((readLen = input.read(buffer, 0, buffer.length)) != -1)
					output.write(buffer, 0, readLen);
				output.closeEntry();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (input != null)
				input.close();
		}
	}

	public static void main(String[] args) throws Exception {
		zip(new File("E:\\vcworkspace\\linux内核0.11注释版1"), new File("E:\\vcworkspace\\linux内核0.11注释版1.zip"));
	}

}
