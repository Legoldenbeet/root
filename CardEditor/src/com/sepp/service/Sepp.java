package com.sepp.service;

import java.io.File;
import java.util.ArrayList;

import com.echeloneditor.utils.Config;

public interface Sepp {
	public final byte headerLen = Byte.parseByte(Config.getValue("CONFIG", "seppHeaderLen"));
	public final int packetSize = Integer.parseInt(Config.getValue("CONFIG", "seppPacketSize"));

	public final byte CLA_FILE = 0x0F;
	public final byte CLA_TERMINAL = 0x0A;

	public final byte INS_TRANSFER = (byte) 0x00;
	public final byte INS_OPEN = (byte) 0x01;
	public final byte INS_CLOSE = (byte) 0x02;

	public final byte INS_NAME = (byte) 0x00;

	public final byte CMD_LEN = (byte) 0x04;
	public final byte FILE_NAME_LEN_OFFSET = CMD_LEN;
	public final byte FILE_NAME_LEN = (byte) 0x1;

	// 创建sepp服务
	public void startService(int seppPort);
	// public String send(byte[] msg,String ip,int port);
	//接收并且打开文件
	public void receiveFile(File file) throws Exception ;
	// public void sendOpen(byte[] data,short offset) throws Exception ;
	// 发送文件
	public boolean sendFile(File file,String ip) throws Exception;

	// 关闭文件
	public boolean closeFile(String fileName);

	// 扫描同网段好友
	public ArrayList<String> scanFriend() throws Exception;

	// 获取终端信息
	public String getTermUserName();

}
