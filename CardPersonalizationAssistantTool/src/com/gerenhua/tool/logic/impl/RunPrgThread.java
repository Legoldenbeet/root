package com.gerenhua.tool.logic.impl;

import java.util.HashMap;
import java.util.Observable;

import javax.swing.JTextPane;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.utils.FileUtil;
import com.watchdata.commons.lang.WDAssert;

public class RunPrgThread extends Observable implements Runnable {
	public JTextPane textPane;
	public CommonAPDU commonAPDU;
	public static Log logger = new Log();
	public static boolean oneStep = false;
	public static int pos=0;
	public static HashMap<String, String> mapBean = new HashMap<String, String>();

	public RunPrgThread(JTextPane textPane, CommonAPDU commonAPDU) {
		this.textPane = textPane;
		this.commonAPDU = commonAPDU;
	}

	@Override
	public void run() {
		try {
			String prg = textPane.getText().trim();
			String[] apdus = prg.split(FileUtil.LINE_SEPARATOR);

			for (int i = 0; i < apdus.length; i++) {
				synchronized (mapBean) {
					if (mapBean.size() > 0) {
						String cmd = mapBean.get("debug");
						if (cmd.equalsIgnoreCase("stop") || oneStep) {
							oneStep = false;
							mapBean.wait();
						} else if (cmd.equalsIgnoreCase("step")) {
							oneStep = true;
						}
					}
				}
				String apdu = apdus[i];
				if (!apdu.startsWith("//") && WDAssert.isNotEmpty(apdu.trim())) {
					apdu = apdu.trim();
					int commentPos = apdu.indexOf("//");
					int swPos = apdu.indexOf("SW");
					int pos = calPos(commentPos, swPos);
					if (pos != -1) {
						apdu = apdu.substring(0, pos).trim();
					}
					commonAPDU.send(apdu);
				}
				String temp=pos+"|";
				//System.out.println("hellohello:"+prg.substring(pos, pos+apdu.length()+2));
				pos+=apdu.length();
				pos+=2;
				temp+=pos;
				this.notifyObservers(temp);
				this.setChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
	}

	public int calPos(int pos1, int pos2) {
		int pos = -1;
		if (pos1 != -1 && pos2 != -1) {
			pos = Math.min(pos1, pos2);
		} else if (pos1 == -1 && pos2 != -1) {
			pos = pos2;
		} else if (pos1 != -1 && pos2 == -1) {
			pos = pos1;
		}
		return pos;
	}
}
