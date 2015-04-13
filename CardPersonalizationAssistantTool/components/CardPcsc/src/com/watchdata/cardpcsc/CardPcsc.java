package com.watchdata.cardpcsc;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;

import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDByteUtil;

public class CardPcsc {
	private static Logger log = Logger.getLogger(CardPcsc.class);
	public static CardTerminal cardTerminal=null;
	public static Card card = null;
	public static CardChannel cardChannel=null;
	private static boolean connected = false;

	/**
	 * Provides a list of readers already introduced to the subsystem
	 * 
	 * @return: a list of readers
	 * 
	 */
	public static String[] getReaderList() {
		// 获取终端列表
		List<CardTerminal> ctList = getCardTerminalList();
		if (ctList.isEmpty()) {
			return null;
		}
		String[] tlist = new String[ctList.size()];
		for (int i = 0; i < tlist.length; i++) {
			tlist[i] = ctList.get(i).getName();
		}
		return tlist;
	}

	// 获取CardTerminal list
	private static List<CardTerminal> getCardTerminalList() {
		// 终端LIST
		List<CardTerminal> ctList = new ArrayList<CardTerminal>();

		CardTerminals cardTerminals = getCardTerminals();
		if (cardTerminals == null) {
			return ctList;
		}
		try {
			ctList = cardTerminals.list();
		} catch (CardException e) {
			log.error("getReadList error:" + e.getMessage());
			// e.printStackTrace();
		}
		return ctList;
	}

	/**
	 * 获取CardTerminals
	 * 
	 * @return CardTerminals
	 */
	public static CardTerminals getCardTerminals() {
		TerminalFactory factory = TerminalFactory.getDefault();
		CardTerminals cardTerminals = factory.terminals();
		return cardTerminals;
	}

	/**
	 * connect the reader(CAD)
	 * @param reader
	 * @return true:success false:fail
	 */
	public static boolean connectReader(String reader) {
		if (WDAssert.isEmpty(reader)) {
			return false;
		}
		cardTerminal = getCardTerminals().getTerminal(reader);

		if (cardTerminal == null) {
			log.error("connection reader error.");
			return false;
		}

		try {
			if (cardTerminal.isCardPresent()) {
				card = cardTerminal.connect("*");
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("connection card error:" + e.getMessage());
			// e.printStackTrace();
			return false;
		}

		connected = true;
		log.debug("connection card success.");
		return true;
	}

	/**
	 * Get the ATR of the current card
	 * 
	 * @return: the reset data
	 * 
	 */
	public static byte[] resetCard() {
		if (connected == false) {
			log.error("please connect reader first.");
			return null;
		}
		ATR atr = null;
		try {
			if (card != null) {
				atr = card.getATR();
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("card reset failure:" + e.getMessage());
			return null;
		}
		if (atr == null) {
			return null;
		} else {
			log.debug("ATR:" +WDByteUtil.bytes2HEX(atr.getBytes()));
			return atr.getBytes();
		}
	}

	/**
	 * Send data to the card terminal.
	 * 
	 * @param CLA
	 *            CLA byte of APDU (byte #0)
	 * @param INS
	 *            INS byte of APDU (byte #1)
	 * @param P1
	 *            P1 byte of APDU (byte #2)
	 * @param P2
	 *            P2 byte of APDU (byte #3)
	 * @param data
	 *            byte array containing command data of APDU. Command data starts at byte #5.
	 * @param LE
	 *            LE byte of APDU (appended to APDU). This byte is omitted if parameter has value -1.
	 * 
	 * @return: the data returned from the card
	 * 
	 */
	public static ResponseAPDU sendApdu(int CLA, int INS, int P1, int P2, byte[] data, int LE) {
		ResponseAPDU responseAPDU = null;
		if (connected == false) {
			log.error("please connect reader first.");
			return responseAPDU;
		}
		try {
			if (card != null) {
				if (LE==0) {
					LE=0x100;//256
				}
				// 通道默认基本逻辑通道
				cardChannel = card.getBasicChannel();
				// 指令
				CommandAPDU commandAPDU;
				if (data==null) {
					if (LE==-1) {
						commandAPDU=new CommandAPDU(CLA, INS, P1, P2);//case 1
					}else {    
						commandAPDU = new CommandAPDU(CLA, INS, P1, P2, LE);//case 2
					}
				}else {
					if (LE==-1) {
						commandAPDU=new CommandAPDU(CLA, INS, P1, P2, data);//case 3
					}else {
						commandAPDU = new CommandAPDU(CLA, INS, P1, P2, data, LE);//case 4
					}
				}
				
				log.debug("send:" + WDByteUtil.bytes2HEX(commandAPDU.getBytes()));
				// 发送指令，返回响应
				responseAPDU = cardChannel.transmit(commandAPDU);
				// 响应数据
				log.debug("resp:" + WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SendApdu error:" + e.getMessage());
			return responseAPDU;
		}
		return responseAPDU;
	}

	/**
	 * Send data to the card terminal and get the 
	 * response as an ResponseAPDU object
	 * @param data send APDU command
	 * @return ResponseAPDU
	 */
	public static ResponseAPDU sendApdu(byte[] data) {
		ResponseAPDU responseAPDU=null;
		if (connected == false) {
			log.error("please connect reader first.");
			return responseAPDU;
		}
		if (data == null) {
			log.error("data invalid cann't be null.");
			return responseAPDU;
		}
		try {
			if (card != null) {
				// 通道
				cardChannel = card.getBasicChannel();
				// 指令
				CommandAPDU commandAPDU = new CommandAPDU(data);
				log.debug("send:" + WDByteUtil.bytes2HEX(commandAPDU.getBytes()));
				// 发送指令，返回响应
				responseAPDU = cardChannel.transmit(commandAPDU);
				// 响应数据
				log.debug("resp:" + WDByteUtil.bytes2HEX(responseAPDU.getBytes()));
			}
		} catch (Exception e) {
			log.error("SendApdu error:" + e.getMessage());
			return responseAPDU;
		}
		return responseAPDU;
	}

	/**
	 * Close the connect to current Smart Card reader
	 */
	public static void disConnectReader() {
		disConnectReader(false);
	}
	/**
	 * 
	 * @param reset
	 */
	private static void disConnectReader(boolean reset) {
		try {
			if (card != null) {
				card.disconnect(reset);
				connected = false;
			}
		} catch (Exception e) {
			log.error("card disconnection error:" + e.getMessage());
		}
		log.debug("card disconnect success.");
	}
	public static void main(String[] args) {
		System.out.println(-1&0x00ff);
	}
}