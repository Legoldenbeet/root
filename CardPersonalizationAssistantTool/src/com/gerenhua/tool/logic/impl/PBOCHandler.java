package com.gerenhua.tool.logic.impl;

import java.util.Date;
import java.util.HashMap;

import javax.swing.JTextPane;

import org.apache.log4j.NDC;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.apdu.PbocProcess;
import com.gerenhua.tool.utils.PropertiesManager;
import com.gerenhua.tool.utils.reportutil.GenReportUtil;
import com.watchdata.commons.lang.WDStringUtil;

public class PBOCHandler extends BaseHandler {
	private static Log logger = new Log();
	private GenReportUtil genWordUtil = null;
	private PropertiesManager pm = new PropertiesManager();

	public PBOCHandler(JTextPane textPane) {
		logger.setLogArea(textPane);
	}

	/**
	 * pboc交易处理流程 LIYAXIAO 2015-6-20,上午10:28:55
	 * 
	 * @param tradeMount
	 * @param readerName
	 * @return
	 */
	public boolean doTrade(int tradeMount, String readerName) {
		// 初始化交易参数，如授权金额，pin等
		HashMap<String, String> param = new HashMap<String, String>();
		String termRandom = WDStringUtil.getRandomHexString(8);
		param.put("9F02", WDStringUtil.paddingHeadZero(String.valueOf(tradeMount), 12));
		// param.put("9C","40");
		param.put("9F7A", "00");
		param.put("9F37", termRandom);
		Date dateTime = new Date();
		param.put("9A", getFormatDate(dateTime, Constants.FORMAT_SHORT_DATE));
		param.put("9F21", getFormatDate(dateTime, Constants.FORMAT_TIME));
		NDC.push("[PBOC]");
		logger.debug("PBOC trade start...", 0);
		genWordUtil = new GenReportUtil();

		genWordUtil.open(pm.getString("mv.tradepanel.lend"));
		genWordUtil.addFileTitle("PBOC交易检测报告");
		genWordUtil.addTransactionName("PBOC");

		try {
			// 为了保证卡片和读卡器的正确性，交易开始前务必先复位
			logger.debug("=============================Initialization=================================");
			HashMap<String, String> result;
			PbocProcess.initialization(apduHandler, logger, genWordUtil);
			logger.debug("============================Application Selection=================================");
			String aid = PbocProcess.applicationSelection(apduHandler, logger, genWordUtil);
			logger.debug("===============================Final Selection=================================");
			result = PbocProcess.finalSelection(aid, apduHandler, logger, genWordUtil);
			String pdol = result.get("9F38");
			genWordUtil.add("PDOL Data:" + pdol);
			// gpo
			logger.debug("==================================Initiate Application(GPO)==================================");
			result = PbocProcess.initiateApplication((BaseHandler) this, pdol, param, apduHandler, logger, genWordUtil);
			String aip = result.get("82");
			// 字节1：
			// 位8：1=RFU
			// 位7：1=支持SDA
			// 位6：1=支持DDA
			// 位5：1=支持持卡人认证
			// 位4：1=执行终端风险管理
			// 位3：1=支持发卡行认证
			// 位2：RFU（0）
			// 位1：1=支持CDA
			// 字节2：RFU（“00”）
			genWordUtil.add("AIP:" + aip);
			// read record
			logger.debug("=================================Read Application Data===========================");
			HashMap<String, String> cardRecordData = PbocProcess.readApplicationData((BaseHandler) this, result, genWordUtil);
			String staticDataList = cardRecordData.get("staticDataList");
			genWordUtil.add("StaticDataList:" + cardRecordData);
			// Internal Authenticate
			logger.debug("=======================Internal Authenticate==============================");
			result = PbocProcess.internalAuthenticate(aip, termRandom, apduHandler, logger, genWordUtil);
			String signedDynmicData = result.get("80");
			logger.debug("===============================Data Authentication============================");
			String pan = cardRecordData.get("5A");
			pan = pan.replaceAll("F", "");
			String panSerial = cardRecordData.get("5F34");
			if (PbocProcess.dataAuthentication(cardRecordData, pan, aid, aip, staticDataList, signedDynmicData, termRandom, logger, genWordUtil)) {
				logger.debug("Offline Data Authentication was performed!\n");
			} else {
				logger.error("Offline Data Authentication Fail!\n");
			}
			// get data
			logger.debug("================================GET DATA=============================");
			HashMap<String, String> dataMap = new HashMap<String, String>();
			dataMap = PbocProcess.getData(cardRecordData, apduHandler);
			logger.debug("================================Processing Restrictions=============================");
			if (PbocProcess.processingRestrictions(cardRecordData,param,logger)) {
				logger.debug("Processing Restrictions OK.");
			} else {
				logger.error("Processing Restrictions Fail!");
			}
			logger.debug("================================Cardholder Verification=============================");
			if (PbocProcess.cardholderVerification(cardRecordData, apduHandler, logger, genWordUtil)) {
				logger.debug("Cardholder Verification OK.");
			} else {
				logger.error("Cardholder Verification Fail!");
			}
			logger.debug("================================Terminal Risk Management=============================");
//			<<终端风险管理>>
//			 终端异常文件检查
//			 商户强制联机
//			 最低限额检查
//			 交易日志
//			 随机选择
//			 频度检查
//			 新卡检查
			logger.debug("================================Terminal Action Analysis=============================");
//			IAC可选（EMV）IAC需要（JR/T 0025借记/贷记）
			// Generate arqc
			logger.debug("==========================Card Action Analysis(Generate AC1)================================");
			result = PbocProcess.cardActionAnalysis((BaseHandler) this, cardRecordData, param, apduHandler, genWordUtil);
			String arqc = result.get("9F26");
			String atc = result.get("9F36");
			String iad = result.get("9F10");
			String CVR = iad.substring(6, 14);
			// 字节1： 长度字节 03
			// 字节2：
			// 位8–7：
			// 00=第2 个GENERATE AC 返回AAC
			// 01=第2 个GENERATE AC 返回TC
			// 10=不请求第2 个GENERATE AC
			// 11=RFU
			// 位6–5：
			// 00=第1 个GENERATE AC 返回AAC
			// 01=第1 个GENERATE AC 返回TC
			// 10=第1 个GENERATE AC 返回ARQC
			// 11=不能返回11
			// 位4：1=发卡行认证执行但失败
			// 位3：1 =脱机PIN 执行
			// 位2：1=脱机PIN 认证失败
			// 位1：1 =不能联机
			// 字节3：
			// 位8：1=上次联机交易没有完成
			// 位7：1=PIN 锁定
			// 位6：1=超过频率检查
			// 位5：1=新卡
			// 位4：1=上次联机交易发卡行认证失败
			// 位3：1=联机授权后，发卡行认证没有执行
			// 位2：1=由于PIN 锁卡片锁定应用
			// 位1：1=上次交易SDA 失败交易拒绝
			// 字节4：
			// 位8–5：上次交易第2 个生成应用密文（GENERATE
			// AC）命令后收到的带有安全报文的发卡行脚本命
			// 令
			// 位4：1 =上次交易发卡行脚本处理失败指针
			// 位3：1=上次交易DDA 失败交易拒绝
			logger.debug("=================================Online processing=================================");
			String arpc = PbocProcess.onlineprocessing((BaseHandler) this, pan, panSerial, aip, atc, iad, arqc, param, logger, genWordUtil);
			// 请求发卡行认证AC密文
			logger.debug("=======================Issuer Authentication============================");
			result = PbocProcess.issuerAuthentication(apduHandler, arpc, logger, genWordUtil);
			// Generate tc
			logger.debug("===========================Completion(Generate AC2)===========================");
			result = PbocProcess.completion((BaseHandler) this, cardRecordData, param, apduHandler, genWordUtil);
			logger.debug("========================PBOC trade Approved!=======================");
			genWordUtil.add("PBOC交易完成!");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			genWordUtil.add(e.getMessage());
			return false;
		} finally {
			genWordUtil.close();
			NDC.pop();
			NDC.remove();
			// apduHandler.close();
		}

	}
}
