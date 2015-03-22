package com.sepp.server;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.main.CardEditor;

public class PooledConnectionHandler implements Runnable {
	private static Logger log = Logger.getLogger(PooledConnectionHandler.class);
	protected File file;
	public static List<File> pool = new LinkedList<File>();

	public PooledConnectionHandler() {
	}

	public void handleConnection() {
		log.debug("get file from pool success.");
		log.debug(Thread.currentThread().getName() + " processing...");
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileHander fileHander=new FileHander(CardEditor.tabbedPane, CardEditor.statusObject);
				fileHander.openFileWithFilePath(file.getPath(), FileAction.DEFAULT_FILE_ENCODE);
			}
		});
		log.debug(Thread.currentThread().getName() + " processing...ok");
	}

	public static void processRequest(File requestToHandle) {
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
				file = (File) pool.remove(0);
			}
			handleConnection();
		}
	}
}
