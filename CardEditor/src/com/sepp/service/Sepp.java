package com.sepp.service;

import java.util.ArrayList;

import com.echeloneditor.utils.Config;
import com.echeloneditor.vo.Cmd;

public interface Sepp {
	public final byte headerLen=Byte.parseByte(Config.getValue("CONFIG", "seppHeaderLen"));
	public final int packetSize=Integer.parseInt(Config.getValue("CONFIG", "seppPacketSize"));
	public final byte CLA_TERMINAL_INFO=0x0A;
	public final byte CLA_FILE_OPERATION=0x0F;
	
	public final byte INS_FILE_OPEN=(byte)0x00;
	public final byte INS_FILE_CLOSE=(byte)0x01;
	public final byte INS_TERM_INFO_NAME=(byte)0x02;

	public final byte CMD_LEN=(byte)0x04;
	public final byte FILE_NAME_LEN_OFFSET=CMD_LEN;
	public final byte FILE_NAME_LEN=(byte)0x1;
	
	public void open(int seppPort);
	public String send(byte[] msg,String ip,int port);
	
	public String process(byte[] cmdHeader);
	public Cmd parse(byte[] cmdHeader);
	public void receiveOpen(byte[] data,short offset) throws Exception ;
	public void sendOpen(byte[] data,short offset) throws Exception ;
	public void closeFile();
	
	public ArrayList<String> scanFriend() throws Exception;
	public String getTermUserName();
	public boolean sendFile(String filePath, String targetIp) throws Exception ;
}
