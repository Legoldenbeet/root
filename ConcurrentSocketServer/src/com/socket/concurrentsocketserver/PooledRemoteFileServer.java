package com.socket.concurrentsocketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class PooledRemoteFileServer {
	private static Logger log = Logger.getLogger(PooledRemoteFileServer.class);
	protected int maxConnections;
	protected int listenPort;
	protected ServerSocket serverSocket;

	public PooledRemoteFileServer(int aListenPort, int maxConnections) {
		listenPort = aListenPort;
		this.maxConnections = maxConnections;
	}

	public void acceptConnections() {
		try {
			ServerSocket server = new ServerSocket(listenPort);
			log.debug("server listen on " + listenPort + "...");
			Socket incomingConnection = null;
			while (true) {
				incomingConnection = server.accept();
				handleConnection(incomingConnection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void handleConnection(Socket connectionToHandle) {
		PooledConnectionHandler.processRequest(connectionToHandle);
	}

	public void setUpHandlers() {
		log.debug("pool init ....");
		for (int i = 0; i < maxConnections; i++) {
			PooledConnectionHandler currentHandler = new PooledConnectionHandler();
			new Thread(currentHandler, "Handler " + i).start();
			log.debug("Handler " + i + "启动...success.");
		}
	}

	public static void main(String args[]) {
		PooledRemoteFileServer server = new PooledRemoteFileServer(9000, 1000);
		server.setUpHandlers();
		server.acceptConnections();
	}
}
