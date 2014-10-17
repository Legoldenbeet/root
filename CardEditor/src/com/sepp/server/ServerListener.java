package com.sepp.server;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.echeloneditor.vo.StatusObject;

public class ServerListener {
	private static Logger log = Logger.getLogger(ServerListener.class);
	private static boolean IS_STOP = false;
	private ServerSocket listener;
	public static int max_thread = 0;
	private JTabbedPane tabbedPane;
	private StatusObject statusObject;

	public static boolean isIS_STOP() {
		return IS_STOP;
	}

	public static void main(String args[]) {
		new ServerListener().startService(9000, null, null);
	}

	public void startService(int port, JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		try {
			IS_STOP = false;
			listener = new ServerSocket(port);
			log.info("Service started on port " + port + "...");
			while (!IS_STOP && !listener.isClosed()) {
				new ServiceSocket(listener.accept(), tabbedPane, statusObject).start();
			}
			log.error("Service stopped.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
