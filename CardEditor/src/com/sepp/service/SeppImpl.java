package com.sepp.service;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.echeloneditor.actions.FileAction;
import com.sepp.interfaces.Sepp;
import com.sepp.vo.Cmd;
import com.watchdata.commons.lang.WDByteUtil;

public class SeppImpl implements Sepp {
	public JTabbedPane tabbedPane;

	public SeppImpl(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	@Override
	public void process(byte[] data) {
		try {
			byte[] cmdHeader = new byte[Sepp.CMD_LEN];
			System.arraycopy(data, 0, cmdHeader, 0, Sepp.CMD_LEN);

			Cmd cmd = parse(cmdHeader);
			if (cmd != null) {
				if (cmd.getCla() != 0x0F) {
					// 发送错误指令给对方

					return;
				}
				switch (cmd.getIns()) {
				case Sepp.INS_FILE_OPEN:
					openFile(data, Sepp.FILE_NAME_LEN_OFFSET);
					break;
				case Sepp.INS_FILE_CLOSE:
					closeFile();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

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
	public void openFile(byte[] data, short offset) throws Exception {
		short fileNameLen = (short) data[offset];
		byte[] fileNameBytes = new byte[fileNameLen];
		offset+=1;
		System.arraycopy(data, offset, fileNameBytes, 0, fileNameLen);
		
		String fileName = new String(fileNameBytes, "GBK");
		
		File file=new File(FileAction.USER_DIR+"/tmp/"+fileName);
		FileOutputStream fos=new FileOutputStream(file);
		BufferedOutputStream bw=new BufferedOutputStream(fos, FileAction.BUFFER_SIZE);
		offset+=fileNameLen;
		bw.write(data, offset, data.length-offset);
		
		fos.close();
		bw.close();

		JOptionPane.showMessageDialog(null, "ok");
	}

	@Override
	public void closeFile() {
		// TODO Auto-generated method stub

	}

}
