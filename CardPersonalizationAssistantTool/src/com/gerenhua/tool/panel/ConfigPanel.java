package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.utils.Config;

/**
 * @author liya.xiao
 * @version 创建时间：2012-2-14 下午05:13:01 类说明
 */

public class ConfigPanel extends AbstractPanle {
	private JDialog dialog = new JDialog();
	private JEditorPane ep = new JEditorPane();
	private JScrollPane dlgscrollPane = new JScrollPane(ep);

	private static Log log = new Log();
	/**
	 * 
	 */
	private static final long serialVersionUID = 5031435350526969832L;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_7;
	private JLabel lblNewLabel_9;
	private JLabel lblNewLabel_11;
	private JLabel lblNewLabel_13;
	private JLabel lblNewLabel_15;
	private JLabel lblNewLabel_17;
	private JLabel lblNewLabel_18;

	public ConfigPanel() {
		super();
		setPreferredSize(new Dimension(899, 718));
		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);
		setLayout(new BorderLayout(0, 0));
		final JPanel ruleListPanel = new JPanel();
		ruleListPanel.setSize(780, 200);
		ruleListPanel.setPreferredSize(new Dimension(780, 220));
		add(ruleListPanel, BorderLayout.CENTER);
		ruleListPanel.setLayout(new BoxLayout(ruleListPanel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u7A7A\u95F4\u4FE1\u606F", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		ruleListPanel.add(panel);
		panel.setLayout(new GridLayout(0, 4, 0, 0));

		JLabel lblNewLabel = new JLabel("空间大小:");
		panel.add(lblNewLabel);

		label_2 = new JLabel("失败");
		panel.add(label_2);

		JLabel lblNewLabel_3 = new JLabel("校验值:");
		panel.add(lblNewLabel_3);

		label_3 = new JLabel("失败");
		panel.add(label_3);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "\u5361\u5E73\u53F0", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ruleListPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 4, 6, 0));

		JLabel lblNewLabel_1 = new JLabel("平台发布版本号:");
		panel_1.add(lblNewLabel_1);

		lblNewLabel_5 = new JLabel("失败");
		panel_1.add(lblNewLabel_5);

		JLabel lblNewLabel_4 = new JLabel("Java虚拟机版本:");
		panel_1.add(lblNewLabel_4);

		lblNewLabel_7 = new JLabel("失败");
		panel_1.add(lblNewLabel_7);

		JLabel lblNewLabel_6 = new JLabel("GP版本:");
		panel_1.add(lblNewLabel_6);

		lblNewLabel_9 = new JLabel("失败");
		panel_1.add(lblNewLabel_9);

		JLabel lblNewLabel_8 = new JLabel("VGP版本:");
		panel_1.add(lblNewLabel_8);

		lblNewLabel_11 = new JLabel("失败");
		panel_1.add(lblNewLabel_11);

		JLabel lblNewLabel_10 = new JLabel("77芯片驱动层:");
		panel_1.add(lblNewLabel_10);

		lblNewLabel_13 = new JLabel("失败");
		panel_1.add(lblNewLabel_13);

		JLabel lblNewLabel_12 = new JLabel("78芯片驱动层:");
		panel_1.add(lblNewLabel_12);

		lblNewLabel_15 = new JLabel("失败");
		panel_1.add(lblNewLabel_15);

		JLabel lblNewLabel_14 = new JLabel("RF协议TypeA:");
		panel_1.add(lblNewLabel_14);

		lblNewLabel_17 = new JLabel("失败");
		panel_1.add(lblNewLabel_17);

		JLabel lblNewLabel_16 = new JLabel("RF协议TypeB:");
		panel_1.add(lblNewLabel_16);

		lblNewLabel_18 = new JLabel("失败");
		panel_1.add(lblNewLabel_18);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "GP\u5E73\u53F0", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		ruleListPanel.add(panel_2);

		JLabel lblNewLabel_2 = new JLabel("New label");
		panel_2.add(lblNewLabel_2);

		final JPanel logPanel_wrap = new JPanel();
		logPanel_wrap.setPreferredSize(new Dimension(800, 200));
		logPanel_wrap.setLayout(new BorderLayout(0, 0));
		// logPanel_wrap.setBackground(new Color(193, 210, 240));
		final JPanel logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.X_AXIS));
		logPanel.setPreferredSize(new Dimension(800, 180));
		logPanel.setBorder(new TitledBorder(null, "数据", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		// logPanel.setBackground(new Color(193, 210, 240));
		logPanel_wrap.add(logPanel);
		add(logPanel_wrap, BorderLayout.SOUTH);

		JScrollPane jScrollPane = new JScrollPane(logTextArea);
		logPanel.add(jScrollPane);
		log.setLogArea(logTextArea);
		dialog.setSize(400, 200);
		dialog.getContentPane().add(dlgscrollPane);
	}

	public void initPanel() {
		try {
			CommonAPDU commonAPDU = new CommonAPDU();
			// getcheckvalue
			String apduList[] = Config.getValue("CardStatus", "privateAuth").split(",");
			for (String apdu : apduList) {
				commonAPDU.send(apdu);
			}
			String resp = commonAPDU.send(Config.getValue("CardStatus", "CardCheckValue"));
			if (resp.endsWith(Constants.SW_SUCCESS)) {
				String space=resp.substring(0, 8);
				label_2.setText(space.toUpperCase()+"[Byte]");
				label_2.setToolTipText(Integer.parseInt(space, 16)/1024+"[K]");
				label_3.setText(resp.substring(8, resp.length() - 4).toUpperCase());
			}
			// get8693
			HashMap<String, String> res=commonAPDU.reset();
			if (!"9000".equals(res.get("sw"))) {
				log.error("card reset error");
			}
			resp = commonAPDU.send("00A4040000");
			commonAPDU.reexternalAuthenticate();
			resp = "";
			resp = commonAPDU.send(Config.getValue("CardStatus", "GetPlatFormVersion"));
			if (resp.endsWith(Constants.SW_SUCCESS)) {
				resp=resp.substring(6);
				lblNewLabel_5.setText(resp.substring(0, 4));
				lblNewLabel_7.setText(resp.substring(4, 6));
				lblNewLabel_9.setText(resp.substring(6, 8));
				lblNewLabel_11.setText(resp.substring(8, 10));
				lblNewLabel_13.setText(resp.substring(10, 12));
				lblNewLabel_15.setText(resp.substring(12, 14));
				lblNewLabel_17.setText(resp.substring(14, 16));
				lblNewLabel_18.setText(resp.substring(16, 18));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
