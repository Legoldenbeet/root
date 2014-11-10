package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.gerenhua.tool.log.Log;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.SwingConstants;

/**
 * @author landon E-mail:landonyongwen@126.com
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

	public ConfigPanel() {
		super();
		setPreferredSize(new Dimension(899, 718));
		// String[] ruleArray = new String[] { "固定值", "动态增长数", "校检位", "随机数" };
		// final SimpleDateFormat sf=new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);
		setLayout(new BorderLayout(0, 0));
		final JPanel ruleListPanel = new JPanel();
		ruleListPanel.setSize(780, 200);
		ruleListPanel.setPreferredSize(new Dimension(780, 220));
		// ruleListPanel.setBackground(new Color(193, 210, 240));
		add(ruleListPanel, BorderLayout.CENTER);
		ruleListPanel.setLayout(new BoxLayout(ruleListPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u7A7A\u95F4\u4FE1\u606F", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		ruleListPanel.add(panel);
		panel.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblNewLabel = new JLabel("空间大小:");
		panel.add(lblNewLabel);
		
		JLabel label_2 = new JLabel("失败");
		panel.add(label_2);
		
		JLabel lblNewLabel_3 = new JLabel("校验值:");
		panel.add(lblNewLabel_3);
		
		JLabel label_3 = new JLabel("失败");
		panel.add(label_3);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "\u5361\u5E73\u53F0", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ruleListPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 4, 6, 0));
		
		JLabel lblNewLabel_1 = new JLabel("平台发布版本号:");
		panel_1.add(lblNewLabel_1);
		
		JLabel lblNewLabel_5 = new JLabel("失败");
		panel_1.add(lblNewLabel_5);
		
		JLabel lblNewLabel_4 = new JLabel("Java虚拟机版本:");
		panel_1.add(lblNewLabel_4);
		
		JLabel lblNewLabel_7 = new JLabel("失败");
		panel_1.add(lblNewLabel_7);
		
		JLabel lblNewLabel_6 = new JLabel("GP版本:");
		panel_1.add(lblNewLabel_6);
		
		JLabel lblNewLabel_9 = new JLabel("失败");
		panel_1.add(lblNewLabel_9);
		
		JLabel lblNewLabel_8 = new JLabel("VGP版本:");
		panel_1.add(lblNewLabel_8);
		
		JLabel lblNewLabel_11 = new JLabel("失败");
		panel_1.add(lblNewLabel_11);
		
		JLabel lblNewLabel_10 = new JLabel("77芯片驱动层:");
		panel_1.add(lblNewLabel_10);
		
		JLabel lblNewLabel_13 = new JLabel("失败");
		panel_1.add(lblNewLabel_13);
		
		JLabel lblNewLabel_12 = new JLabel("78芯片驱动层:");
		panel_1.add(lblNewLabel_12);
		
		JLabel lblNewLabel_15 = new JLabel("失败");
		panel_1.add(lblNewLabel_15);
		
		JLabel lblNewLabel_14 = new JLabel("RF协议TypeA:");
		panel_1.add(lblNewLabel_14);
		
		JLabel lblNewLabel_17 = new JLabel("失败");
		panel_1.add(lblNewLabel_17);
		
		JLabel lblNewLabel_16 = new JLabel("RF协议TypeB:");
		panel_1.add(lblNewLabel_16);
		
		JLabel lblNewLabel_18 = new JLabel("失败");
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
}
