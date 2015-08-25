package com.gerenhua.tool.logic.apdu;

import com.gerenhua.tool.log.Log;

public abstract class IAPDUChannel {
	public static Log logger = new Log();
	/**
	 * 指令发送
	 * @param commandApdu
	 * @return
	 */
	public abstract String send(String commandApdu);
	/**
	 * 通道初始化
	 * @param readName
	 */
	public abstract boolean init(String readName);
	
	public abstract String reset();
	
	public abstract void close();

}
