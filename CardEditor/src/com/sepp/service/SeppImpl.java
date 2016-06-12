package com.sepp.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTabbedPane;

import org.apache.commons.io.FileUtils;

import com.echeloneditor.actions.FileHander;
import com.echeloneditor.os.OsConstants;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.vo.Cmd;
import com.echeloneditor.vo.StatusObject;
import com.sessionsocket.client.SessionClient;
import com.socket.concurrentsocketserver.PooledRemoteFileServer;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class SeppImpl extends Thread implements Sepp {
	JTabbedPane tabbedPane;
	StatusObject statusObject;

	public SeppImpl(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
	}

	public String process(byte[] data) {
		String resp = "successed_done.";
		try {
			byte[] commandHeader = new byte[Sepp.COMMAND_LEN];
			System.arraycopy(data, 0, commandHeader, 0, Sepp.COMMAND_LEN);

			Cmd cmd = parse(commandHeader);
			if (cmd != null) {
				if (cmd.getCla() != 0x0F) {
					// 发送错误指令给对方
					return "INS_NOT_SUPPORT_EXCEPTION";
				}
				switch (cmd.getIns()) {
				case Sepp.INS_TRANSFER:
					receiveFile(new File(""), data, Sepp.FILE_NAME_LEN_OFFSET);
					break;
				case Sepp.INS_TRANSFER_OPEN:
					receiveFileAndOpenIt(new File(""), data, Sepp.FILE_NAME_LEN_OFFSET);
					break;
				case Sepp.INS_CLOSE:
					// resp = getTermUserName();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			resp = e.getMessage();
		}
		return resp;
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
		return cmd;
	}

	// 在编辑区打开文件
	private void openFileInEditor(File file) throws IOException {
		String fileName = file.getName();
		Collection<String> fileTypeList = Config.getItems("FILE_TYPE");
		String fileExt = fileName.substring(fileName.indexOf(".") + 1);
		if (fileTypeList.contains(fileExt)) {
			openFile(file);
		} else {
			openDir(file.getParent());
		}
	}

	private void openFile(final File file) throws IOException {
		// 在编辑区打开文件开启独立线程
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new FileHander(tabbedPane, statusObject).openFileWithFilePath(file.getPath(),
						OsConstants.DEFAULT_FILE_ENCODE);
			}
		});
		thread.start();
	}

	public void openDir(String target) {
		try {
			WindowsExcuter.excute(new File("."), "start " + target + "\\", false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * send file to targetIp
	 * 
	 * @param file
	 * @param targetIp
	 * @return
	 * @throws Exception
	 */
	public String sendFile(byte ins,File file, String tip) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);
		int len = fileInputStream.available();
		int dataLen=Sepp.COMMAND_LEN + Sepp.FILE_NAME_LEN + file.getName().getBytes(OsConstants.DEFAULT_FILE_ENCODE).length + len;
		byte[] data = new byte[Sepp.HEADER_LEN+dataLen];
		byte[] fileBytes = new byte[len];

		fileInputStream.read(fileBytes);
		int pos = 0;
		byte[] headerLenBytes=WDByteUtil.HEX2Bytes(WDStringUtil.paddingHeadZero(Integer.toHexString(dataLen), Sepp.HEADER_LEN*2));
		System.arraycopy(headerLenBytes, 0, data, 0, Sepp.HEADER_LEN);
		pos+=Sepp.HEADER_LEN;
		byte[] cmdBytes=WDByteUtil.HEX2Bytes("0F000000");
		cmdBytes[1]=ins;
		System.arraycopy(cmdBytes, 0, data, pos, 4);
		int fileNameLen = file.getName().getBytes(OsConstants.DEFAULT_FILE_ENCODE).length;
		pos += Sepp.COMMAND_LEN;
		data[pos] = (byte) fileNameLen;
		pos++;
		System.arraycopy(file.getName().getBytes(OsConstants.DEFAULT_FILE_ENCODE), 0, data, pos, fileNameLen);
		pos += fileNameLen;
		System.arraycopy(fileBytes, 0, data, pos, len);
		fileInputStream.close();

		return send(data, tip, 9991);
	}

	@Override
	public String getTermUserName() {
		return System.getProperty("user.name");
	}

	@Override
	public ArrayList<String> scanFriend() {
		return null;
	}

	public static void main(String[] args) throws Exception {
		// new SeppImpl().scanFriend();
		WindowsExcuter.excute(new File("."), "telnet 10.0.97.68 9000", true);
	}

	@Override
	public void run() {
		PooledRemoteFileServer server = new PooledRemoteFileServer(Integer.parseInt(Config.getValue("CONFIG", "seppPort")), 10);
		server.setUpHandlers();
		server.acceptConnections();
	}

	public String send(byte[] msg, String ip, int port) {
		SessionClient sessionClient=null;
		try {
			sessionClient = new SessionClient("shuishuo9000", ip, port);
			sessionClient.send(msg, "shuishuo9000");
			return sessionClient.recive("shuishuo9000");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public File receiveFile(File file, byte[] buf, short offset) throws Exception {
		short fileNameLen = (short) buf[offset];
		byte[] fileNameBytes = new byte[fileNameLen];
		offset += 1;
		System.arraycopy(buf, offset, fileNameBytes, 0, fileNameLen);
		offset += fileNameLen;
		String fileName = new String(fileNameBytes, OsConstants.DEFAULT_FILE_ENCODE);
		file = new File(OsConstants.DEFAULT_USER_DIR + "/" + Config.getValue("CONFIG", "debugPath") + "/" + fileName);
		if (!file.getParentFile().exists()) {
			file.mkdir();
		}
		if (file.exists()) {
			FileUtils.deleteQuietly(file);
		} else {
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		bos.write(buf, offset, buf.length - offset);
		bos.flush();
		fos.close();
		bos.close();

		return file;
	}

	@Override
	public void receiveFileAndOpenIt(File file, byte[] buf, short offset) throws Exception {
		openFileInEditor(receiveFile(file, buf, offset));
	}

	@Override
	public boolean closeFile(String fileName, String ip) {
		// TODO Auto-generated method stub
		return false;
	}
}
