package com.socket.concurrentsocketserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.watchdata.commons.lang.WDByteUtil;

public class PooledConnectionHandler implements Runnable {
	private static Logger log = Logger.getLogger(PooledConnectionHandler.class);
	protected Socket socket;
	protected static List<Socket> pool = new LinkedList<Socket>();

	public PooledConnectionHandler() {
	}

	public void handleConnection() {
		log.debug("get socket from pool success.");
		log.debug(Thread.currentThread().getName() + " processing...");
		while (true) {
			try {
				byte[] data = reciveMessage(socket);

				if (data != null && data.length > 0) {

					FileOutputStream out = null;

					out = new FileOutputStream("d:/test/hello.rar");
					out.write(data);
					out.flush();

					log.debug("注意:有消息到达:socketID:" + socket.hashCode() + "[" + socket.toString() + "]【接收：" + data.length + "字节数据】");
					sendMessage("success.\n".getBytes());

					if (out != null) {
						out.close();
					}
				}
			} catch (Exception e) {
				try {
					if (socket != null) {
						socket.close();
						break;
					}
				} catch (IOException e1) {
					
				}
			}
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
		byte[] msgHeader = new byte[4];// 缓存大小
		byte[] buffer = new byte[5 * 1024];// 缓存大小

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
			handleConnection();
		}
	}
}
