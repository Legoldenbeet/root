package com.echeloneditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.watchdata.commons.lang.WDStringUtil;

public class ZipUtil {
	public static String tmpFileDir;
	public static String[] encryptArray=Config.getValue("CONFIG", "specialExt").split(" ");
	public static HashMap<String, String> encryptMap=new HashMap<String, String>();
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
		File tmpFile=null;
		try {
			for (int i = 0; i < encryptArray.length; i++) {
				encryptMap.put(encryptArray[i], String.valueOf(i));
			}
			
			//创建缓存文件夹
			tmpFileDir=zipFile.getPath()+"_"+WDStringUtil.getRandomHexString(8);
			tmpFile=new File(tmpFileDir);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			//拷贝原始文件夹到缓存
			FileUtils.copyDirectory(file, tmpFile);
			
			preDecrypt(tmpFile, "");
			output = new ZipOutputStream(new FileOutputStream(new File(tmpFileDir+".zip")));
			output.setEncoding("utf-8");
			output.setFallbackToUTF8(true);
			output.setLevel(-1);
			// 顶层目录开始
			zipMFile(output, tmpFile, "");
			
			FileUtils.deleteDirectory(tmpFile);
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
	 * 解密预处理
	 * @param file
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	public static boolean preDecrypt(File file, String basePath) throws IOException{
		boolean excuteResult=false;
		try {
			// 文件为目录
			if (file.isDirectory()) {
				// 得到当前目录里面的文件列表
				File list[] = file.listFiles();
				basePath = basePath + (basePath.length() == 0 ? "" : "/") + file.getName();
				// 循环递归压缩每个文件
				for (File f : list)
					preDecrypt(f, basePath);
			} else {
				// 压缩文件
				basePath = (basePath.length() == 0 ? "" : basePath + "/") + file.getName();
				Debug.log.debug(basePath);
				
				String destPath="";
				
				if (isEncrypted(file.getName())) {
					if (file.getName().endsWith(".a51")) {
						if (!file.canWrite()) {
							file.setWritable(true);
						}
						File file2=new File(file.getPath().substring(0,file.getPath().length())+".c");
						file.renameTo(file2);
						file=file2;
						
						destPath=file.getName().substring(0, file.getName().lastIndexOf(".c"));
					}else {
						destPath=file.getName();
					}
					//方案一
					WindowsExcuter.excute(file.getParentFile(), "cmd.exe /c type "+file.getName()+" >"+destPath+".cardeditor",true);
					//方案二
					//WindowsExcuter.excute(file.getParentFile(), "cmd.exe /c type "+file.getName()+" >"+file.getName());
					
					FileUtils.deleteQuietly(file);
				}
			}
		} catch (Exception ex) {
			excuteResult=false;
			ex.printStackTrace();
		} 
		return excuteResult;
	}
	
	private static boolean isEncrypted(String fileName){
		String fileExt=fileName.substring(fileName.lastIndexOf('.'), fileName.length());
		if(encryptMap.containsKey(fileExt)){
			return true;
		}
		return false;
	}
	
	/**
	 * 处理中间文件
	 * @param output
	 * @param file
	 * @param basePath
	 * @throws IOException
	 */
	private static void zipMFile(ZipOutputStream output, File file, String basePath) throws IOException {
		FileInputStream input = null;
		try {
			// 文件为目录
			if (file.isDirectory()) {
				// 得到当前目录里面的文件列表
				File list[] = file.listFiles();
				basePath = basePath + (basePath.length() == 0 ? "" : "/") + file.getName();
				// 循环递归压缩每个文件
				for (File f : list)
					zipMFile(output, f, basePath);
			} else {
				// 压缩文件
				basePath = (basePath.length() == 0 ? "" : basePath + "/") + file.getName();
				Debug.log.debug(basePath);
				if (basePath.endsWith(".cardeditor")) {
					basePath=basePath.substring(0, basePath.lastIndexOf(".cardeditor"));
				}
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
		//zip(new File("D:\\123\\Eid"), new File("D:\\123\\Eid.zip"));
		//WindowsExcuter.excute(new File("D:\\123\\Eid\\"), "cmd.exe /c type 123.bat >123.txt");
       //WindowsExcuter.excute(new File("D:\\123\\Eid\\"), "cmd.exe /c ipconfig/all");
		new File("D:\\123\\JC30_Platform_20140821_1001.zip_5FB65A3C.zip").renameTo(new File("D:\\123\\JC30_Platform_20140821_1001.zip_5FB65A3C.zip.helo"));
	}

}
