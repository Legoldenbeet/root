package com.gerenhua.tool.app;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.PropertiesManager;

/**
 * @title BottomPanel.java
 * @description 程序底部界面
 * @author pei.li 2012-3-15
 * @version 1.0.0
 * @modify
 * @copyright watchdata
 */
public class BottomPanel extends JPanel {

	private static final long serialVersionUID = -6944067066093744254L;

	/**
	 * Create the panel
	 */
	public BottomPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		final JPanel panel = new JPanel();
		panel.setOpaque(false);
		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		final JLabel label = new JLabel();
		label.setText("主程序版本："+Config.getValue("Terminal_Data","version"));
		panel.add(label);

		final JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JLabel wwwwatchdatacomLabel = new JLabel();
		wwwwatchdatacomLabel.setText("网址："+Config.getValue("Terminal_Data","www"));
		panel_1.add(wwwwatchdatacomLabel);

		final JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		final JLabel label_1 = new JLabel();
		label_1.setText("版权归属："+Config.getValue("Terminal_Data","company"));
		panel_2.add(label_1);
	}

}
