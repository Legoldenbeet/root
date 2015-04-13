package com.watchdata.test;

import javax.smartcardio.ResponseAPDU;

import com.watchdata.cardpcsc.CardPcsc;
import com.watchdata.commons.lang.WDByteUtil;

public class TestPcsc {
	public static void main(String[] args) {
		// 获取列表
		String[] libiao = CardPcsc.getReaderList();
		if (libiao != null) {
			for (int i = 0; i < libiao.length; i++) {
				System.out.println(libiao[i]);
			}
		} else {
			System.out.println("error1");
		}
		// 连接卡片
		/*int ret = cardPcsc.connectReaderModel("WatchData CRW-V Plus PC/SC Reader 0");
		cardPcsc.disConnectReader();*/
		boolean ret1 = CardPcsc.connectReader("SCM Microsystems Inc. SCR3310 v2.0 USB SC Reader 0");
				
		//System.out.println(ret);
		
		/*if (cardPcsc.verifyPin("123456")) {
			System.out.println("登录成功");
		}else {
			System.out.println("登录失败");
		}
*/
		// 复位
		byte[] data = CardPcsc.resetCard();
		if (data == null) {

		} else {
			System.out.println(WDByteUtil.bytes2HEX(data));
		}

		// 发送指令1
		ResponseAPDU responseAPDU = CardPcsc.sendApdu(0x00, 0x84, 0x00, 0x00, null, 0x08);
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
		responseAPDU = CardPcsc.sendApdu(0x00, 0xA4, 0x04, 0x00, null, 0x00);
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
		responseAPDU = CardPcsc.sendApdu(0x80, 0xca, 0x57, 0x44, null, 0x00);
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getData()));
		
		responseAPDU = CardPcsc.sendApdu(WDByteUtil.HEX2Bytes("0084000008"));
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
		responseAPDU = CardPcsc.sendApdu(WDByteUtil.HEX2Bytes("80ca574400"));
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
		responseAPDU = CardPcsc.sendApdu(WDByteUtil.HEX2Bytes("00A4040000"));
		System.out.println(responseAPDU.getSW()+":"+WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
		// 断开连接
		CardPcsc.disConnectReader();
	}
}
