package com.gerenhua.tool.utils;

import java.util.List;

import com.gerenhua.tool.configdao.AIDInfo;
import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.panel.AtmPanel.TerminalSupportType;
import com.watchdata.commons.lang.WDStringUtil;

/**
 * 
 * @author peng.wang
 * 
 *         该类提供判断终端是否支持某一项终端性能
 * 
 */
public class Terminal {
	public static Log log = new Log();

	public static String getTerminal_perform() {
		return Config.getValue("Terminal_Data", "9F33");
	}

	/**
	 * 根据传入的参数，判断终端是否支持相应的终端性能
	 * 
	 * @param supportType
	 *            需要判断的功能
	 * @return
	 */
	public static boolean isSupportTheFunction(TerminalSupportType supportType) {
		// 获取终端性能参数
		String termPerform = Terminal.getTerminal_perform();
		termPerform = Integer.toBinaryString(Integer.parseInt(termPerform, 16));
		termPerform = WDStringUtil.paddingHeadZero(termPerform, 24);
		switch (supportType) {
		case TOUCHIC:
			return isSupport(termPerform.substring(5, 6));
		case TRACK:
			return isSupport(termPerform.substring(6, 7));
		case KEYBOARD:
			return isSupport(termPerform.substring(7, 8));
		case CERTIFICATECHECK:
			return isSupport(termPerform.substring(8, 9));
		case NOCVM:
			return isSupport(termPerform.substring(11, 12));
		case SIGN:
			return isSupport(termPerform.substring(13, 14));
		case LINKPIN:
			return isSupport(termPerform.substring(14, 15));
		case ICPINCHECK:
			return isSupport(termPerform.substring(15, 16));
		case SUPPORTCDA:
			return isSupport(termPerform.substring(19, 20));
		case EATCARD:
			return isSupport(termPerform.substring(21, 22));
		case SUPPORTDDA:
			return isSupport(termPerform.substring(22, 23));
		case SUPPORTSDA:
			return isSupport(termPerform.substring(23, 24));
		default:
			return false;
		}
	}

	public static void parse8E(String str8E) {
		String x = str8E.substring(0, 8);
		String y = str8E.substring(8, 16);
		String cvmCode = "";
		String cvmCondtionCode = "";

		x = x + "------金额X（二进制）";
		y = y + "------金额Y（二进制）";
		log.warn(x);
		log.warn(y);
		log.warn("---------------------------------------\n");
		int i = 16;
		while (i < str8E.length()) {
			cvmCode = str8E.substring(i, i + 2);
			cvmCondtionCode = str8E.substring(i + 2, i + 4);

			String binary = Integer.toBinaryString(Integer.parseInt(cvmCode, 16));
			binary = WDStringUtil.paddingHeadZero(binary, 8);

			cvmCode = cvmCode + "------" + Config.getValue("CVM_CODE", binary.substring(0, 2)) + ";" + Config.getValue("CVM_TYPE", binary.substring(2, 8));
			cvmCondtionCode = cvmCondtionCode + "------" + Config.getValue("CVM_Condition_Code", cvmCondtionCode);
			i += 4;
			log.warn(cvmCode);
			log.warn(cvmCondtionCode);
			log.warn("---------------------------------------");
		}
	}

	/**
	 * 判断具体某一项终端性能是否支持
	 * 
	 * @param supportData
	 *            终端性能的支持类别，为0或1
	 * @return
	 */
	private static boolean isSupport(String supportData) {
		if ("1".equals(supportData)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判读终端是否支持某一个AID
	 * 
	 * @param aid
	 * @return
	 */
	public static boolean support(String aid) {
		// 获取终端支持的AID列表
		List<AIDInfo> aidlist = new AIDInfo().getAidInfos("SupAID");
		// 循环aid列表存进hashset
		for (AIDInfo aidInfo : aidlist) {
			if (aidInfo.getAid().equals(aid)) {
				return true;
			}
		}
		return false;
	}
}
