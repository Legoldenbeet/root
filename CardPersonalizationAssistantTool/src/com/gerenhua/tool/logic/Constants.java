package com.gerenhua.tool.logic;


/**
 * 
 * @description: 常量定义类
 * @author: juan.jiang Apr 16, 2012
 * @version: 1.0.0
 * @modify:
 * @Copyright: watchdata
 */
public class Constants {
	public static final String PSE = "315041592E5359532E4444463031";
	public static final String PPSE = "325041592E5359532E4444463031";
	
	private static final byte CLA_GP = (byte) 0x80;
	private static final byte CLA_MAC = (byte) 0x84;
	private static final byte INS_INITIALIZE_UPDATE = (byte) 0x50;
	private static final byte INS_INSTALL = (byte) 0xE6;
	private static final byte INS_LOAD = (byte) 0xE8;
	private static final byte INS_DELETE = (byte) 0xE4;
	private static final byte INS_GET_STATUS = (byte) 0xF2;
	private static final byte INS_SET_STATUS = (byte) 0xF0;
	private static final byte INS_PUT_KEY = (byte) 0xD8;
	
	public static final String SW_SUCCESS = "9000";
	public static final String SW_FILE_NOT_FIND = "6A83";
	
	/** 日期格式定义 */
	public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATE = "yyyyMMdd";
	public static final String FORMAT_SHORT_DATE = "yyMMdd";
	public static final String FORMAT_TIME = "HHmmss";
	
}
