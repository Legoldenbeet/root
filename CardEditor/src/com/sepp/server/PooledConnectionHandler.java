package com.sepp.server;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.main.CardEditor;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.FileUtil;
import com.echeloneditor.utils.WindowsExcuter;
import com.watchdata.commons.lang.WDAssert;

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
				String itemName=Config.getValue("FILE_TYPE", FileUtil.getFileNameExtNoDot(file));
				if (WDAssert.isEmpty(itemName)) {
					List<String> cmdList = new ArrayList<String>();
					cmdList.add("cmd.exe");
					cmdList.add("/c");
					cmdList.add("start");
					cmdList.add(file.getParent());

					try {
						WindowsExcuter.excute(new File(FileAction.USER_DIR), cmdList,true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					FileHander fileHander=new FileHander(CardEditor.tabbedPane, CardEditor.statusObject);
					fileHander.openFileWithFilePath(file.getPath(), FileAction.DEFAULT_FILE_ENCODE);
				}
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
