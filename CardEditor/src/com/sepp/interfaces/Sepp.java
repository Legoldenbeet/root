package com.sepp.interfaces;

import java.util.ArrayList;

import com.echeloneditor.vo.Cmd;

public interface Sepp {
	public final byte CLA_TERMINAL_INFO=0x0A;
	public final byte CLA_FILE_OPERATION=0x0F;
	
	public final byte INS_FILE_OPEN=(byte)0x00;
	public final byte INS_FILE_CLOSE=(byte)0x01;
	public final byte INS_TERM_INFO_NAME=(byte)0x02;

	public final short CMD_LEN=(byte)0x08;
	public final short FILE_NAME_LEN_OFFSET=CMD_LEN;
	
	public final byte[] EXCEPTION_INS_NOT_SUPPORT="6982".getBytes();
	public final byte[] SUCCESSFUL_DONE_WITHOUT_ERROR="9000".getBytes();
	
	public byte[] process(byte[] cmdHeader,byte[] resp,byte len);
	public Cmd parse(byte[] cmdHeader);
	public void receiveOpen(byte[] data,short offset) throws Exception ;
	public void sendOpen(byte[] data,short offset) throws Exception ;
	public void closeFile();
	
	public ArrayList<String> scanFriend() throws Exception;
	public String getTermUserName();
}
