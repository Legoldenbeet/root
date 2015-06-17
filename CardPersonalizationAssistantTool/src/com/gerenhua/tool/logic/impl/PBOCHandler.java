package com.gerenhua.tool.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.log4j.NDC;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.apdu.AbstractAPDU;
import com.gerenhua.tool.logic.apdu.CommonHelper;
import com.gerenhua.tool.logic.issuer.IIssuerDao;
import com.gerenhua.tool.logic.issuer.local.IssuerDaoImpl;
import com.gerenhua.tool.logic.pki.DataAuthenticate;
import com.gerenhua.tool.utils.PropertiesManager;
import com.gerenhua.tool.utils.TermSupportUtil;
import com.gerenhua.tool.utils.reportutil.APDUSendANDRes;
import com.gerenhua.tool.utils.reportutil.GenReportUtil;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDStringUtil;

/**
 * 
 * @description: PBOC交易逻辑处理
 * @author: juan.jiang 2012-3-21
 * @version: 1.0.0
 * @modify:
 * @Copyright: watchdata
 */
public class PBOCHandler extends BaseHandler {
	private static Log logger = new Log();
	private IIssuerDao issuerDao = new IssuerDaoImpl();
	private GenReportUtil genWordUtil = null;

	private PropertiesManager pm = new PropertiesManager();

	public PBOCHandler(JTextPane textPane) {
		logger.setLogArea(textPane);
	}

	/**
	 * @author juan.jiang
	 * @param tradeMount
	 *            交易金额
	 * @param readerName
	 *            读卡器名称
	 * @param tradeLabel
	 *            交易界面显示详细控件
	 * @param termSupportUtil
	 *            判断终端性能
	 * @return
	 */
	public boolean doTrade(int tradeMount, String readerName, TermSupportUtil termSupportUtil) {
		// 初始化交易参数，如授权金额，pin等
		HashMap<String, String> param = new HashMap<String, String>();
		String termRandom = WDStringUtil.getRandomHexString(8);
		param.put("9F02", WDStringUtil.paddingHeadZero(String.valueOf(tradeMount), 12));
//		param.put("9C","40");
		param.put("9F7A", "00");
		param.put("9F37", termRandom);
		Date dateTime = new Date();
		param.put("9A", getFormatDate(dateTime, Constants.FORMAT_SHORT_DATE));
		param.put("9F21", getFormatDate(dateTime, Constants.FORMAT_TIME));
		param.put("9F66", "46800000");// 非接触能力
		NDC.push("[PBOC]");
		logger.debug("PBOC trade start...", 0);
		genWordUtil = new GenReportUtil();

		genWordUtil.open(pm.getString("mv.tradepanel.lend"));
		genWordUtil.addFileTitle("PBOC交易检测报告");
		genWordUtil.addTransactionName("PBOC");

		try {
			// 为了保证卡片和读卡器的正确性，交易开始前务必先复位
			logger.debug("=============================reset===================================");
			HashMap<String, String> res = apduHandler.reset();
			if (!"9000".equals(res.get("sw"))) {
				logger.error("card reset falied");
				genWordUtil.add("卡片复位失败");
				// genWordUtil.close();
				return false;
			}
			logger.debug("atr:" + res.get("atr"));
			// 复位报告内容
			genWordUtil.add("atr", "Card Reset", res.get("atr"), new HashMap<String, String>());

			logger.debug("============================select PSE=================================");
			HashMap<String, String> result = apduHandler.select(Constants.PSE);
			if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
				logger.error("select PSE error,card return:" + result.get("sw"));
				genWordUtil.add("选择PSE出错");
				// genWordUtil.close();
				return false;
			}

			// 选择pse报告内容
			genWordUtil.add(result.get("apdu"), "Select PSE", result.get("res"), result);

			if (WDAssert.isNotEmpty(result.get("88"))) {
				// read dir, begin from 01
				logger.debug("==============================read dir================================");
				List<HashMap<String, String>> readDirList = apduHandler.readDir(result.get("88"));

				// select aid
				String aid = readDirList.get(0).get("4F");
				logger.debug("===============================select aid==============================");
				if (WDAssert.isEmpty(aid)) {
					logger.error("select aid is null");
					genWordUtil.add("获取AID为空");
					// genWordUtil.close();
					return false;
				}
				if (termSupportUtil.isSupportAID(aid)) {
					result = apduHandler.select(aid);
				} else {
					logger.error("Terminal can not support the app");
					genWordUtil.add("终端不支持此应用");
					// genWordUtil.close();
					return false;
				}
				if (!"9000".equals(result.get("sw"))) {
					logger.error("select app get response:" + result.get("sw"));
					genWordUtil.add("选择应用出错");
					// genWordUtil.close();
					return false;
				}
				String pdol = result.get("9F38");

				// 选择aid报告内容
				genWordUtil.add(result.get("apdu"), "Select AID", result.get("res"), result);
				genWordUtil.add("PDOL Data:" + pdol);
				// gpo
				logger.debug("==================================gpo==================================");
				String loadDolDataResult = "";
				try {
					loadDolDataResult = loadDolData(pdol, param);
				} catch (Exception e) {
					logger.error("PBOC get ddol param exception!");
					genWordUtil.add("获取DDOL数据出错");
					// genWordUtil.close();
					return false;
				}
				result = apduHandler.gpo("83" + CommonHelper.getLVData(loadDolDataResult, 1));
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

				genWordUtil.add(result.get("apdu"), "GPO", result.get("res"), result);

				genWordUtil.add("LoadDolDataResult:" + loadDolDataResult);
				genWordUtil.add("AIP:" + aip);

				// read record
				logger.debug("=================================read record===========================");
				List<APDUSendANDRes> aList = new ArrayList<APDUSendANDRes>();
				HashMap<String, String> cardRecordData = getCardRecordData(result.get("94"), aList);
				String staticDataList = cardRecordData.get("staticDataList");

				// 读记录报告
				for (APDUSendANDRes apduSendANDRes2 : aList) {
					genWordUtil.add(apduSendANDRes2);
				}
				// Internal Authenticate
				logger.debug("=======================internal Authenticate==============================");
				result = apduHandler.internalAuthenticate(termRandom);
				String signedDynmicData = result.get("80");

				genWordUtil.add(result.get("apdu"), "Internal Authenticate", result.get("res"), result);

				genWordUtil.add("Random Data:" + termRandom);
				genWordUtil.add("StaticDataList:" + staticDataList);
				// DDA,SDA
				String issuerPKCert = cardRecordData.get("90");
				String issuerPKReminder = cardRecordData.get("92");
				String issuerPKExp = cardRecordData.get("9F32");
				String signedStaticData = cardRecordData.get("93");
				String icPKCert = cardRecordData.get("9F46");
				String icPKExp = cardRecordData.get("9F47");
				String icPKReminder = cardRecordData.get("9F48");
				String caPKIndex = cardRecordData.get("8F");
				if (CommonHelper.support(aip, AIP_SUPPORT_DDA)) {
					staticDataList += aip;
				}
				String pan = cardRecordData.get("5A");
				pan = pan.replaceAll("F", "");
				String panSerial = cardRecordData.get("5F34");
				String rid = aid.substring(0, 10);

				DataAuthenticate dataAuthenticate = new DataAuthenticate(rid, caPKIndex, issuerPKCert, issuerPKReminder, issuerPKExp, pan, staticDataList);
				List<String> logList = new ArrayList<String>();
				if (CommonHelper.support(aip, AIP_SUPPORT_DDA)) {
					logger.debug("===========================DDA validate===============================");
					if (!dataAuthenticate.dynamicDataAuthenticate(icPKCert, icPKReminder, icPKExp, signedDynmicData, termRandom, logList)) {
						logger.error("DDA failed!");
						genWordUtil.add("动态数据认证失败");
						// genWordUtil.close();
						return false;
					}

					logger.debug("DDA validate successed!");
					genWordUtil.add("DDA中使用的数据");
				} else if (CommonHelper.support(aip, AIP_SUPPORT_SDA)) {
					logger.debug("===========================SDA validate===============================");
					if (!dataAuthenticate.staticDataAuthenticate(signedStaticData, logList)) {
						logger.error("SDA failed!");
						genWordUtil.add("静态数据认证失败");
						// genWordUtil.close();
						return false;
					}

					logger.debug("SDA validate successed!");
					genWordUtil.add("SDA中使用的数据");
				}

				for (String log : logList) {
					genWordUtil.add(log);
				}
				// get data
				logger.debug("================================get data=============================");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				result = apduHandler.getData("9F52");
				dataMap.put("9F52", result.get("9F52"));
				result = apduHandler.getData("9F54");
				dataMap.put("9F54", result.get("9F54"));
				result = apduHandler.getData("9F5C");
				dataMap.put("9F5C", result.get("9F5C"));
				result = apduHandler.getData("9F56");
				dataMap.put("9F56", result.get("9F56"));
				result = apduHandler.getData("9F57");
				dataMap.put("9F57", result.get("9F57"));
				result = apduHandler.getData("9F58");
				dataMap.put("9F58", result.get("9F58"));
				result = apduHandler.getData("9F59");
				dataMap.put("9F59", result.get("9F59"));
				// Verify PIN
				if (WDAssert.isNotEmpty(cardRecordData.get("8E"))) {
					if (CommonHelper.parse8E(cardRecordData.get("8E"))) {
						logger.debug("=================================Verify PIN===========================");
						String pin = JOptionPane.showInputDialog("请输入PIN：");
						if (WDAssert.isNotEmpty(pin)) {
							result = apduHandler.verifyPin(pin);
							if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
								logger.error("verify pin failed,card return:" + result.get("sw"));
								genWordUtil.add(result.get("apdu"), "Verify PIN", result.get("res"), result);
							} else {
								logger.debug("verify pin pass!");
								genWordUtil.add(result.get("apdu"), "Verify PIN", result.get("res"), result);
							}
						} else {
							logger.error("verify pin failed,card return:" + result.get("sw"));
						}
					}
				}

				// Generate arqc
				logger.debug("==========================Generate AC1================================");
//				交易日期 9A 3
//				交易时间9F21 3
//				授权金额9F02 6
//				其它金额9F03 6
//				终端国家代码9F1A 2
//				交易货币代码5F2A 2
//				商户名称 9F4E 20
//				交易类型9C 1
//				应用交易计数器（ATC） 9F36
//				终端不可预知数 9F37
//				终端验证结果TVR 95
//				9F0206授权金额 9F0306其它金额 9F1A02终端国家代码 9505终端验证结果TVR 5F2A02交易货币代码 9A03交易日期 9F2103交易时间 9C01交易类型 9F3704终端不可预知数
				String cdol1Data = loadDolData(cardRecordData.get("8C"), param);
				// #######################################################
				// 控制参数 40： bit8，bit7 ：00=AAC--拒绝
				// 01=TC--脱机
				// 10=ARQC--联机
				// 11=RFU
				// 生成密文的数据源：第五部分附录D： 授权金额
				// #######################################################
				result = apduHandler.generateAC(cdol1Data, AbstractAPDU.P1_ARQC);

				genWordUtil.add(result.get("apdu"), "Generate AC1", result.get("res"), result);

				genWordUtil.add("CDOL1 Data:" + cdol1Data);

				String arqc = result.get("9F26");
				String atc = result.get("9F36");
				String iad = result.get("9F10");
				String CVR = iad.substring(6, 14);
//				字节1： 长度字节 03
//				字节2：
//				位8–7：
//				00=第2 个GENERATE AC 返回AAC
//				01=第2 个GENERATE AC 返回TC
//				10=不请求第2 个GENERATE AC
//				11=RFU
//				位6–5：
//				00=第1 个GENERATE AC 返回AAC
//				01=第1 个GENERATE AC 返回TC
//				10=第1 个GENERATE AC 返回ARQC
//				11=不能返回11
//				位4：1=发卡行认证执行但失败
//				位3：1 =脱机PIN 执行
//				位2：1=脱机PIN 认证失败
//				位1：1 =不能联机
//				字节3：
//				位8：1=上次联机交易没有完成
//				位7：1=PIN 锁定
//				位6：1=超过频率检查
//				位5：1=新卡
//				位4：1=上次联机交易发卡行认证失败
//				位3：1=联机授权后，发卡行认证没有执行
//				位2：1=由于PIN 锁卡片锁定应用
//				位1：1=上次交易SDA 失败交易拒绝
//				字节4：
//				位8–5：上次交易第2 个生成应用密文（GENERATE
//				AC）命令后收到的带有安全报文的发卡行脚本命
//				令
//				位4：1 =上次交易发卡行脚本处理失败指针
//				位3：1=上次交易DDA 失败交易拒绝
				logger.debug("=================================ARQC=================================");
				String arpc = issuerDao.requestArpc(pan, panSerial, cdol1Data, aip, atc, iad, arqc);
				logger.debug("online validate successed!");

				genWordUtil.add("验证ARQC中使用的数据");
				genWordUtil.add("ARQC:" + arqc);
				genWordUtil.add("ATC:" + atc);
				genWordUtil.add("IAD:" + iad);
				genWordUtil.add("ARPC:" + arpc);
				// 请求发卡行认证AC密文
				logger.debug("=======================External Authenticate============================");
				result = apduHandler.externalAuthenticate(arpc + authRespCode);
				if (!Constants.SW_SUCCESS.equalsIgnoreCase(result.get("sw"))) {
					logger.error("external Authenticate failed,card return:" + result.get("sw"));
					genWordUtil.add("外部认证失败");
					// genWordUtil.close();
					return false;
				}

				genWordUtil.add(result.get("apdu"), "External Authenticate", result.get("res"), result);

				// Generate tc
//				if (CommonHelper.shiftRight(CVR, 22) != 2) {
					logger.debug("===========================Generate AC2===========================");
					param.put("8A", authRespCode);
					String cdol2Data = loadDolData(cardRecordData.get("8D"), param);
					result = apduHandler.generateAC(cdol2Data, AbstractAPDU.P1_TC);

					genWordUtil.add(result.get("apdu"), "Generate AC2", result.get("res"), result);
					genWordUtil.add("CDOL2 Data:" + cdol2Data);
//				}

				logger.debug("========================PBOC trade finished!=======================");
				genWordUtil.add("PBOC交易完成!");
				return true;
			} else {
				return false;
			}
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
