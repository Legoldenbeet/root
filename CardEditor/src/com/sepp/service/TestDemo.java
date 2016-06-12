package com.sepp.service;

import java.io.File;

import javax.swing.JFileChooser;

import com.echeloneditor.main.CardEditor;

public class TestDemo {
	public static void sendFile() throws Exception{
		JFileChooser jFileChooser=new JFileChooser(".");
		int ret=jFileChooser.showOpenDialog(null);
		if (ret==JFileChooser.APPROVE_OPTION) {
			File file= jFileChooser.getSelectedFile();
//			SessionClient sessionClient=new SessionClient("test", "10.0.97.68", 9000);
			
//			long len=file.length();
			
			
//			FileInputStream fileInputStream=new FileInputStream(file);
//			int len=fileInputStream.available();
//			byte[] data=new byte[4+1+file.getName().getBytes("GBK").length+len];
//			byte[] fileBytes=new byte[len];
//			
//			fileInputStream.read(fileBytes);
//			
////			String length=Integer.toHexString(len+8);
////			length=WDStringUtil.paddingHeadZero(length, 8);
//			
////			byte[] lenBytes=WDByteUtil.HEX2Bytes(length);
//			
//			int pos=0;
////			System.arraycopy(lenBytes, 0, data, 0, lenBytes.length);
////			pos+=lenBytes.length;
//			System.arraycopy(WDByteUtil.HEX2Bytes("0F000000"), 0, data, pos, 4);
//			int fileNameLen=file.getName().getBytes("GBK").length;
//			pos+=4;
//			data[pos]=(byte)fileNameLen;
//			pos++;
//			System.arraycopy(file.getName().getBytes("GBK"), 0, data, pos, fileNameLen);
//			pos+=fileNameLen;
////			byte[] fileLenBytes=WDByteUtil.HEX2Bytes(WDStringUtil.paddingHeadZero(String.valueOf(len), 8));
////			System.arraycopy(fileLenBytes, 0, data, pos, 4);
////			pos+=4;
//			System.arraycopy(fileBytes, 0, data, pos, len);
//			
//			fileInputStream.close();
			
			Sepp sepp=new SeppImpl(CardEditor.tabbedPane,CardEditor.statusObject);
			String res=sepp.sendFile(Sepp.INS_TRANSFER_OPEN,file, "127.0.0.1");
			System.out.println(res);
		}
	}

	public static void main(String[] args) throws Exception {
		sendFile();
	}
}
