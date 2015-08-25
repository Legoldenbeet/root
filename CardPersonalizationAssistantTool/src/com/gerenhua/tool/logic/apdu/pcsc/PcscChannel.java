package com.gerenhua.tool.logic.apdu.pcsc;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ResponseAPDU;

import com.gerenhua.tool.globalplatform.ISO7816;
import com.gerenhua.tool.logic.apdu.IAPDUChannel;
import com.gerenhua.tool.utils.Config;
import com.watchdata.cardpcsc.CardPcsc;
import com.watchdata.commons.lang.WDByteUtil;

/**
 * 
 * @description: PCSC通道
 * @author: liya.xiao Apr 10, 2012
 * @version: 1.0.0
 * @modify:
 * @Copyright: watchdata
 */
public class PcscChannel extends IAPDUChannel {
	public PcscChannel() {
	}

	public boolean init(String reader) {
		CardPcsc.LOG_ENABLE = false;
		return CardPcsc.connectReader(reader);
	}

	public String reset() {
		return WDByteUtil.bytes2HEX(CardPcsc.resetCard().getBytes()) + "9000";
	}

	public String send(String commandApdu) {
		logger.debug("Send【" + commandApdu.toUpperCase() + "】");
		byte[] apduBuffer=WDByteUtil.HEX2Bytes(commandApdu);
		ResponseAPDU responseAPDU = CardPcsc.sendApdu(apduBuffer);
		String resp = WDByteUtil.bytes2HEX(responseAPDU.getBytes());
		switch (responseAPDU.getSW()) {
		case 0x9000:
			logger.debug("Recv【" + resp + "】");
			break;
		case 0x6310:
			logger.debug("Recv【" + resp + "】");
			resp=resp.substring(0,resp.length()-4);
			apduBuffer[ISO7816.OFFSET_P2]=0x01;
			resp+=send(WDByteUtil.bytes2HEX(apduBuffer));
			break;
		default:
			logger.error("Recv【" + resp + "】");
			break;
		}
//		if (responseAPDU.getSW() == 0x9000) {
//			logger.debug("recv[" + resp + "]");
//		} else if (responseAPDU.getSW() == 0x6310) {
//			logger.debug("recv[" + resp + "]");
//		} else {
//			logger.error("recv[" + resp + "]");
//		}
		return resp;
	}

	public List<String> getReaderList() {
		List<String> list = new ArrayList<String>();
		String[] termList = CardPcsc.getReaderList();

		if (termList != null) {
			for (String term : termList) {
				list.add(term);
			}
		}

		for (String board : Config.getItems("BoardList")) {
			list.add(Config.getValue("BoardList", board));
		}

		for (String usb : Config.getItems("USBreaderList")) {
			list.add(Config.getValue("USBreaderList", usb));
		}

		return list;
	}

	public void close() {
		CardPcsc.disConnectReader();
	}
}
