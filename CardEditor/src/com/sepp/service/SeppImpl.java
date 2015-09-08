package com.sepp.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.Debug;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.vo.Cmd;
import com.echeloneditor.vo.StatusObject;
import com.watchdata.commons.lang.WDByteUtil;

public class SeppImpl implements Sepp {
	JTabbedPane tabbedPane;
	StatusObject statusObject;

	public SeppImpl(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
	}

	@Override
	public String process(byte[] data) {
		String resp = "success_done.";
		try {
			byte[] cmdHeader = new byte[Sepp.CMD_LEN];
			System.arraycopy(data, 0, cmdHeader, 0, Sepp.CMD_LEN);

			Cmd cmd = parse(cmdHeader);
			if (cmd != null) {
				if (cmd.getCla() != 0x0F) {
					// 发送错误指令给对方

					return "EXCEPTION_INS_NOT_SUPPORT";
				}
				switch (cmd.getIns()) {
				case Sepp.INS_FILE_OPEN:
					receiveOpen(data, Sepp.FILE_NAME_LEN_OFFSET);
					break;
				case Sepp.INS_FILE_CLOSE:
					closeFile();
					break;
				case Sepp.INS_TERM_INFO_NAME:
					resp = getTermUserName();
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

	@Override
	public void receiveOpen(byte[] data, short offset) throws Exception {
		short fileNameLen = (short) data[offset];
		byte[] fileNameBytes = new byte[fileNameLen];
		offset += 1;
		System.arraycopy(data, offset, fileNameBytes, 0, fileNameLen);
		offset += fileNameLen;
		String fileName = new String(fileNameBytes, "GBK");
		File file = new File(FileAction.USER_DIR + "/" + Config.getValue("CONFIG", "debugPath") + "/" + fileName);
		if (!file.getParentFile().exists()) {
			file.mkdir();
		}
		if (file.exists()) {
			FileUtils.deleteQuietly(file);
		} else {
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bw = new BufferedOutputStream(fos);

		bw.write(data, offset, data.length - offset);
		bw.flush();
		fos.close();
		bw.close();
		
		Collection<String> fileTypeList=Config.getItems("FILE_TYPE");
		String fileExt=fileName.substring(fileName.indexOf(".")+1);
		if (fileTypeList.contains(fileExt)) {
			openFile(file);
		}else {
			openDir(file.getParent());
		}
	}

	private void openFile(final File file) throws IOException{
		//在编辑区打开文件开启独立线程
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new FileHander(tabbedPane, statusObject).openFileWithFilePath(file.getPath(), FileAction.DEFAULT_FILE_ENCODE);
			}
		});
	}
	
	public void openDir(String target) {
		try {
			WindowsExcuter.excute(new File("."), "cmd.exe /c start " + target+"\\",false);
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
	public boolean sendFile(File file, String targetIp) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);
		int len = fileInputStream.available();
		byte[] data = new byte[Sepp.CMD_LEN + Sepp.FILE_NAME_LEN + file.getName().getBytes("GBK").length + len];
		byte[] fileBytes = new byte[len];

		fileInputStream.read(fileBytes);
		int pos = 0;
		System.arraycopy(WDByteUtil.HEX2Bytes("0F000000"), 0, data, pos, 4);
		int fileNameLen = file.getName().getBytes("GBK").length;
		pos += 4;
		data[pos] = (byte) fileNameLen;
		pos++;
		System.arraycopy(file.getName().getBytes("GBK"), 0, data, pos, fileNameLen);
		pos += fileNameLen;
		System.arraycopy(fileBytes, 0, data, pos, len);
		fileInputStream.close();

		send(data, targetIp, 9991);
		return true;
	}

	public boolean sendFile(String filePath, String targetIp) throws Exception {
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
		return null;
	}

	public static void main(String[] args) throws Exception {
		// new SeppImpl().scanFriend();
		WindowsExcuter.excute(new File("."), "cmd.exe /c telnet 10.0.97.68 9000", true);
	}

	@Override
	public void open(int seppPort) {
		try {
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
			Executor threadPool = Executors.newCachedThreadPool();// 建立线程池
			acceptor.getFilterChain().addLast("exector", new ExecutorFilter(threadPool));
			acceptor.getSessionConfig().setReuseAddress(true);
			acceptor.setHandler(new SeppIOHander());
			acceptor.bind(new InetSocketAddress(seppPort));
			Debug.log.debug("Service started on port " + seppPort + "...");
		} catch (Exception e) {
			Debug.log.debug(e.getMessage());
		}
	}

	@Override
	public String send(byte[] msg, String ip, int port) {
		String recv = "";
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.getSessionConfig().setUseReadOperation(true);
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		IoSession session = connector.connect(new InetSocketAddress(ip, port)).awaitUninterruptibly().getSession();
		try {
			session.write(msg).awaitUninterruptibly(3, TimeUnit.SECONDS);
			Debug.log.info("Send：" + msg);
			ReadFuture readFuture = session.read();
			if (readFuture.awaitUninterruptibly(3, TimeUnit.SECONDS)) {
				recv = readFuture.getMessage().toString();
				Debug.log.info("Recv：" + recv);
			}
		} finally {
			session.close(true);
			session.getService().dispose();
			connector.dispose();
		}
		return recv;

	}

	private class SeppIOHander extends IoHandlerAdapter {
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			Debug.log.info("exceptionCaught=" + cause.toString());
			cause.printStackTrace();
		}

		public void messageSent(IoSession session, Object message) throws Exception {
			Debug.log.info("messageSent=" + session.getRemoteAddress().toString());
		}

		public void sessionClosed(IoSession session) throws Exception {
			session.close(true);
			Debug.log.info("sessionClosed");
		}

		public void sessionCreated(IoSession session) throws Exception {
			SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
			cfg.setSoLinger(0);
			Debug.log.info("sessionCreated=" + session.getRemoteAddress().toString());
		}

		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			Debug.log.info("sessionIdle=" + status.toString() + "=" + session.getRemoteAddress().toString());
		}

		public void sessionOpened(IoSession session) throws Exception {
			Debug.log.info("sessionOpened=" + session.getRemoteAddress().toString());
		}

		public void messageReceived(IoSession session, Object msg) throws Exception {
			try {
				Debug.log.info("接收到的报文数据：" + msg.toString());
				String recv = process((byte[]) msg);
				session.write(recv);
				Debug.log.info("发送出的报文数据：" + recv);
			} catch (Exception e) {
				Debug.log.debug(e.getMessage());
			}
		}
	}
}
