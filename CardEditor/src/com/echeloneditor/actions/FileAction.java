package com.echeloneditor.actions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.echeloneditor.utils.Config;

public class FileAction {
	private static final Logger log = Logger.getLogger(FileAction.class);
	private int BUFFER_SIZE=Integer.parseInt(Config.getValue("CONFIG", "ioBuffer"))<<20;//M
	public FileAction() {
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedCharsetException
	 * @throws Exception
	 */
	public Map<String, String> open(String filePath) throws IOException, FileNotFoundException, UnsupportedEncodingException {
		StringBuilder sb =new StringBuilder();
		Map<String, String> map = new HashMap<String, String>();
		File file = new File(filePath);
		long fileSize=file.length();
		
		//if(fileSize<=40*1024*1024){
			//Charset charset = detector.detectCodepage(file.toURL());
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			BufferedReader br=new BufferedReader(new InputStreamReader(fis, "UTF-8"), 5*1024*1024);
			String line=null;
			while ((line=br.readLine())!=null) {
				sb.append(line);
			}
			
			/*byte[] b = new byte[bis.available()];
			bis.read(b, 0, b.length);
			// 文件内容
			if (!charset.name().isEmpty() && !charset.name().equals("void")) {
				fileContent = new String(b, charset.name());
				log.debug("detect file's charset:" + charset.name());
			} else {
				log.debug("detect return void ,default charset:" + charset.name());
				fileContent = new String(b, "UTF-8");
			}*/
			// 文件编码
			map.put("encode","UTF-8");
			fis.close();
			bis.close();
		/*}else{
			RandomAccessFile randomAccessFile=new RandomAccessFile(filePath, "r");
			randomAccessFile.readLine();
		}*/
		// 文件大小
		map.put("fileSize", String.valueOf(fileSize));

		//fileContent = fileContent.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
		map.put("fileContent", sb.toString());
		// log.debug("open file done.");
		return map;
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
			writer = new BufferedWriter(osw,BUFFER_SIZE);
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
