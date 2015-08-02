package com.gerenhua.tool.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;

import com.gerenhua.tool.globalplatform.CapFile;
import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.panel.CardInfoDetectPanel;
import com.gerenhua.tool.utils.Config;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class LoadCapThead extends Thread {
	/**
	 * @return the isJTS
	 */
	public boolean isJTS() {
		return isJTS;
	}

	/**
	 * @param isJTS the isJTS to set
	 */
	public void setJTS(boolean isJTS) {
		this.isJTS = isJTS;
	}
	public static CommonAPDU commonAPDU;
	public static Log log = new Log();
	public JTextPane textPane;
	public File[] capFiles;
	public boolean isRealCard = false;
	public boolean isJTS=false;

	public boolean isRealCard() {
		return isRealCard;
	}

	public void setRealCard(boolean isRealCard) {
		this.isRealCard = isRealCard;
	}

	public LoadCapThead(File[] file, CommonAPDU commonAPDU, JTextPane textPane) {
		this.capFiles = file;
		this.commonAPDU = commonAPDU;
		this.textPane = textPane;
	}

	@Override
	public void run() {
		log.setLogArea(textPane);
		textPane.setText("");
		for (File file : capFiles) {
			try {
				String resp = "";
				CapFile cap = new CapFile(new FileInputStream(file));
				
				boolean includeDebug=Integer.parseInt(Config.getValue("CardInfo", "includeDebug"))==0?false:true;
				boolean separateComponents=Integer.parseInt(Config.getValue("CardInfo", "separateComponents"))==0?false:true;
				int blockSize=Integer.parseInt(Config.getValue("CardInfo", "cap2prg_commandlen"),16);
				List<byte[]> loadFileInfo = cap.getLoadBlocks(includeDebug, separateComponents, blockSize);

				List<String> outList=new ArrayList<String>();
				cap.dump(outList);
				
				for (String out : outList) {
					log.out("//"+out+"\n", Log.LOG_COLOR_BLACK);
				}
				
				String apduCommand = WDStringUtil.paddingHeadZero(Integer.toHexString(cap.getPackageAID().getBytes().length), 2) + WDByteUtil.bytes2HEX(cap.getPackageAID().getBytes());
				apduCommand += "00000000";
				apduCommand = WDStringUtil.paddingHeadZero(Integer.toHexString(apduCommand.length() / 2), 2) + apduCommand;
				apduCommand = "80E60200" + apduCommand.toUpperCase();
				if (isRealCard) {
					resp = commonAPDU.send(apduCommand);
				} else {
					log.out(formatLoadScript(apduCommand, "//INSTALL [for load]",isJTS), Log.LOG_COLOR_BLACK);
					resp = "9000";
				}
				if (resp.endsWith(Constants.SW_SUCCESS)) {
					for (int j = 0; j < loadFileInfo.size(); j++) {
						String p1 = (j == loadFileInfo.size() - 1) ? "80" : "00";

						String p2 = "";
						if (j <=0xFF) {
							p2 = WDStringUtil.paddingHeadZero(Integer.toHexString(j), 2);
						} else {
//							p2 = WDStringUtil.paddingHeadZero(Integer.toHexString(j - 0xFF-1), 2);
							p2 = WDStringUtil.paddingHeadZero(Integer.toHexString(j%0x100), 2);
						}

						String lc = WDStringUtil.paddingHeadZero(Integer.toHexString(loadFileInfo.get(j).length), 2);
						String temp = "80E8" + p1 + p2 + lc;
						temp += WDByteUtil.bytes2HEX(loadFileInfo.get(j));
						temp = temp.toUpperCase();
						if (isRealCard) {
							resp = commonAPDU.send(temp);
						} else {
							if(p1.equalsIgnoreCase("80")){
								log.out(formatLoadScript(temp, "//LOAD [for Last Block]",isJTS), Log.LOG_COLOR_BLACK);
							}else {
								log.out(formatLoadScript(temp, "//LOAD [for " + (j+1) + " Block]",isJTS), Log.LOG_COLOR_BLACK);
							}
							resp = "9000";
						}
						if (!resp.endsWith(Constants.SW_SUCCESS)) {
							break;
						}
					}
					String msg = "load " + file.getName() + " complete.";
					if (isRealCard) {
						log.info(msg);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				Config.setValue("CardInfo", "currentCap", capFiles[0].getParent());
			}
		}
		if (isRealCard) {
			CardInfoDetectPanel.refreshTree();
		}
	}
	/**
	 * formatLoadScript
	 * 
	 * @param apdu
	 * @param desc
	 * @return
	 */
	public String formatLoadScript(String apdu, String desc,boolean isJTS) {
		StringBuilder sb = new StringBuilder();
		sb.append(desc).append("\n");
		if (isJTS) {
			sb.append("    jts.GP_senDisplay('").append(apdu).append("SW9000").append("');").append("\n");
		}else {
			sb.append(apdu).append("SW9000").append("\n");
		}
		return sb.toString();
	}
}
