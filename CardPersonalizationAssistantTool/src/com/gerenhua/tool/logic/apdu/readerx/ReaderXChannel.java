package com.gerenhua.tool.logic.apdu.readerx;

import com.gerenhua.tool.logic.apdu.IAPDUChannel;
import com.gerenhua.tool.logic.apdu.readerx.ReaderXOperate.CLibrary;
import com.gerenhua.tool.utils.Config;
import com.watchdata.commons.lang.WDByteUtil;

public class ReaderXChannel extends IAPDUChannel {
	private static CLibrary handle = null;

	public ReaderXChannel() {
		handle = CLibrary.INSTANCE;
	}

	@Override
	public String send(String commandApdu) {
		logger.debug("Send【" + commandApdu.toUpperCase() + "】");
		// byte len = (byte) (commandApdu.length() / 2);
		byte[] apduBuffer = WDByteUtil.HEX2Bytes(commandApdu);
		String recv = handle.Send((byte) apduBuffer.length, apduBuffer);
		String sw = recv.substring(recv.length() - 4, recv.length());
		String sw1 = sw.substring(0, 2);
		String sw2 = sw.substring(sw.length() - 2, sw.length());

		switch (Integer.parseInt(sw1, 16)) {
		case 0x90:
			logger.debug("Recv【" + recv + "】");
			break;
		case 0x61:
			logger.debug("Recv【" + recv + "】");
			recv = send("00C00000" + sw2);
			break;
		case 0x6C:
			logger.debug("Recv【" + recv + "】");
			recv = send(commandApdu.substring(0, commandApdu.length() - 2) + sw2);
			break;
		case 0x63:
			if (sw2.equalsIgnoreCase("10")) {
				logger.debug("Recv【" + recv + "】");
				recv = recv.substring(0, recv.length() - 4);
				recv += send(commandApdu.substring(0, 6) + "01" + commandApdu.substring(8));
			}
			break;
		default:
			logger.error("Recv【" + recv + "】["+Config.getValue("Exception_Code", recv)+"]");
			break;
		}
		// if (sw.equalsIgnoreCase("9000")) {
		// logger.debug("recv[" + recv + "]");
		// } else if (sw.startsWith("61")) {
		// logger.debug("recv[" + recv + "]");
		// recv = send("00C00000" + sw2);
		// } else if (sw.toUpperCase().startsWith("6C")) {
		// logger.debug("recv[" + recv + "]");
		// recv = send(commandApdu.substring(0, commandApdu.length() - 2) + sw2);
		// } else if (sw.startsWith("6310")) {
		// logger.debug("recv[" + recv + "]");
		// recv=recv.substring(0,recv.length()-4);
		// recv+= send(commandApdu.substring(0, 6)+"01"+commandApdu.substring(8));
		// return recv;
		// } else {
		// logger.error("recv[" + recv + "]");
		// }
		return recv;
	}

	@Override
	public boolean init(String readName) {
		// TODO Auto-generated method stub
		int ret = handle.Open("USB1", 1, (byte) 1);
		if (ret == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String reset() {
		// TODO Auto-generated method stub
		return handle.Reset();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		handle.Close();
	}

	public static void main(String[] args) {
		IAPDUChannel bo = new ReaderXChannel();
		bo.init("USB1");
		bo.reset();
		bo.close();
	}
}
