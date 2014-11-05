package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

//import com.watchdata.numanag.dao.INumberDao;

/**
 * @author landon E-mail:landonyongwen@126.com
 * @version 创建时间：2012-2-14 下午05:13:01 类说明
 */

public class ConfigPanel extends AbstractPanle {
	private JDialog dialog = new JDialog();
	private JEditorPane ep = new JEditorPane();
	private JScrollPane dlgscrollPane = new JScrollPane(ep);

	private static Logger log = Logger.getLogger(ConfigPanel.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5031435350526969832L;

	public ConfigPanel() {
		super();
		setPreferredSize(new Dimension(843, 460));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// String[] ruleArray = new String[] { "固定值", "动态增长数", "校检位", "随机数" };
		// final SimpleDateFormat sf=new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		final BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(10);
		borderLayout.setVgap(10);
		final JPanel ruleListPanel = new JPanel();
		ruleListPanel.setSize(780, 200);
		ruleListPanel.setPreferredSize(new Dimension(780, 220));
		// ruleListPanel.setBackground(new Color(193, 210, 240));
		add(ruleListPanel);
		ruleListPanel.setLayout(new BorderLayout(0, 0));

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
		add(logPanel_wrap);

		JScrollPane jScrollPane = new JScrollPane(logTextArea);
		logPanel.add(jScrollPane);

		dialog.setSize(400, 200);
		dialog.getContentPane().add(dlgscrollPane);
	}
}
