package com.sepp.interfaces;

import com.sepp.vo.Cmd;

public interface Sepp {
	public final byte CLA_FILE_OPERATION=0x0F;
	
	public final byte INS_FILE_OPEN=(byte)0x00;
	public final byte INS_FILE_CLOSE=(byte)0x01;

	public final short CMD_LEN=(byte)0x08;
	public final short FILE_NAME_LEN_OFFSET=CMD_LEN;
	
	public void process(byte[] cmdHeader);
	public Cmd parse(byte[] cmdHeader);
	public void openFile(byte[] data,short offset) throws Exception ;
	public void closeFile();
}
