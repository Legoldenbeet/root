package com.echeloneditor.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyPair;
import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.RsaUtil;
import com.echeloneditor.vo.StatusObject;
import com.watchdata.commons.crypto.WD3DesCryptoUtil;
import com.watchdata.commons.crypto.pboc.WDPBOCUtil;
import com.watchdata.commons.jce.JceBase.Padding;
import com.watchdata.commons.lang.WDBase64;
import com.watchdata.commons.lang.WDByteUtil;
import com.watchdata.commons.lang.WDEncodeUtil;
import com.watchdata.kms.kmsi.IKms;

public class AssistantToolDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea dataField;
	private JTextArea restultField;
	private JTextField keyField;
	private JLabel lblResult;
	private StatusObject statusObject;
	private JTextField textField;
	private JTextField lenTextField_1;
	private JTextField expTextField_2;
	private JTextField modulusTextField_1;

	/**
	 * Create the dialog.
	 */
	public AssistantToolDialog(final StatusObject statusObject) {
		this.statusObject = statusObject;
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(72, 75, 840, 200);
		contentPanel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel_1.add(scrollPane_1, BorderLayout.CENTER);

		dataField = new JTextArea();
		dataField.setLineWrap(true);
		scrollPane_1.setViewportView(dataField);

		JPanel panel = new JPanel();
		panel.setBounds(72, 285, 840, 200);
		contentPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, BorderLayout.CENTER);

		restultField = new JTextArea();
		restultField.setLineWrap(true);
		scrollPane.setViewportView(restultField);

		JLabel lblNewLabel = new JLabel("Data");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 75, 59, 15);
		contentPanel.add(lblNewLabel);

		final JButton configip = new JButton("设置");
		configip.setBorderPainted(false);
		configip.setVisible(false);
		configip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigIpDialog dialog = new ConfigIpDialog(CardEditor.frmEcheloneditor);
				dialog.setVisible(true);
			}
		});
		configip.setBounds(846, 15, 67, 21);
		contentPanel.add(configip);

		final JCheckBox chckbxHsm = new JCheckBox("");
		chckbxHsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxHsm.isSelected()) {
					keyField.setText(Config.getValue("HSM", "keyindex"));
					configip.setVisible(true);
				} else {
					keyField.setText("404142434445464748494A4B4C4D4E4F");
					configip.setVisible(false);
				}
			}
		});
		chckbxHsm.setBounds(812, 15, 51, 21);
		contentPanel.add(chckbxHsm);
		{
			JLabel lblKey = new JLabel("Key");
			lblKey.setHorizontalAlignment(SwingConstants.CENTER);
			lblKey.setBounds(10, 15, 59, 15);
			contentPanel.add(lblKey);
		}
		{
			keyField = new JTextField();
			keyField.setText("404142434445464748494A4B4C4D4E4F");
			keyField.setColumns(10);
			keyField.setBounds(72, 15, 332, 22);
			contentPanel.add(keyField);
		}

		lblResult = new JLabel("Result");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(10, 343, 59, 15);
		contentPanel.add(lblResult);

		JButton btnBase = new JButton("Base64解码");
		btnBase.setBorderPainted(false);
		btnBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDBase64.decode(dataField.getText(), statusObject.getSelectedEncodeItem()));
			}
		});
		btnBase.setBounds(316, 530, 125, 25);
		contentPanel.add(btnBase);

		JButton btnBase_1 = new JButton("Base64编码");
		btnBase_1.setBorderPainted(false);
		btnBase_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDBase64.encode(dataField.getText(), statusObject.getSelectedEncodeItem()));
			}
		});
		btnBase_1.setBounds(316, 495, 125, 25);
		contentPanel.add(btnBase_1);

		JButton btnNewButton = new JButton("ECB DES");
		// btnNewButton.setFocusPainted(false);
		btnNewButton.setBorderPainted(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (chckbxHsm.isSelected()) {
						IKms iKms = IKms.getInstance();

						restultField.setText(iKms.encrypt(keyField.getText(), IKms.DES_ECB, dataField.getText(), Config.getValue("HSM", "IP") + "_" + Config.getValue("HSM", "PORT")));
						Config.setValue("HSM", "keyindex", keyField.getText());

					} else {
						restultField.setText(WD3DesCryptoUtil.ecb_encrypt(keyField.getText(), dataField.getText(), Padding.NoPadding));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(72, 495, 112, 25);
		contentPanel.add(btnNewButton);

		JButton btnEcbDecrypt = new JButton("ECB Decrypt");
		btnEcbDecrypt.setBorderPainted(false);
		btnEcbDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (chckbxHsm.isSelected()) {
						IKms iKms = IKms.getInstance();

						restultField.setText(iKms.decrypt(keyField.getText(), IKms.DES_ECB, dataField.getText(), Config.getValue("HSM", "IP") + "_" + Config.getValue("HSM", "PORT")));
						Config.setValue("HSM", "keyindex", keyField.getText());

					} else {
						restultField.setText(WD3DesCryptoUtil.ecb_decrypt(keyField.getText(), dataField.getText(), Padding.NoPadding));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnEcbDecrypt.setBounds(72, 530, 112, 25);
		contentPanel.add(btnEcbDecrypt);

		JButton btnCbcDes = new JButton("CBC DES");
		btnCbcDes.setBorderPainted(false);
		btnCbcDes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (chckbxHsm.isSelected()) {
						IKms iKms = IKms.getInstance();

						restultField.setText(iKms.encrypt(keyField.getText(), IKms.DES_CBC, dataField.getText(), "0000000000000000", Config.getValue("HSM", "IP") + "_" + Config.getValue("HSM", "PORT")));
						Config.setValue("HSM", "keyindex", keyField.getText());

					} else {
						restultField.setText(WD3DesCryptoUtil.cbc_encrypt(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000"));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnCbcDes.setBounds(194, 495, 112, 25);
		contentPanel.add(btnCbcDes);

		JButton btnCbcDecrypt = new JButton("CBC Decrypt");
		btnCbcDecrypt.setBorderPainted(false);
		btnCbcDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (chckbxHsm.isSelected()) {
						IKms iKms = IKms.getInstance();

						restultField.setText(iKms.decrypt(keyField.getText(), IKms.DES_CBC, dataField.getText(), "0000000000000000", Config.getValue("HSM", "IP") + "_" + Config.getValue("HSM", "PORT")));
						Config.setValue("HSM", "keyindex", keyField.getText());

					} else {
						restultField.setText(WD3DesCryptoUtil.cbc_decrypt(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000"));
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnCbcDecrypt.setBounds(194, 530, 112, 25);
		contentPanel.add(btnCbcDecrypt);

		JButton btnNewButton_1 = new JButton("TDES MAC");
		btnNewButton_1.setBorderPainted(false);
		btnNewButton_1.setToolTipText("Full Triple DES MAC");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String res = WD3DesCryptoUtil.cbc_encrypt(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000");
				restultField.setText(res.substring(res.length() - 16, res.length()));
			}
		});
		btnNewButton_1.setBounds(451, 495, 95, 25);
		contentPanel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("MAC");
		btnNewButton_2.setBorderPainted(false);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDPBOCUtil.triple_des_mac(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000"));
			}
		});
		btnNewButton_2.setToolTipText("Single DES Plus Final Triple DES MAC");
		btnNewButton_2.setBounds(451, 530, 95, 25);
		contentPanel.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("ASC->String");
		btnNewButton_3.setBorderPainted(false);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// restultField.setText(WDStringUtil.hex2asc(new String(dataField.getText().getBytes(statusObject.getSelectedEncodeItem()),statusObject.getSelectedEncodeItem())));
					restultField.setText(new String(WDByteUtil.HEX2Bytes(dataField.getText()), statusObject.getSelectedEncodeItem()));
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		btnNewButton_3.setBounds(556, 533, 112, 25);
		contentPanel.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("String->ASC");
		btnNewButton_4.setBorderPainted(false);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// restultField.setText(WDStringUtil.asc2hex(new String(dataField.getText().getBytes(statusObject.getSelectedEncodeItem()), statusObject.getSelectedEncodeItem())));
					restultField.setText(WDByteUtil.bytes2HEX(dataField.getText().getBytes(statusObject.getSelectedEncodeItem())));
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		btnNewButton_4.setBounds(556, 495, 112, 25);
		contentPanel.add(btnNewButton_4);

		JButton btnNewButton_5 = new JButton("SHA1");
		btnNewButton_5.setBorderPainted(false);
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha1(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnNewButton_5.setBounds(678, 495, 112, 25);
		contentPanel.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("MD5");
		btnNewButton_6.setBorderPainted(false);
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.md5(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnNewButton_6.setBounds(677, 533, 112, 25);
		contentPanel.add(btnNewButton_6);

		JButton btnXor = new JButton("XOR");
		btnXor.setBorderPainted(false);
		btnXor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String com1 = keyField.getText();
				String com2 = dataField.getText();

				if (com1.length() % 2 != 0 || com2.length() % 2 != 0) {
					JOptionPane.showMessageDialog(null, "不是整字节！");
					return;
				}

				byte[] com1Bytes = new byte[com1.length() / 2];
				byte[] com2Bytes = new byte[com2.length() / 2];

				com1Bytes = WDByteUtil.HEX2Bytes(com1);
				com2Bytes = WDByteUtil.HEX2Bytes(com2);

				if (com1Bytes.length != com2Bytes.length) {
					JOptionPane.showMessageDialog(null, "长度不一致！");
					return;
				}

				byte[] xorBytes = new byte[com1Bytes.length];
				for (int i = 0; i < xorBytes.length; i++) {
					xorBytes[i] = (byte) (com1Bytes[i] ^ com2Bytes[i]);
				}

				restultField.setText(WDByteUtil.bytes2HEX(xorBytes));

			}
		});
		btnXor.setBounds(800, 495, 112, 25);
		contentPanel.add(btnXor);

		JButton btnNot = new JButton("NOT");
		btnNot.setBorderPainted(false);
		btnNot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// String com1 = keyField.getText();
				String com2 = dataField.getText();

				if (com2.length() % 2 != 0) {
					JOptionPane.showMessageDialog(null, "不是整字节！");
					return;
				}

				byte[] com2Bytes = WDByteUtil.HEX2Bytes(com2);

				for (int i = 0; i < com2Bytes.length; i++) {
					com2Bytes[i] = (byte) (~com2Bytes[i]);
				}

				restultField.setText(WDByteUtil.bytes2HEX(com2Bytes));
			}
		});
		btnNot.setBounds(799, 533, 112, 25);
		contentPanel.add(btnNot);

		JButton btnSha = new JButton("SHA256");
		btnSha.setBorderPainted(false);
		btnSha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha256(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnSha.setBounds(195, 565, 112, 25);
		contentPanel.add(btnSha);

		JButton btnSha_1 = new JButton("SHA512");
		btnSha_1.setBorderPainted(false);
		btnSha_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha512(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnSha_1.setBounds(194, 603, 112, 25);
		contentPanel.add(btnSha_1);

		JButton btnShafile = new JButton("File->SHA1");
		btnShafile.setBorderPainted(false);
		btnShafile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jFileChooser = new JFileChooser();
				int ret = jFileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();
					try {
						FileInputStream fis = new FileInputStream(file);
						FileChannel fc = fis.getChannel();
						MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
						MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
						messageDigest.update(byteBuffer);
						File md5File = new File(file.getParent() + "/" + "SHA1.txt");

						FileOutputStream out = new FileOutputStream(md5File);
						out.write(WDByteUtil.bytes2HEX(messageDigest.digest()).getBytes());

						fis.close();
						fc.close();
						out.close();

						JOptionPane.showMessageDialog(null, "计算完成！");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		btnShafile.setBounds(72, 565, 112, 25);
		contentPanel.add(btnShafile);

		JButton btnMdfile = new JButton("File->MD5");
		btnMdfile.setBorderPainted(false);
		btnMdfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				int ret = jFileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();
					try {
						FileInputStream fis = new FileInputStream(file);
						FileChannel fc = fis.getChannel();
						MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
						MessageDigest messageDigest = MessageDigest.getInstance("MD5");
						messageDigest.update(byteBuffer);
						File md5File = new File(file.getParent() + "/" + "MD5.txt");

						FileOutputStream out = new FileOutputStream(md5File);
						out.write(WDByteUtil.bytes2HEX(messageDigest.digest()).getBytes());

						fis.close();
						fc.close();
						out.close();

						JOptionPane.showMessageDialog(null, "计算完成！");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		btnMdfile.setBounds(72, 603, 112, 25);
		contentPanel.add(btnMdfile);

		JButton btnRsapublicdecrypt = new JButton("RSA_Decrypt");
		btnRsapublicdecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(RsaUtil.rsa_decrypt(modulusTextField_1.getText().trim(), expTextField_2.getText().trim(), dataField.getText().trim()));
			}
		});
		btnRsapublicdecrypt.setBorderPainted(false);
		btnRsapublicdecrypt.setBounds(316, 565, 125, 25);
		contentPanel.add(btnRsapublicdecrypt);

		JButton btnRsagenerate = new JButton("RSA_Generate");
		btnRsagenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					KeyPair keyPair = RsaUtil.generateKeyPair(Integer.parseInt(lenTextField_1.getText().trim()), Integer.parseInt(expTextField_2.getText().trim()));
					restultField.setText(keyPair.getPrivate().toString());
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		btnRsagenerate.setBorderPainted(false);
		btnRsagenerate.setBounds(316, 603, 125, 25);
		contentPanel.add(btnRsagenerate);

		textField = new JTextField();
		textField.setText("0000000000000000");
		textField.setBounds(444, 15, 155, 22);
		contentPanel.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Iv:");
		lblNewLabel_1.setBounds(414, 15, 33, 21);
		contentPanel.add(lblNewLabel_1);

		JLabel lblSize = new JLabel("Len:");
		lblSize.setBounds(609, 15, 33, 21);
		contentPanel.add(lblSize);

		lenTextField_1 = new JTextField();
		lenTextField_1.setText("1024");
		lenTextField_1.setColumns(10);
		lenTextField_1.setBounds(652, 15, 44, 22);
		contentPanel.add(lenTextField_1);

		JLabel lblExp = new JLabel("Exp:");
		lblExp.setBounds(713, 15, 35, 21);
		contentPanel.add(lblExp);

		expTextField_2 = new JTextField();
		expTextField_2.setText("03");
		expTextField_2.setColumns(10);
		expTextField_2.setBounds(758, 15, 44, 22);
		contentPanel.add(expTextField_2);

		JLabel lblNewLabel_2 = new JLabel("Modulus");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(0, 50, 69, 15);
		contentPanel.add(lblNewLabel_2);

		modulusTextField_1 = new JTextField();
		modulusTextField_1.setBounds(72, 45, 841, 21);
		contentPanel.add(modulusTextField_1);
		modulusTextField_1.setColumns(10);

	}
}
