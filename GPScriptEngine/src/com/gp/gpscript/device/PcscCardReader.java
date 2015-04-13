package com.gp.gpscript.device;

import javax.smartcardio.ResponseAPDU;

import org.apache.log4j.Logger;

import com.gp.gpscript.script.ApduChannel;
import com.watchdata.cardpcsc.CardPcsc;
import com.watchdata.commons.lang.WDStringUtil;

public class PcscCardReader implements ApduChannel {
	public Logger log = Logger.getLogger(PcscCardReader.class);
	public PcscCardReader(String reader){
		CardPcsc.connectReader(reader);
	}

	@Override
	public int init(String p1, String p2) {
		return 0;
	}

	@Override
	public byte[] reset() {
		return CardPcsc.resetCard().getBytes();
	}

	@Override
	public byte[] sendApdu(int CLA, int INS, int P1, int P2, byte[] toSendData, int LE) {
		ResponseAPDU responseAPDU=CardPcsc.sendApdu(CLA, INS, P1, P2, toSendData, LE);
		return responseAPDU.getBytes();
	}

	@Override
	public byte[] sendApdu(byte[] toSendData, int len) {
		return (CardPcsc.sendApdu(toSendData)).getBytes();
	}
public static void main(String[] args) {
	System.out.println(WDStringUtil.getRandomHexString(8));
}
}
