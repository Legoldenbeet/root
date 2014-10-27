package com.sepp.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.vo.Cmd;
import com.sepp.interfaces.Sepp;
import com.sepp.server.PooledConnectionHandler;
import com.watchdata.commons.lang.WDByteUtil;

public class SeppImpl implements Sepp {

	public SeppImpl() {
	}

	@Override
	public short process(byte[] data,String resp) {
		try {
			byte[] cmdHeader = new byte[Sepp.CMD_LEN];
			System.arraycopy(data, 0, cmdHeader, 0, Sepp.CMD_LEN);

			Cmd cmd = parse(cmdHeader);
			if (cmd != null) {
				if (cmd.getCla() != 0x0F) {
					// 发送错误指令给对方

					return EXCEPTION_INS_NOT_SUPPORT;
				}
				switch (cmd.getIns()) {
				case Sepp.INS_FILE_OPEN:
					receiveOpen(data, Sepp.FILE_NAME_LEN_OFFSET);
					break;
				case Sepp.INS_FILE_CLOSE:
					closeFile();
					break;
				case Sepp.INS_TERM_INFO_NAME:
					resp+=getTermUserName();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			resp+=e.getMessage();
		}
		String lc=Integer.toHexString(resp.length()/2);
		resp=lc+resp;
		return SUCCESSFUL_DONE_WITHOUT_ERROR;
	}

	/**
	 * 解析收到的指令
	 */
	public Cmd parse(byte[] cmdHeader) {
		if (cmdHeader == null) {
			return null;
		}

		Cmd cmd = new Cmd();
		cmd.setCla(cmdHeader[0]);
		cmd.setIns(cmdHeader[1]);
		cmd.setP1(cmdHeader[2]);
		cmd.setP2(cmdHeader[3]);

		byte[] lcBytes = new byte[4];
		System.arraycopy(cmdHeader, 4, lcBytes, 0, 4);
		cmd.setLc(Long.parseLong(WDByteUtil.bytes2HEX(lcBytes), 16));

		return cmd;
	}

	@Override
	public void receiveOpen(byte[] data, short offset) throws Exception {
		short fileNameLen = (short) data[offset];
		byte[] fileNameBytes = new byte[fileNameLen];
		offset += 1;
		System.arraycopy(data, offset, fileNameBytes, 0, fileNameLen);

		String fileName = new String(fileNameBytes, "GBK");

		File file = new File(FileAction.USER_DIR + "/tmp/" + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bw = new BufferedOutputStream(fos);
		offset += fileNameLen;
		bw.write(data, offset, data.length - offset);
		bw.flush();
		fos.close();
		bw.close();

		PooledConnectionHandler.processRequest(file);
		//FileHander fileHander = new FileHander(tabbedPane, statusObject);
		//fileHander.openFileWithFilePath(file.getPath(), FileAction.DEFAULT_FILE_ENCODE);
		// JOptionPane.showMessageDialog(null, "ok");
	}

	@Override
	public void closeFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendOpen(byte[] data, short offset) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTermUserName() {
		return System.getProperty("user.name");
	}
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.name"));
	}
}
