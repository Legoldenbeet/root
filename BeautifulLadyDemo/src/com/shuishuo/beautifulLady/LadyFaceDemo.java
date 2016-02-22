package com.shuishuo.beautifulLady;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import javax.swing.SwingConstants;

public class LadyFaceDemo {

	private JFrame frmAfc;
	public static final Color BACK_COLOR = new Color(116, 149, 226);
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JLabel label_2;
	private JTextField textField_3;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JLabel label_3;
	private JTextField textField_4;
	private JLabel label_4;
	private JButton button_3;
	private JButton button_4;
	private JButton button_5;
	private JTextField textField_5;
	private JLabel label_6;
	private JTextField textField_6;
	private JLabel label_7;
	private JTextField textField_7;
	private JLabel label_8;
	private JTextField textField_8;
	private JLabel label_9;
	private JTextField textField_9;
	private JLabel label_10;
	private JTextField textField_10;
	private JLabel label_11;
	private JTextField textField_11;
	private JLabel label_12;
	private JTextField textField_12;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.frameBorderStyle.generalNoTranslucencyShadow;
					BeautyEyeLNFHelper.launchBeautyEyeLNF();
					LadyFaceDemo window = new LadyFaceDemo();
					window.frmAfc.setVisible(true);
					window.frmAfc.setExtendedState(JFrame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LadyFaceDemo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAfc = new JFrame();
		frmAfc.setIconImage(Toolkit.getDefaultToolkit().getImage(LadyFaceDemo.class.getResource("/resources/images/beijing_mtr.jpg")));
		frmAfc.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		frmAfc.setTitle("\u5317\u4EAC\u5730\u94C1\u552E\u5361\u5145\u503C\u7CFB\u7EDF");
		// 最大化 居中 不遮盖任务栏
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(d);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(frmAfc.getGraphicsConfiguration());

		bounds.x = insets.left;
		bounds.y = insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		frmAfc.setBounds(bounds);
		// 设置背景
		frmAfc.getContentPane().setBackground(BACK_COLOR);
		frmAfc.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField.setText("10007510724045616430");
		textField.setBounds(820, 28, 400, 40);
		textField.setToolTipText("10007510724045616430");
		frmAfc.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("\u5361\u53F7");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		lblNewLabel.setBounds(674, 28, 60, 40);
		frmAfc.getContentPane().add(lblNewLabel);

		JLabel label = new JLabel("\u72B6\u6001");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label.setBounds(674, 154, 60, 40);
		frmAfc.getContentPane().add(label);

		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_1.setText("\u5DF2\u542F\u7528");
		textField_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_1.setColumns(10);
		textField_1.setBounds(820, 154, 400, 40);
		frmAfc.getContentPane().add(textField_1);

		JLabel label_1 = new JLabel("卡片类型");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_1.setBounds(49, 91, 120, 40);
		frmAfc.getContentPane().add(label_1);

		textField_2 = new JTextField();
		textField_2.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_2.setText("普通储值卡");
		textField_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_2.setColumns(10);
		textField_2.setBounds(209, 91, 400, 40);
		frmAfc.getContentPane().add(textField_2);

		label_2 = new JLabel("充值前余额");
		label_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_2.setBounds(49, 154, 150, 40);
		frmAfc.getContentPane().add(label_2);

		textField_3 = new JTextField();
		textField_3.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_3.setText("50.2\u5143");
		textField_3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_3.setColumns(10);
		textField_3.setBounds(209, 154, 400, 40);
		frmAfc.getContentPane().add(textField_3);

		JButton btnNewButton = new JButton("\u5145\u503C10\u5143");
		btnNewButton.setToolTipText("<html><body>This message just used for demo, cool tool tip!<br>Ni hao Jack Jiang.</body></html>");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		btnNewButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		btnNewButton.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		btnNewButton.setBounds(49, 689, 180, 46);
		frmAfc.getContentPane().add(btnNewButton);

		button = new JButton("\u5145\u503C20\u5143");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button.setBounds(255, 689, 180, 46);
		frmAfc.getContentPane().add(button);

		button_1 = new JButton("\u5145\u503C30\u5143");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_1.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_1.setBounds(464, 689, 180, 46);
		frmAfc.getContentPane().add(button_1);

		button_2 = new JButton("\u5145\u503C50\u5143");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_2.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_2.setBounds(676, 689, 180, 46);
		frmAfc.getContentPane().add(button_2);

		label_3 = new JLabel("\u81EA\u5B9A\u4E49\u91D1\u989D");
		label_3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_3.setBounds(49, 774, 130, 40);
		frmAfc.getContentPane().add(label_3);

		textField_4 = new JTextField();
		textField_4.setText("200");
		textField_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_4.setColumns(10);
		textField_4.setBounds(189, 775, 76, 40);
		frmAfc.getContentPane().add(textField_4);

		label_4 = new JLabel("\u5143");
		label_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_4.setBounds(275, 774, 30, 40);
		frmAfc.getContentPane().add(label_4);

		button_3 = new JButton("\u5145\u503C");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, "充值" + textField_4.getText() + label_4.getText() + "完成");
				textField_4.requestFocusInWindow();
				textField_4.setText("");
			}
		});
		button_3.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_3.setBounds(343, 774, 90, 40);
		frmAfc.getContentPane().add(button_3);

		button_4 = new JButton("\u51B2\u8D26");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, "最后一笔充值冲账完成");
			}
		});
		button_4.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_4.setBounds(464, 774, 90, 40);
		frmAfc.getContentPane().add(button_4);

		button_5 = new JButton("\u5145\u503C100\u5143");
		button_5.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_5.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_5.setBounds(887, 689, 180, 46);
		frmAfc.getContentPane().add(button_5);
		
		JLabel label_5 = new JLabel("卡发行商");
		label_5.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_5.setBounds(674, 90, 120, 40);
		frmAfc.getContentPane().add(label_5);
		
		textField_5 = new JTextField();
		textField_5.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_5.setText("北京一卡通");
		textField_5.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_5.setColumns(10);
		textField_5.setBounds(820, 90, 400, 40);
		frmAfc.getContentPane().add(textField_5);
		
		label_6 = new JLabel("交易类型");
		label_6.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_6.setBounds(49, 29, 120, 40);
		frmAfc.getContentPane().add(label_6);
		
		textField_6 = new JTextField();
		textField_6.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_6.setText("充值");
		textField_6.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_6.setColumns(10);
		textField_6.setBounds(209, 28, 400, 40);
		frmAfc.getContentPane().add(textField_6);
		
		label_7 = new JLabel("充值金额");
		label_7.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_7.setBounds(49, 217, 120, 40);
		frmAfc.getContentPane().add(label_7);
		
		textField_7 = new JTextField();
		textField_7.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_7.setText("30元");
		textField_7.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_7.setColumns(10);
		textField_7.setBounds(209, 217, 400, 40);
		frmAfc.getContentPane().add(textField_7);
		
		label_8 = new JLabel("充值后余额");
		label_8.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_8.setBounds(49, 285, 150, 40);
		frmAfc.getContentPane().add(label_8);
		
		textField_8 = new JTextField();
		textField_8.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_8.setText("80.2元");
		textField_8.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_8.setColumns(10);
		textField_8.setBounds(209, 285, 400, 40);
		frmAfc.getContentPane().add(textField_8);
		
		label_9 = new JLabel("有效期限");
		label_9.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_9.setBounds(674, 217, 120, 40);
		frmAfc.getContentPane().add(label_9);
		
		textField_9 = new JTextField();
		textField_9.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_9.setText("2017-11-27");
		textField_9.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_9.setColumns(10);
		textField_9.setBounds(820, 217, 400, 40);
		frmAfc.getContentPane().add(textField_9);
		
		label_10 = new JLabel("应收金额");
		label_10.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_10.setBounds(49, 349, 120, 40);
		frmAfc.getContentPane().add(label_10);
		
		textField_10 = new JTextField();
		textField_10.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_10.setText("30元");
		textField_10.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_10.setColumns(10);
		textField_10.setBounds(209, 349, 400, 40);
		frmAfc.getContentPane().add(textField_10);
		
		label_11 = new JLabel("付款金额");
		label_11.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_11.setBounds(49, 407, 120, 40);
		frmAfc.getContentPane().add(label_11);
		
		textField_11 = new JTextField();
		textField_11.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_11.setText("50元");
		textField_11.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_11.setColumns(10);
		textField_11.setBounds(209, 407, 400, 40);
		frmAfc.getContentPane().add(textField_11);
		
		label_12 = new JLabel("找零金额");
		label_12.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_12.setBounds(49, 468, 120, 40);
		frmAfc.getContentPane().add(label_12);
		
		textField_12 = new JTextField();
		textField_12.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_12.setText("20元");
		textField_12.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_12.setColumns(10);
		textField_12.setBounds(209, 468, 400, 40);
		frmAfc.getContentPane().add(textField_12);
		// 设置可改变尺寸
		frmAfc.setResizable(false);
//		frmAfc.setAlwaysOnTop(true);
		frmAfc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
