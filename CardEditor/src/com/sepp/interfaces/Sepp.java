package com.sepp.interfaces;

import com.echeloneditor.vo.Cmd;

public interface Sepp {
	public final byte CLA_TERMINAL_INFO=0x0A;
	public final byte CLA_FILE_OPERATION=0x0F;
	
	public final byte INS_FILE_OPEN=(byte)0x00;
	public final byte INS_FILE_CLOSE=(byte)0x01;
	public final byte INS_TERM_INFO_NAME=(byte)0x02;

	public final short CMD_LEN=(byte)0x08;
	public final short FILE_NAME_LEN_OFFSET=CMD_LEN;
	
	public final short EXCEPTION_INS_NOT_SUPPORT=(short)0x6982;
	public final short SUCCESSFUL_DONE_WITHOUT_ERROR=(short)0x9000;
	
	public int process(byte[] cmdHeader,byte[] resp);
	public Cmd parse(byte[] cmdHeader);
	public void receiveOpen(byte[] data,short offset) throws Exception ;
	public void sendOpen(byte[] data,short offset) throws Exception ;
	public void closeFile();
	
	public String getTermUserName();
}
