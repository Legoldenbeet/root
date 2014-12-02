package com.gerenhua.tool.logic.impl;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDStringUtil;

public class InstallAppletThread extends Thread {
	public JTree tree;
	public CommonAPDU commonAPDU;
	public static String aid = "";
	public static String privilege = "";
	public static String param = "";
	public static Log logger = new Log();

	public InstallAppletThread(JTree tree, CommonAPDU commonAPDU) {
		this.tree = tree;
		this.commonAPDU = commonAPDU;
	}

	@Override
	public void run() {
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		String moduleName = (selNode != null) ? selNode.toString() : null;
		if (WDAssert.isNotEmpty(moduleName)) {

			if (selNode.getParent() == null) {
				return;
			}
			String loadFileName = selNode.getParent().toString();
			loadFileName = loadFileName.substring(0, loadFileName.indexOf(";"));
			//String aid = "A000000333010101";
			//String privilege = "04";
			//String param = "";

			String p1 = "0C";
			String p2 = "00";

			String loadFileNameLen = WDStringUtil.paddingHeadZero(Integer.toHexString(loadFileName.length() / 2), 2);
			String moduleNameLen = WDStringUtil.paddingHeadZero(Integer.toHexString(moduleName.length() / 2), 2);
			String aidLen = WDStringUtil.paddingHeadZero(Integer.toHexString(aid.length() / 2), 2);
			String privilegeLen = WDStringUtil.paddingHeadZero(Integer.toHexString(privilege.length() / 2), 2);
			String paramLen = WDStringUtil.paddingHeadZero(Integer.toHexString(param.length() / 2), 2);

			String c9 = "C9" + paramLen + param;
			String c9Len = WDStringUtil.paddingHeadZero(Integer.toHexString(c9.length() / 2), 2);

			int lcInt = Integer.parseInt(loadFileNameLen, 16) + Integer.parseInt(moduleNameLen, 16) + Integer.parseInt(aidLen, 16);
			lcInt += Integer.parseInt(privilegeLen, 16) + Integer.parseInt(c9Len, 16) + 6;

			String lc = WDStringUtil.paddingHeadZero(Integer.toHexString(lcInt), 2);
			String cmd = "80E6" + p1 + p2 + lc;
			cmd += loadFileNameLen + loadFileName + moduleNameLen + moduleName + aidLen + aid;
			cmd += privilegeLen + privilege;
			cmd += c9Len + c9;
			cmd += "00";
			try {
				commonAPDU.send(cmd);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
