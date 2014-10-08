package com.echeloneditor.actions;

import java.io.IOException;

import com.watchdata.commons.crypto.WD3DesCryptoUtil;
import com.watchdata.commons.jce.JceBase.Padding;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDEncodeUtil;

public class DecryptTemplateAction {
	public static String doAction(String readFileCnt) throws IOException{
		String result="";
		
		String deReadFileCnt =WD3DesCryptoUtil.cbc_decrypt("57415443484441544154696D65434F53", readFileCnt, Padding.NoPadding, "0000000000000000");
		deReadFileCnt = deReadFileCnt.substring(0, deReadFileCnt.lastIndexOf("80"));
		
		String hash = deReadFileCnt.substring(deReadFileCnt.length() - 32);
		
		String profileData = deReadFileCnt.substring(0, deReadFileCnt.length() - 32);
		byte[] md5=WDEncodeUtil.md5(WDByteUtil.HEX2Bytes(profileData));
		
		if (hash.equalsIgnoreCase(WDByteUtil.bytes2HEX(md5))) {
			result=new String(WDByteUtil.HEX2Bytes(profileData),"GBK");
		}else {
			throw new IOException("this is not a template.");
		}
		return result;
	}
}
