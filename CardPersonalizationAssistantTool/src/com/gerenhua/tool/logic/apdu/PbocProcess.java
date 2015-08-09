package com.gerenhua.tool.logic.apdu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import com.gerenhua.tool.app.Application;
import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.impl.BaseHandler;
import com.gerenhua.tool.logic.issuer.IIssuerDao;
import com.gerenhua.tool.logic.issuer.local.IssuerDaoImpl;
import com.gerenhua.tool.logic.pki.DataAuthenticate;
import com.gerenhua.tool.panel.ApplicationSelectDialog;
import com.gerenhua.tool.utils.Terminal;
import com.gerenhua.tool.utils.reportutil.APDUSendANDRes;
import com.gerenhua.tool.utils.reportutil.GenReportUtil;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDStringUtil;

public class PbocProcess extends BaseHandler {
	public static HashMap<String, String> result;
	private static IIssuerDao issuerDao = new IssuerDaoImpl();

	/**
	 * 初始化 复位
	 * 
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 */
	public static void initialization(CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		HashMap<String, String> res = apduHandler.reset();
		if (!"9000".equals(res.get("sw"))) {
			logger.error("card reset falied");
			genWordUtil.add("卡片复位失败");
			return;
		}
		logger.debug("ATR:" + res.get("atr"));
		// 复位报告内容
		genWordUtil.add("ATR", "Card Reset", res.get("atr"), new HashMap<String, String>());
	}

	/**
	 * 应用列表选择
	 * 
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static String applicationSelection(String paySysDir, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		HashMap<String, String> result = apduHandler.select(paySysDir);
		if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
			logger.error("select PSE error,card return:" + result.get("sw"));
			genWordUtil.add("选择PSE出错");
			// genWordUtil.close();
			return null;
		}

		// 选择pse报告内容
		genWordUtil.add(result.get("apdu"), "Select " + WDStringUtil.hex2asc(paySysDir), result.get("res"), result);

		if (WDAssert.isEmpty(result.get("88"))) {
			logger.error("88 is null.");
			return null;
		}
		// read dir, begin from 01
		List<HashMap<String, String>> readDirList = apduHandler.readDir(result.get("88"));

		ApplicationSelectDialog applicationSelectDialog = new ApplicationSelectDialog(Application.frame, readDirList);
		applicationSelectDialog.setLocationRelativeTo(Application.frame);
		applicationSelectDialog.setVisible(true);
		// select aid
		return applicationSelectDialog.getSelectedAID();
	}
	/**
	 * qpboc ppse
	 * @param paySysDir
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static String applicationSelection_qpboc(String paySysDir, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		HashMap<String, String> result = apduHandler.select(paySysDir);
		if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
			logger.error("select PPSE error,card return:" + result.get("sw"));
			genWordUtil.add("选择PPSE出错");
			// genWordUtil.close();
			return null;
		}

		// 选择ppse报告内容
		genWordUtil.add(result.get("apdu"), "Select " + WDStringUtil.hex2asc(paySysDir), result.get("res"), result);

		if (WDAssert.isEmpty(result.get("BF0C"))) {
			logger.error("BF0C is null.");
			return null;
		}
		// read dir, begin from 01
		List<HashMap<String, String>> readDirList = apduHandler.readDir(result.get("88"));

		ApplicationSelectDialog applicationSelectDialog = new ApplicationSelectDialog(Application.frame, readDirList);
		applicationSelectDialog.setLocationRelativeTo(Application.frame);
		applicationSelectDialog.setVisible(true);
		// select aid
		return applicationSelectDialog.getSelectedAID();
	}

	/**
	 * 选择应用
	 * 
	 * @param aid
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static HashMap<String, String> finalSelection(String aid, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		if (WDAssert.isEmpty(aid)) {
			logger.error("select aid is null");
			genWordUtil.add("获取AID为空");
			// genWordUtil.close();
			return null;
		}
		if (Terminal.support(aid)) {
			result = apduHandler.select(aid);
		} else {
			logger.error("Terminal can not support the app");
			genWordUtil.add("终端不支持此应用");
			// genWordUtil.close();
			return null;
		}
		if (!Constants.SW_SUCCESS.equals(result.get("sw"))) {
			logger.error("select app get response:" + result.get("sw"));
			genWordUtil.add("选择应用出错");
			// genWordUtil.close();
			return null;
		}
		// 选择aid报告内容
		genWordUtil.add(result.get("apdu"), "Select AID", result.get("res"), result);
		return result;
	}

	/**
	 * 应用初始化GPO
	 * 
	 * @param baseHandler
	 * @param pdol
	 * @param param
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static HashMap<String, String> initiateApplication(BaseHandler baseHandler, String pdol, HashMap<String, String> param, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		String loadDolDataResult = "";
		try {
			loadDolDataResult = baseHandler.loadDolData(pdol, param);
		} catch (Exception e) {
			logger.error(e.getMessage());
			genWordUtil.add("获取DDOL数据出错");
			return null;
		}
		result = apduHandler.gpo("83" + CommonHelper.getLVData(loadDolDataResult, 1));

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
		genWordUtil.add(result.get("apdu"), "GPO", result.get("res"), result);
		genWordUtil.add("LoadDolDataResult:" + loadDolDataResult);
		return result;
	}

	/**
	 * 读取应用数据
	 * 
	 * @param baseHandler
	 * @param gpoResult
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> readApplicationData(BaseHandler baseHandler, HashMap<String, String> gpoResult, GenReportUtil genWordUtil) throws Exception {
		List<APDUSendANDRes> aList = new ArrayList<APDUSendANDRes>();
		result = baseHandler.getCardRecordData(gpoResult.get("94"), aList);
		// 读记录报告
		for (APDUSendANDRes apduSendANDRes2 : aList) {
			genWordUtil.add(apduSendANDRes2);
		}
		return result;
	}

	/**
	 * 内部认证
	 * 
	 * @param aip
	 * @param termRandom
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static HashMap<String, String> internalAuthenticate(String aip, String termRandom, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		if (CommonHelper.support(aip, BaseHandler.AIP_SUPPORT_DDA)) {// 有条件的——如果支持DDA（EMV）
			result = apduHandler.internalAuthenticate(termRandom);
			genWordUtil.add(result.get("apdu"), "Internal Authenticate", result.get("res"), result);
			genWordUtil.add("Random Data:" + termRandom);
		} else {
			logger.debug("Application not support Offline Dynamic Data Authentication (DDA).");
			logger.debug("Internal Authenticate not performed!");
		}
		return result;
	}

	/**
	 * 脱机数据认证
	 * 
	 * @param cardRecordData
	 * @param pan
	 * @param aid
	 * @param aip
	 * @param staticDataList
	 * @param signedDynmicData
	 * @param termRandom
	 * @param logger
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static boolean dataAuthentication(HashMap<String, String> cardRecordData, String pan, String aid, String aip, String staticDataList, String signedDynmicData, String termRandom, Log logger, GenReportUtil genWordUtil) throws Exception {
		boolean processResult = false;
		// DDA,SDA
		String issuerPKCert = cardRecordData.get("90");
		String issuerPKReminder = cardRecordData.get("92");
		String issuerPKExp = cardRecordData.get("9F32");
		String signedStaticData = cardRecordData.get("93");
		String icPKCert = cardRecordData.get("9F46");
		String icPKExp = cardRecordData.get("9F47");
		String icPKReminder = cardRecordData.get("9F48");
		String caPKIndex = cardRecordData.get("8F");
		if (CommonHelper.support(aip, BaseHandler.AIP_SUPPORT_DDA) && cardRecordData.get("9F4A") != null) {
			staticDataList += aip;
		}
		String rid = aid.substring(0, 10);

		DataAuthenticate dataAuthenticate = new DataAuthenticate(rid, caPKIndex, issuerPKCert, issuerPKReminder, issuerPKExp, pan, staticDataList);
		List<String> logList = new ArrayList<String>();
		if (CommonHelper.support(aip, BaseHandler.AIP_SUPPORT_DDA)) {
			logger.debug("===========================Offline Dynamic Data Authentication (DDA)===============================");
			if (!dataAuthenticate.dynamicDataAuthenticate(icPKCert, icPKReminder, icPKExp, signedDynmicData, termRandom, logList)) {
				processResult = false;
				logger.error("DDA validate failed!");
				genWordUtil.add("动态数据认证失败");
				return processResult;
			}

			logger.debug("DDA validate successed,OK!");
			genWordUtil.add("DDA中使用的数据");
		} else if (CommonHelper.support(aip, BaseHandler.AIP_SUPPORT_SDA)) {
			logger.debug("===========================SDA validate===============================");
			if (!dataAuthenticate.staticDataAuthenticate(signedStaticData, logList)) {
				logger.error("SDA validate failed!");
				genWordUtil.add("静态数据认证失败");
				processResult = false;
				return processResult;
			}

			logger.debug("SDA validate successed!");
			genWordUtil.add("SDA中使用的数据");
		}
		processResult = true;
		for (String log : logList) {
			genWordUtil.add(log);
		}
		return processResult;
	}

	/**
	 * 获取内部数据元
	 * 
	 * @param cardRecordData
	 * @param apduHandler
	 * @return
	 */
	public static HashMap<String, String> getData(HashMap<String, String> cardRecordData, CommonAPDU apduHandler) {
		HashMap<String, String> dataMap = new HashMap<String, String>();
		result = apduHandler.getData("9F52");
		dataMap.put("9F52", result.get("9F52"));
		result = apduHandler.getData("9F54");
		dataMap.put("9F54", result.get("9F54"));
		result = apduHandler.getData("9F56");
		dataMap.put("9F56", result.get("9F56"));
		result = apduHandler.getData("9F57");
		dataMap.put("9F57", result.get("9F57"));
		result = apduHandler.getData("9F58");
		dataMap.put("9F58", result.get("9F58"));
		result = apduHandler.getData("9F59");
		dataMap.put("9F59", result.get("9F59"));
		result = apduHandler.getData("9F5C");
		dataMap.put("9F5C", result.get("9F5C"));
		return dataMap;
	}

	/**
	 * 处理限制
	 * 
	 * @param cardRecordData
	 * @param logger
	 * @return
	 */
	// 应用版本号检查
	//  应用用途控制检查
	//  生效日期检查
	//  失效日期检查
	public static boolean processingRestrictions(HashMap<String, String> cardRecordData, HashMap<String, String> param, Log logger) {
		boolean processResult = false;
		logger.debug("Card Application Version Number [9F08]:" + cardRecordData.get("9F08"));
		logger.debug("Issuer Country Code [5F28] :" + cardRecordData.get("5F28"));

		logger.debug("Check Application Effective Date");
		logger.debug("Application Effective Date [5F25]:" + cardRecordData.get("5F25"));
		logger.debug("Transaction Date [9A] :" + param.get("9A"));
		if (Integer.parseInt(param.get("9A")) >= Integer.parseInt(cardRecordData.get("5F25"))) {
			logger.debug("Check Application Effective Date...OK.");
		} else {
			processResult = false;
			logger.error("Check Application Effective Date error!");
		}

		logger.debug("Check Application Expiration Date");
		logger.debug("Application Expiration Date [5F24]:" + cardRecordData.get("5F24"));
		logger.debug("Transaction Date [9A] :" + param.get("9A"));
		if (Integer.parseInt(param.get("9A")) <= Integer.parseInt(cardRecordData.get("5F24")) && Integer.parseInt(cardRecordData.get("5F25")) <= Integer.parseInt(cardRecordData.get("5F24"))) {
			logger.debug("Check Application Expiration Date...OK.");
			processResult = true;
		} else {
			logger.error("Check Application Expiration Date error!");
			processResult = false;
		}
		return processResult;
	}

	/**
	 * 持卡人验证
	 * 
	 * @param cardRecordData
	 * @param apduHandler
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static boolean cardholderVerification(HashMap<String, String> cardRecordData, CommonAPDU apduHandler, Log logger, GenReportUtil genWordUtil) {
		boolean processResult = false;
		// Verify PIN
		if (WDAssert.isNotEmpty(cardRecordData.get("8E"))) {
			// 持卡人验证方法
			logger.debug("CVM LIST:");
			String chooseCVM = Terminal.parse8E(cardRecordData.get("8E"));
			logger.debug("CVM to be taken:\n" + chooseCVM);
			if (CommonHelper.supportOfflinePin(cardRecordData.get("8E"))) {
				logger.debug("=================================Verify PIN===========================");
				String pin = JOptionPane.showInputDialog("请输入PIN：");
				if (WDAssert.isNotEmpty(pin)) {
					result = apduHandler.verifyPin(pin);
					if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
						processResult = false;
						logger.error("verify pin failed,card return:" + result.get("sw"));
						genWordUtil.add(result.get("apdu"), "Verify PIN", result.get("res"), result);
					} else {
						processResult = true;
						logger.debug("Verify PIN PASS!");
						genWordUtil.add(result.get("apdu"), "Verify PIN", result.get("res"), result);
					}
				} else {
					processResult = false;
					logger.error("Cardholder not input PIN,Cardholder Verification FAIL!");
				}
			}
		}
		processResult = true;
		return processResult;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean terminalRiskManagement() {
		boolean processResult = true;
		return processResult;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean terminalActionAnalysis() {
		boolean processResult = true;
		return processResult;
	}

	/**
	 * Generate AC1
	 * 
	 * @param baseHandler
	 * @param cardRecordData
	 * @param param
	 * @param apduHandler
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> cardActionAnalysis(BaseHandler baseHandler, HashMap<String, String> cardRecordData, HashMap<String, String> param,String requestType, CommonAPDU apduHandler, GenReportUtil genWordUtil) throws Exception {
		// 交易日期 9A 3
		// 交易时间9F21 3
		// 授权金额9F02 6
		// 其它金额9F03 6
		// 终端国家代码9F1A 2
		// 交易货币代码5F2A 2
		// 商户名称 9F4E 20
		// 交易类型9C 1
		// 应用交易计数器（ATC） 9F36
		// 终端不可预知数 9F37
		// 终端验证结果TVR 95
		// 9F0206授权金额 9F0306其它金额 9F1A02终端国家代码 9505终端验证结果TVR 5F2A02交易货币代码 9A03交易日期 9F2103交易时间 9C01交易类型 9F3704终端不可预知数
		String cdol1Data = baseHandler.loadDolData(cardRecordData.get("8C"), param);
		// #######################################################
		// 控制参数 40： bit8，bit7 ：00=AAC--拒绝
		// 01=TC--脱机
		// 10=ARQC--联机
		// 11=RFU
		// 生成密文的数据源：第五部分附录D： 授权金额
		// #######################################################
		result = apduHandler.generateAC(cdol1Data, requestType);

		genWordUtil.add(result.get("apdu"), "Generate AC1", result.get("res"), result);
		genWordUtil.add("CDOL1 Data:" + cdol1Data);
		return result;
	}

	/**
	 * 联机处理
	 * 
	 * @param baseHandler
	 * @param pan
	 * @param panSerial
	 * @param aip
	 * @param atc
	 * @param iad
	 * @param arqc
	 * @param param
	 * @param logger
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static String onlineprocessing(BaseHandler baseHandler, String pan, String panSerial, String aip, String atc, String iad, String arqc, HashMap<String, String> param, Log logger, GenReportUtil genWordUtil) throws Exception {
		String gAC1_DDOL = baseHandler.loadDolData(BaseHandler.GAC1_CODOL, param);
		String arpc = issuerDao.requestArpc(pan, panSerial, gAC1_DDOL, aip, atc, iad, arqc);
		logger.debug("Online processing OK!");

		genWordUtil.add("验证ARQC中使用的数据");
		genWordUtil.add("ARQC:" + arqc);
		genWordUtil.add("ATC:" + atc);
		genWordUtil.add("IAD:" + iad);
		genWordUtil.add("ARPC:" + arpc);
		return arpc;
	}

	/**
	 * 发卡行认证
	 * 
	 * @param apduHandler
	 * @param arpc
	 * @param logger
	 * @param genWordUtil
	 * @return
	 */
	public static HashMap<String, String> issuerAuthentication(CommonAPDU apduHandler, String arpc, Log logger, GenReportUtil genWordUtil) {
		result = apduHandler.externalAuthenticate(arpc + authRespCode);
		if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
			logger.error("external Authenticate failed,card return:" + result.get("sw"));
			genWordUtil.add("发卡行认证失败！");
			// genWordUtil.close();
			return null;
		}

		genWordUtil.add(result.get("apdu"), "Issuer Authentication", result.get("res"), result);
		return result;
	}

	/**
	 * Generate AC2
	 * 
	 * @param baseHandler
	 * @param cardRecordData
	 * @param param
	 * @param apduHandler
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> completion(BaseHandler baseHandler, HashMap<String, String> cardRecordData, HashMap<String, String> param, CommonAPDU apduHandler, GenReportUtil genWordUtil) throws Exception {
		String cdol2Data = baseHandler.loadDolData(cardRecordData.get("8D"), param);
		result = apduHandler.generateAC(cdol2Data, AbstractAPDU.P1_TC);

		genWordUtil.add(result.get("apdu"), "Completion(Generate AC2)", result.get("res"), result);
		genWordUtil.add("CDOL2 Data:" + cdol2Data);
		return result;
	}

	/**
	 * 
	 * @param apduHandler
	 * @param pan
	 * @param panSerial
	 * @param atc
	 * @param arqc
	 * @param balance
	 * @param tradeMount
	 * @param genWordUtil
	 * @return
	 * @throws Exception
	 */
	public static boolean putData(CommonAPDU apduHandler, String pan, String panSerial, String atc, String arqc, String balance, int tradeMount, GenReportUtil genWordUtil) throws Exception {
		String[] script = issuerDao.generateLoadIssuerScript(pan, panSerial, atc, arqc, balance, tradeMount);
		String issuerScript = script[0];
		// 交易成功
		result = apduHandler.putData(issuerScript);
		// PUT DATA 报告
		genWordUtil.add(result.get("apdu"), "PUT DATA", result.get("res"), result);
		return true;
	}

	/**
	 * 
	 * @param apduHandler
	 * @param genWordUtil
	 * @throws Exception
	 */
	public static void showBlance(CommonAPDU apduHandler, GenReportUtil genWordUtil) throws Exception {
		result = apduHandler.getData("9F79");
		genWordUtil.add("电子现金账户余额：:" + result.get("9F79"));
	}
}
