package com.sepp.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;

import com.echeloneditor.vo.TcpConnector;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class TestDemo {
	public void test(Socket socket) throws UnknownHostException, IOException {
		// SessionClient sessionClient = new SessionClient("hello", "127.0.0.1", 9000);
		// TcpConnector tcpConnector = new TcpConnector("127.0.0.1", 3003);

		File file = new File("G:\\1000_hotel_data.txt");
		// File file = new File("D:\\eclipse-SDK-4.2.rar");
		FileInputStream fis = new FileInputStream(file);

		int a = fis.available();

		byte[] bs = new byte[a];

		fis.read(bs);
		fis.close();
		String filelength = Integer.toHexString(a);
		while (filelength.length() < 8) {
			filelength = "0" + filelength;
		}
		byte[] lenth = WDByteUtil.HEX2Bytes(filelength);
		byte[] temp = new byte[a + 4];
		System.arraycopy(lenth, 0, temp, 0, 4);
		System.arraycopy(bs, 0, temp, 4, a);
		// System.out.println(new String(temp));
		// sessionClient.send(temp, "hello");

		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		out.write(temp);
		out.flush();
		// out.close();

		// InputStream in= socket.getInputStream();

		// byte[] tem1p=new byte[1024];
		String www = read.readLine();
		System.out.println(Thread.currentThread().getId()+":"+www);
	}

	public void test1() throws UnknownHostException, IOException {
		SessionClient sessionClient = new SessionClient("9000", "127.0.0.1", 9000);
		TcpConnector tcpConnector = new TcpConnector("10.0.97.124", 5050);
		sessionClient.addConnector("helo", tcpConnector);
		File file = new File("D:\\ccspace\\Business_WD_CAMS_Prj_Dev\\PayID_Business_VOB\\Business_WD_CAMS\\WD_CAMS\\Product\\应用模板\\湖北农信\\测试数据\\Native\\湖北农信金融社保卡送检数据（20121207）.rar");
		FileInputStream fis = new FileInputStream(file);

		int a = fis.available();

		byte[] bs = new byte[a];

		fis.read(bs);
		fis.close();
		String filelength = Integer.toHexString(a);
		while (filelength.length() < 8) {
			filelength = "0" + filelength;
		}
		byte[] lenth = WDByteUtil.HEX2Bytes(filelength);
		byte[] temp = new byte[a + 4];
		System.arraycopy(lenth, 0, temp, 0, 4);
		System.arraycopy(bs, 0, temp, 4, a);

		sessionClient.send(temp, "helo");

		String www = sessionClient.recive("helo");
		System.out.println(www);

		sessionClient.send(temp, "9000");

		System.out.println(sessionClient.recive("9000"));

	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		JFileChooser jFileChooser=new JFileChooser(".");
		int ret=jFileChooser.showOpenDialog(null);
		if (ret==JFileChooser.APPROVE_OPTION) {
			File file= jFileChooser.getSelectedFile();
			SessionClient sessionClient=new SessionClient("test", "10.0.97.68", 9000);
			
			//long len=file.length();
			
			
			FileInputStream fileInputStream=new FileInputStream(file);
			int len=fileInputStream.available();
			byte[] data=new byte[4+8+1+file.getName().getBytes("GBK").length+len];
			byte[] fileBytes=new byte[len];
			
			fileInputStream.read(fileBytes);
			
			String length=Integer.toHexString(len+8);
			length=WDStringUtil.paddingHeadZero(length, 8);
			
			byte[] lenBytes=WDByteUtil.HEX2Bytes(length);
			
			int pos=0;
			System.arraycopy(lenBytes, 0, data, 0, lenBytes.length);
			pos+=lenBytes.length;
			System.arraycopy(WDByteUtil.HEX2Bytes("0F00000010000000"), 0, data, pos, 8);
			int fileNameLen=file.getName().getBytes("GBK").length;
			pos+=8;
			data[pos]=(byte)fileNameLen;
			pos++;
			System.arraycopy(file.getName().getBytes("GBK"), 0, data, pos, fileNameLen);
			pos+=fileNameLen;
			System.arraycopy(fileBytes, 0, data, pos, len);
			
			fileInputStream.close();
			
			sessionClient.send(data, "test");
			String res=sessionClient.recive("test");
			System.out.println(res);
		}
	}
}
