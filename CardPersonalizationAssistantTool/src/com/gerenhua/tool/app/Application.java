package com.gerenhua.tool.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.SwingUtils;

/**
 * @title Application.java
 * @description 应用主程序
 * @author pei.li 2012-3-15
 * @version 1.0.0
 * @modify
 * @copyright watchdata
 */
public class Application extends JFrame {
	private static final long serialVersionUID = -1077236347297286235L;
	private static Logger log = Logger.getLogger(Application.class);
	public static Application frame;
	private static final String titleStr = Config.getValue("Terminal_Data", "appName");
	private static Container contentPanel;
	private static LeftPanel leftPanel;
	public static RightPanel rightPanel;
	private static JSplitPane splitPane;
	private static BottomPanel bottomPanel;
	private Color bakColor = null;

	/**
	 * @Title: main
	 * @Description 程序入口函数
	 * @param args
	 * @return
	 * @throws LookAndFeel不支持异常
	 *             ， 产生异常时会采用默认的LookAndFeel不会影响程序功能实现
	 */
	public static void main(String[] args) {
		try {
			// 设置皮肤
			String currentLaf = Config.getValue("CURRENT_THEME", "current_laf");
			String currentTheme = Config.getValue("CURRENT_THEME", "current_theme");
			String lafIndex = Config.getValue("CURRENT_THEME", "current_lafIndex");

			if (!currentTheme.equals("window")) {
				SwingUtils.setTheme(Integer.parseInt(lafIndex), currentTheme);
			}

			UIManager.setLookAndFeel(currentLaf);
			JFrame.setDefaultLookAndFeelDecorated(true);

			SwingUtils.updateUI();
		} catch (Exception e) {
			log.error("LookAndFeel unsupported.");
		}
		frame = new Application();
		frame.setVisible(true);
	}

	public Application() {
		setTitle(titleStr);
		setIconImage(new ImageIcon(Application.class.getResource("/com/gerenhua/tool/resources/images/logo_48.png")).getImage());
		initContentPane();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(d);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());

		bounds.x = insets.left;
		bounds.y = insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		setBounds(bounds);

		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initContentPane() {
		contentPanel = getContentPane();
		contentPanel.setLayout(new BorderLayout());
		leftPanel = new LeftPanel();
		try {
			rightPanel = new RightPanel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, rightPanel);
		splitPane.setEnabled(false);
		splitPane.setDividerLocation(170);
		contentPanel.add(splitPane, BorderLayout.CENTER);
		bottomPanel = new BottomPanel();
		bottomPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				Component container = e.getComponent();
				JPanel panel = (JPanel) container;
				panel.setBackground(bakColor);
				container.repaint();

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				Component container = e.getComponent();
				JPanel panel = (JPanel) container;
				bakColor = panel.getBackground();
				panel.setBackground(new Color(214, 223, 247));
				container.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		contentPanel.add(bottomPanel, BorderLayout.SOUTH);
	}
}
