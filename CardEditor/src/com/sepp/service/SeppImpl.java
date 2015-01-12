package com.sepp.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.vo.Cmd;
import com.sepp.client.SessionClient;
import com.sepp.interfaces.Sepp;
import com.sepp.server.PooledConnectionHandler;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class SeppImpl implements Sepp {

	public SeppImpl() {
	}

	@Override
	public byte[] process(byte[] data, byte[] resp, byte len) {
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
					byte[] termName = getTermUserName().getBytes();
					System.arraycopy(termName, 0, resp, 0, termName.length);
					len += termName.length;
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			byte[] errorOut = e.getMessage().getBytes();
			len += errorOut.length;
			return errorOut;
		}
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
		// FileHander fileHander = new FileHander(tabbedPane, statusObject);
		// fileHander.openFileWithFilePath(file.getPath(), FileAction.DEFAULT_FILE_ENCODE);
		// JOptionPane.showMessageDialog(null, "ok");
	}

	/**
	 * send file to targetIp
	 * 
	 * @param file
	 * @param targetIp
	 * @return
	 * @throws Exception
	 */
	private boolean sendFile(File file, String targetIp) throws Exception {
		SessionClient sessionClient = new SessionClient("sepp", targetIp, 9000);

		FileInputStream fileInputStream = new FileInputStream(file);
		int len = fileInputStream.available();
		byte[] data = new byte[4 + 8 + 1 + file.getName().getBytes("GBK").length + len];
		byte[] fileBytes = new byte[len];

		fileInputStream.read(fileBytes);

		String length = Integer.toHexString(len + 8);
		length = WDStringUtil.paddingHeadZero(length, 8);

		byte[] lenBytes = WDByteUtil.HEX2Bytes(length);

		int pos = 0;
		System.arraycopy(lenBytes, 0, data, 0, lenBytes.length);
		pos += lenBytes.length;
		System.arraycopy(WDByteUtil.HEX2Bytes("0F00000010000000"), 0, data, pos, 8);
		int fileNameLen = file.getName().getBytes("GBK").length;
		pos += 8;
		data[pos] = (byte) fileNameLen;
		pos++;
		System.arraycopy(file.getName().getBytes("GBK"), 0, data, pos, fileNameLen);
		pos += fileNameLen;
		System.arraycopy(fileBytes, 0, data, pos, len);

		fileInputStream.close();

		sessionClient.send(data, "sepp");
		String res = sessionClient.recive("sepp");
		System.out.println(res);
		return false;
	}

	private boolean sendFile(String filePath, String targetIp) throws Exception {
		return sendFile(new File(filePath), targetIp);
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

	@Override
	public ArrayList<String> scanFriend() {
		Sepp sepp = new SeppImpl();
		// SessionClient sessionClient=new SessionClient(connectorName, ip, port)
		// TODO Auto-generated method stub
		ArrayList<String> friendList = new ArrayList<String>();
		
		Socket socket = null;
		for (int i = 67; i < 254; i++) {
			try {
			    socket = new Socket();
			    socket.setSoTimeout(200);
			
				socket.connect(new InetSocketAddress("10.0.97."+i, 9000));
				if (socket.isConnected()) {
					System.out.println(socket.getRemoteSocketAddress());
				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				
				continue;
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return friendList;
	}

	public static void main(String[] args) throws Exception {
		//new SeppImpl().scanFriend();
		WindowsExcuter.excute(new File("."), "cmd.exe /c telnet 10.0.97.68 9000");
	}
}
