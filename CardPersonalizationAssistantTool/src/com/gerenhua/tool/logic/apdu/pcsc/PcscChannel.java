package com.gerenhua.tool.logic.apdu.pcsc;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ResponseAPDU;

import com.gerenhua.tool.log.Log;
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
public class PcscChannel implements IAPDUChannel{
	private static Log logger = new Log();	
	public PcscChannel(){
	}
	
	public boolean init(String reader) {
		CardPcsc.LOG_ENABLE=false;
		return CardPcsc.connectReader(reader);
	}
	public String reset(){
		return WDByteUtil.bytes2HEX(CardPcsc.resetCard().getBytes())+"9000";
	}
	
	public String send(String commandApdu) {
		logger.debug("send[" + commandApdu.toUpperCase() + "]");
		ResponseAPDU responseAPDU = CardPcsc.sendApdu(WDByteUtil.HEX2Bytes(commandApdu));
		String resp=WDByteUtil.bytes2HEX(responseAPDU.getBytes());
		if (responseAPDU.getSW()==0x9000) {
			logger.debug("recv[" +resp+ "]");
		}else {
			logger.error("recv[" + resp + "]");
		}
		return resp;
	}
	
	public List<String> getReaderList(){
		List<String> list=new ArrayList<String>();
		String[] termList=CardPcsc.getReaderList();
		
		if(termList != null){
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
	
	public void close(){
		CardPcsc.disConnectReader();
	}
}
