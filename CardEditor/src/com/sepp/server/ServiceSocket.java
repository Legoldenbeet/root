package com.sepp.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.sepp.AbstractSessionSocket;
import com.sepp.interfaces.Sepp;
import com.sepp.service.SeppImpl;
import com.watchdata.commons.lang.WDByteUtil;

public class ServiceSocket extends AbstractSessionSocket {
	private static Logger log = Logger.getLogger(ServiceSocket.class);
	private Sepp sepp;

	public ServiceSocket(Socket socket) {
		super(socket);
		sepp = new SeppImpl();
	}

	@Override
	public void beforeConnected(Socket socket) {
		log.debug("=============================================");
		log.debug("默认的最大线程数是：" + getMAX_THREAD());
		if (ServerListener.max_thread > 0)
			setMAX_THREAD(ServerListener.max_thread);
		setBUFFER_SIZE(10);// 10*1024
		log.debug("当前最大线程数是：" + getMAX_THREAD());

	}

	@Override
	public void beforeThreadStarted(Thread thread, Socket socket) {
		log.debug("信息:线程启动之前。线程ID：" + thread.getId());
	}

	@Override
	public void onClose(Socket socket, Thread thread) {
		log.debug("注意:连接断开。socketID:" + socket.hashCode() + "[" + socket.toString() + "]");
	}

	@Override
	public void onConnected(Socket socket, Thread thread) {
		log.debug("信息:连接成功。socketID:" + socket.hashCode() + "[" + socket.toString() + "]");
	}

	@Override
	public void onDataArrived(byte[] data, Socket socket, Thread thread) {
		if (data == null) {
			return;
		}
		int resp=sepp.process(data);
		try {
			sendMessage(WDByteUtil.HEX2Bytes(Integer.toHexString(resp)+"\n"), socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("注意:有消息到达:socketID:" + socket.hashCode() + "[" + socket.toString() + "]【接收：" + data.length + "字节数据】");
	}

	@Override
	public void onError(Exception e, Socket socket, Thread thread) {
		log.error("注意:连接异常[" + e.getMessage() + "|" + e.getStackTrace()[0] + "]socketID:" + socket.hashCode() + "[" + socket.toString() + "]");
	}

	@Override
	public void onMaxThread(Socket socket) {
		try {
			sendMessage("Max connections reached!\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("注意:已经达到最大线程值。当前被拒绝的连接socketID：：" + socket.hashCode() + "[" + socket.toString() + "]");
	}

	@Override
	public void onThreadExit(Thread thread, Socket socket) {
		log.debug("信息:线程退出。线程ID：" + thread.getId());
	}

	@Override
	public void onThreadStarted(Thread thread, Socket socket) {
		log.debug("信息:线程启动。线程ID：" + thread.getId());
	}

	public void sendMessageToAll(byte[] message) {
		ArrayList<AbstractSessionSocket> sessions = getSessions();
		for (int i = 0; i < sessions.size(); i++) {
			ServiceSocket session = (ServiceSocket) sessions.get(i);
			try {
				sendMessage(message, session.getSocket());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] reciveMessage(Socket socket, Thread thread) throws IOException {
		// 获得输入缓冲流
		BufferedInputStream reciver = new BufferedInputStream(socket.getInputStream());
		// 创建缓存文件
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// 读取数据
		byte[] msgHeader = new byte[4];// 缓存大小
		byte[] buffer = new byte[getBUFFER_SIZE() * 1024];// 缓存大小

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
}
