package com.shuishuo.ftp4j;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FTP {
	public static void main(String[] args) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
		FTPClient ftp = new FTPClient();
		ftp.connect("ftp.watchdata.com", 21);
		ftp.login("rk", "rk^&21");
		ftp.setCharset("GBK");
		ftp.setPassive(true);

		String dir = ftp.currentDirectory();
		System.out.println("currentDirectory:" + dir);
//		String[] ftpFileNames = ftp.listNames();
//		for (String ftpFileName : ftpFileNames) {
//			System.out.println(ftpFileName);
//		}
		
		File fileDir = new File("E:/baidu player/DBS/");
		FileUtils.deleteDirectory(fileDir);
		fileDir.mkdirs();
		
		FTPFile[] ftpFiles = ftp.list();
		for (FTPFile ftpFile : ftpFiles) {
			int fileType = ftpFile.getType();
			if (fileType == FTPFile.TYPE_DIRECTORY)
				System.out.println(ftpFile.getName() + "|" + ftpFile.getType() + "|" + ftpFile.getSize() + "|" + ftpFile.getModifiedDate());
			else if (fileType == FTPFile.TYPE_FILE) {
				ftp.download(ftpFile.getName(), new File("E:/baidu player/DBS/" + ftpFile.getName()), new MyFTPDataTransferListener(ftpFile.getSize()));
				System.out.println(ftpFile.getName() + "|" + ftpFile.getType() + "|" + ftpFile.getSize() + "|" + ftpFile.getModifiedDate() + "successful done.");
			}
		}
		System.out.println("all successful done.");

		// ftp.upload(new File("E:/baidu player/DBS/123.txt"));
		// ftp.deleteFile("123.txt");
		System.out.println("upload successful done.");
		ftp.disconnect(true);
	}
}
