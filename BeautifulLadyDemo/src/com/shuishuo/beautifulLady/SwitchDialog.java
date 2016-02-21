/**
 * 
 */
package com.shuishuo.beautifulLady;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author LIYAXIAO
 *
 */
public class SwitchDialog extends JDialog {
	JButton btnNewButton;
	JButton button_1;
	JButton button_2;
	JButton button_3;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.frameBorderStyle.generalNoTranslucencyShadow;
					BeautyEyeLNFHelper.launchBeautyEyeLNF();
					SwitchDialog dialog = new SwitchDialog(null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public SwitchDialog(JFrame frame) {
		super(frame, true);
//		setBounds(100, 100, 767, 173);
		setSize( 780, 173);
		getContentPane().setLayout(null);
		
		btnNewButton = new JButton("售卡充值");
		btnNewButton.setEnabled(false);
		btnNewButton.setFont(new Font("微软雅黑", Font.PLAIN, 28));
		btnNewButton.setBounds(120, 10, 160, 120);
		getContentPane().add(btnNewButton);
		
		JButton button = new JButton("登陆");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(true);
				button_1.setEnabled(true);
				button_2.setEnabled(true);
				button_3.setEnabled(true);
			}
		});
		button.setFont(new Font("微软雅黑", Font.PLAIN, 28));
		button.setBounds(10, 10, 100, 120);
		getContentPane().add(button);
		
		button_1 = new JButton("系统设置");
		button_1.setEnabled(false);
		button_1.setFont(new Font("微软雅黑", Font.PLAIN, 28));
		button_1.setBounds(590, 10, 160, 120);
		getContentPane().add(button_1);
		
		button_2 = new JButton("资金结算");
		button_2.setEnabled(false);
		button_2.setFont(new Font("微软雅黑", Font.PLAIN, 28));
		button_2.setBounds(290, 10, 160, 120);
		getContentPane().add(button_2);
		
		button_3 = new JButton("登出");
		button_3.setEnabled(false);
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);;
				button_1.setEnabled(false);
				button_2.setEnabled(false);
				button_3.setEnabled(false);
			}
		});
		button_3.setFont(new Font("微软雅黑", Font.PLAIN, 28));
		button_3.setBounds(460, 10, 120, 120);
		getContentPane().add(button_3);
		setLocationRelativeTo(frame);
	}
}
