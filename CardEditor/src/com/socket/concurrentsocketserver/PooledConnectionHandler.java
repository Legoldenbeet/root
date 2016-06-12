package com.socket.concurrentsocketserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.echeloneditor.main.CardEditor;
import com.echeloneditor.os.OsConstants;
import com.echeloneditor.utils.Debug;
import com.sepp.service.Sepp;
import com.sepp.service.SeppImpl;
import com.watchdata.commons.lang.WDByteUtil;

public class PooledConnectionHandler implements Runnable {
	private static Logger log = Logger.getLogger(PooledConnectionHandler.class);
	protected Socket socket;
	protected static List<Socket> pool = new LinkedList<Socket>();

	public PooledConnectionHandler() {
	}

	public void handleConnection() {
		log.debug("get socket from pool success:::process adress "+socket.getLocalAddress()+"on port:"+socket.getLocalPort()+"/"+socket.getRemoteSocketAddress());
		if(!socket.isClosed()) {
			try {
				byte[] data = reciveMessage(socket);
				SeppImpl sepp=new SeppImpl(CardEditor.tabbedPane, CardEditor.statusObject);
				String cmd=sepp.process(data);
				sendMessage((cmd+"\n").getBytes(OsConstants.DEFAULT_FILE_ENCODE));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			System.out.println(":::::连接异常关闭:::::");
		}
		
	}

	/**
	 * @Description 使用成员socket发送信息
	 * @param data
	 *            : byte[] 要发送的信息
	 * @throws IOException
	 *             抛出IO异常,说明网络异常
	 * @return 返回类型 void
	 */
	public void sendMessage(byte[] data) throws IOException {
		OutputStream sender = socket.getOutputStream();
		sender.write(data);
		sender.flush();
	};

	public byte[] reciveMessage(Socket socket) throws Exception {
		BufferedInputStream reciver = null;
		ByteArrayOutputStream out = null;
		// 获得输入缓冲流
		reciver = new BufferedInputStream(socket.getInputStream());
		// 创建缓存文件
		out = new ByteArrayOutputStream();

		// 读取数据
		byte[] msgHeader = new byte[Sepp.HEADER_LEN];// 缓存大小
		byte[] buffer = new byte[Sepp.PACKAGE_SIZE];// 缓存大小

		int amount = reciver.read(msgHeader);
		long headLen = Long.parseLong(WDByteUtil.bytes2HEX(msgHeader), 16);

		long pos = 0;
		while (pos < headLen) {
			amount = reciver.read(buffer);
			out.write(buffer, 0, amount);
			pos += amount;
		}
		out.flush();
		out.close();
		return out.toByteArray();
	}

	public static void processRequest(Socket requestToHandle) {
		synchronized (pool) {
			pool.add(pool.size(), requestToHandle);
			pool.notifyAll();
		}
	}

	public void run() {
		while (true) {
			synchronized (pool) {
				while (pool.isEmpty()) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				socket = (Socket) pool.remove(0);
			}
			Debug.log.info("Thread: "+Thread.currentThread().getName()+" id:"+Thread.currentThread().getId()+"process now.");
			handleConnection();
		}
	}
}
