package com.echeloneditor.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.echeloneditor.utils.Config;

public class FileAction {
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	public static int LINE_SEPARATOR_LEN = LINE_SEPARATOR.length();
	public static String DEFAULT_FILE_ENCODE =Config.getValue("CONFIG", "defaultCharset");
	public static int BUFFER_SIZE = Integer.parseInt(Config.getValue("CONFIG", "ioBuffer")) << 20;// M
	public static String USER_DIR = System.getProperty("user.dir");
	public static int BIG_FILE_READ_UNIT_SIZE = Integer.parseInt(Config.getValue("CONFIG", "bigFileReadUnitSize"));

	public FileAction() {

	}

	/**
	 * 
	 * @param filePath
	 * @param FileContent
	 * @param encode
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void save(String filePath, String fileContent, String encode) throws IOException {
		File file = null;
		OutputStream os = null;
		OutputStreamWriter osw = null;
		Writer writer = null;
		try {
			// 创建文件
			file = new File(filePath);
			// 文件输出流
			os = new FileOutputStream(file);
			// 字符流通向字节流的桥梁
			osw = new OutputStreamWriter(os, encode);
			// 缓冲区
			writer = new BufferedWriter(osw, BUFFER_SIZE);
			// 将字符写到文件中
			writer.write(fileContent);
			// 刷新缓冲区
			writer.flush();
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			if (os != null) {
				os.close();
			}
			if (osw != null) {
				osw.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}
}
