package com.gp.gpscript.device;

import org.apache.log4j.Logger;

import com.gp.gpscript.engine.ScriptEngine;
import com.gp.gpscript.script.ApduChannel;
import com.watchdata.cardpcsc.CardPcsc;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class PcscCardReader implements ApduChannel {
	public Logger log = Logger.getLogger(PcscCardReader.class);
	public static CardPcsc cardPcsc;
	public PcscCardReader(String reader){
		if (cardPcsc==null) {
			cardPcsc=new CardPcsc();
		}
		cardPcsc.connectReader(reader);
	}

	@Override
	public int init(String p1, String p2) {
		return 0;
	}

	@Override
	public byte[] reset() {
		return cardPcsc.resetCard();
	}

	@Override
	public byte[] sendApdu(int CLA, int INS, int P1, int P2, byte[] toSendData, int LE) {
		if (LE==-1) {
			LE=0x00;
		}
		
		return WDByteUtil.HEX2Bytes(cardPcsc.SendApdu(CLA, INS, P1, P2, toSendData, LE));
	}

	@Override
	public byte[] sendApdu(byte[] toSendData, int len) {
		return WDByteUtil.HEX2Bytes(cardPcsc.SendApdu(toSendData));
	}
public static void main(String[] args) {
	System.out.println(WDStringUtil.getRandomHexString(8));
}
}
