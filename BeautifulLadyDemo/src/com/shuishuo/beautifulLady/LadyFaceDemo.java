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
		textField.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField.setText("10007510724045616430");
		textField.setBounds(128, 23, 400, 40);
		textField.setToolTipText("10007510724045616430");
		frmAfc.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("\u5361\u53F7");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		lblNewLabel.setBounds(58, 23, 60, 40);
		frmAfc.getContentPane().add(lblNewLabel);

		JLabel label = new JLabel("\u72B6\u6001");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label.setBounds(1016, 23, 60, 40);
		frmAfc.getContentPane().add(label);

		textField_1 = new JTextField();
		textField_1.setText("\u5DF2\u542F\u7528");
		textField_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_1.setColumns(10);
		textField_1.setBounds(1086, 23, 102, 40);
		frmAfc.getContentPane().add(textField_1);

		JLabel label_1 = new JLabel("\u5361\u7C7B\u578B");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_1.setBounds(706, 23, 90, 40);
		frmAfc.getContentPane().add(label_1);

		textField_2 = new JTextField();
		textField_2.setText("\u666E\u901A\u5145\u503C\u5361");
		textField_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_2.setColumns(10);
		textField_2.setBounds(806, 23, 166, 40);
		frmAfc.getContentPane().add(textField_2);

		label_2 = new JLabel("\u4F59\u989D");
		label_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_2.setBounds(58, 106, 60, 40);
		frmAfc.getContentPane().add(label_2);

		textField_3 = new JTextField();
		textField_3.setText("50.2\u5143");
		textField_3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_3.setColumns(10);
		textField_3.setBounds(128, 107, 102, 40);
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
		btnNewButton.setBounds(128, 301, 180, 46);
		frmAfc.getContentPane().add(btnNewButton);

		button = new JButton("\u5145\u503C20\u5143");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button.setBounds(334, 301, 180, 46);
		frmAfc.getContentPane().add(button);

		button_1 = new JButton("\u5145\u503C30\u5143");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_1.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_1.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_1.setBounds(543, 301, 180, 46);
		frmAfc.getContentPane().add(button_1);

		button_2 = new JButton("\u5145\u503C50\u5143");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_2.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_2.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_2.setBounds(755, 301, 180, 46);
		frmAfc.getContentPane().add(button_2);

		label_3 = new JLabel("\u81EA\u5B9A\u4E49\u91D1\u989D");
		label_3.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_3.setBounds(128, 386, 130, 40);
		frmAfc.getContentPane().add(label_3);

		textField_4 = new JTextField();
		textField_4.setText("200");
		textField_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		textField_4.setColumns(10);
		textField_4.setBounds(268, 387, 76, 40);
		frmAfc.getContentPane().add(textField_4);

		label_4 = new JLabel("\u5143");
		label_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		label_4.setBounds(354, 386, 30, 40);
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
		button_3.setBounds(422, 386, 90, 40);
		frmAfc.getContentPane().add(button_3);

		button_4 = new JButton("\u51B2\u8D26");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, "最后一笔充值冲账完成");
			}
		});
		button_4.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_4.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_4.setBounds(131, 464, 90, 40);
		frmAfc.getContentPane().add(button_4);

		button_5 = new JButton("\u5145\u503C100\u5143");
		button_5.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmAfc, ((JButton) e.getSource()).getText() + "完成");
			}
		});
		button_5.setFont(new Font("微软雅黑", Font.PLAIN, 24));
		button_5.setBounds(966, 301, 180, 46);
		frmAfc.getContentPane().add(button_5);
		// 设置可改变尺寸
		frmAfc.setResizable(false);
//		frmAfc.setAlwaysOnTop(true);
		frmAfc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
