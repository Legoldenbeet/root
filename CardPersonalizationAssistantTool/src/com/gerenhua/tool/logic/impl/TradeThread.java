package com.gerenhua.tool.logic.impl;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import com.gerenhua.tool.panel.AtmPanel.TerminalSupportType;
import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.PropertiesManager;
import com.gerenhua.tool.utils.Terminal;
import com.watchdata.commons.lang.WDAssert;

public class TradeThread implements Runnable {
	public String money;
	public JTextPane textPane;
	public String tradeType;
	public JButton reportButton;
	private PropertiesManager pm = new PropertiesManager();

	public TradeThread(String money, String tradeType, JButton reportButton, JTextPane textPane) {
		this.money = money;
		this.tradeType = tradeType;
		this.reportButton = reportButton;
		this.textPane = textPane;
	}

	@Override
	public void run() {
		if ("".equals(tradeType)) {
			JOptionPane.showMessageDialog(null, pm.getString("mv.tradepanel.selectTradeType"), pm.getString("mv.testdata.InfoWindow"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 读卡器驱动名称
		String readerName = Config.getValue("Terminal_Data", "reader");
		if (WDAssert.isEmpty(money)) {
			JOptionPane.showMessageDialog(null, "请输入交易金额！");
			return;
		}
		// 交易金额
		int tradeMount = Integer.parseInt(money);

		if (!Terminal.isSupportTheFunction(TerminalSupportType.SUPPORTDDA)) {
			JOptionPane.showMessageDialog(null, "终端不支持DDA验证,交易无法进行!");
		}
		if ("qPBOC".equals(tradeType)) {
			// 执行交易
			QPBOCHandler qpbocHandler = new QPBOCHandler(textPane);
			qpbocHandler.trade(readerName, tradeMount);
		} else {
			if (!Terminal.isSupportTheFunction(TerminalSupportType.TOUCHIC)) {
				JOptionPane.showMessageDialog(null, "终端不支持接触式IC,交易无法进行!", pm.getString("mv.testdata.InfoWindow"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			if ("借贷记".equals(tradeType)) {
				PBOCHandler pBOCHandler = new PBOCHandler(textPane);
				pBOCHandler.doTrade(tradeMount, readerName);
			} else if ("电子现金".equals(tradeType)) {
				ElectronicCashHandler electronicCashHandler = new ElectronicCashHandler(textPane);
				electronicCashHandler.ECPurcharse(tradeMount, readerName);
			} else if ("圈存".equals(tradeType)) {
				ElectronicCashHandler electronicCashHandler = new ElectronicCashHandler(textPane);
				electronicCashHandler.ECLoad(tradeMount, readerName);
			}
		}

		reportButton.setEnabled(true);
	}
}
