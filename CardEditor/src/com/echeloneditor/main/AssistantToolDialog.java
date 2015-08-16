package com.echeloneditor.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.echeloneditor.utils.Config;
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
		panel_1.setBounds(72, 45, 800, 160);
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
		panel.setBounds(72, 215, 800, 160);
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
		lblNewLabel.setBounds(10, 45, 59, 15);
		contentPanel.add(lblNewLabel);

		final JButton configip = new JButton("设置");
		configip.setVisible(false);
		configip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigIpDialog dialog = new ConfigIpDialog(CardEditor.frmEcheloneditor);
				dialog.setVisible(true);
			}
		});
		configip.setBounds(686, 10, 101, 25);
		contentPanel.add(configip);

		final JCheckBox chckbxHsm = new JCheckBox("Hsm");
		chckbxHsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxHsm.isSelected()) {
					keyField.setText(Config.getValue("HSM", "keyindex"));
					configip.setVisible(true);
				} else {
					configip.setVisible(false);
				}
			}
		});
		chckbxHsm.setBounds(601, 11, 79, 23);
		contentPanel.add(chckbxHsm);
		{
			JLabel lblKey = new JLabel("Key");
			lblKey.setHorizontalAlignment(SwingConstants.CENTER);
			lblKey.setBounds(10, 15, 59, 15);
			contentPanel.add(lblKey);
		}
		{
			keyField = new JTextField();
			keyField.setText("57415443484441544154696D65434F53");
			keyField.setColumns(10);
			keyField.setBounds(72, 13, 474, 22);
			contentPanel.add(keyField);
		}

		lblResult = new JLabel("Result");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setBounds(10, 233, 59, 15);
		contentPanel.add(lblResult);

		JButton btnBase = new JButton("Base64解码");
		btnBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDBase64.decode(dataField.getText(), statusObject.getSelectedEncodeItem()));
			}
		});
		btnBase.setBounds(316, 420, 125, 25);
		contentPanel.add(btnBase);

		JButton btnBase_1 = new JButton("Base64编码");
		btnBase_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDBase64.encode(dataField.getText(), statusObject.getSelectedEncodeItem()));
			}
		});
		btnBase_1.setBounds(316, 385, 125, 25);
		contentPanel.add(btnBase_1);

		JButton btnNewButton = new JButton("ECB DES");
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
		btnNewButton.setBounds(72, 385, 112, 25);
		contentPanel.add(btnNewButton);

		JButton btnEcbDecrypt = new JButton("ECB Decrypt");
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
		btnEcbDecrypt.setBounds(72, 420, 112, 25);
		contentPanel.add(btnEcbDecrypt);

		JButton btnCbcDes = new JButton("CBC DES");
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
		btnCbcDes.setBounds(194, 385, 112, 25);
		contentPanel.add(btnCbcDes);

		JButton btnCbcDecrypt = new JButton("CBC Decrypt");
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
		btnCbcDecrypt.setBounds(194, 420, 112, 25);
		contentPanel.add(btnCbcDecrypt);

		JButton btnNewButton_1 = new JButton("TDES MAC");
		btnNewButton_1.setToolTipText("Full Triple DES MAC");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String res = WD3DesCryptoUtil.cbc_encrypt(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000");
				restultField.setText(res.substring(res.length() - 16, res.length()));
			}
		});
		btnNewButton_1.setBounds(451, 385, 95, 25);
		contentPanel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("MAC");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDPBOCUtil.triple_des_mac(keyField.getText(), dataField.getText(), Padding.NoPadding, "0000000000000000"));
			}
		});
		btnNewButton_2.setToolTipText("Single DES Plus Final Triple DES MAC");
		btnNewButton_2.setBounds(451, 420, 95, 25);
		contentPanel.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("ASC->String");
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
		btnNewButton_3.setBounds(556, 423, 112, 25);
		contentPanel.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("String->ASC");
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
		btnNewButton_4.setBounds(556, 385, 112, 25);
		contentPanel.add(btnNewButton_4);

		JButton btnNewButton_5 = new JButton("SHA1");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha1(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnNewButton_5.setBounds(678, 385, 112, 25);
		contentPanel.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("MD5");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.md5(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnNewButton_6.setBounds(677, 423, 112, 25);
		contentPanel.add(btnNewButton_6);

		JButton btnXor = new JButton("XOR");
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
		btnXor.setBounds(800, 385, 112, 25);
		contentPanel.add(btnXor);

		JButton btnNot = new JButton("NOT");
		btnNot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "未实现！");
			}
		});
		btnNot.setBounds(799, 423, 112, 25);
		contentPanel.add(btnNot);

		JButton btnSha = new JButton("SHA256");
		btnSha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha256(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnSha.setBounds(922, 385, 112, 25);
		contentPanel.add(btnSha);

		JButton btnSha_1 = new JButton("SHA512");
		btnSha_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restultField.setText(WDByteUtil.bytes2HEX(WDEncodeUtil.sha512(WDByteUtil.HEX2Bytes(dataField.getText()))));
			}
		});
		btnSha_1.setBounds(921, 423, 112, 25);
		contentPanel.add(btnSha_1);

		JButton btnShafile = new JButton("File->SHA1");
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
		btnShafile.setBounds(72, 455, 112, 25);
		contentPanel.add(btnShafile);

		JButton btnMdfile = new JButton("File->MD5");
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
		btnMdfile.setBounds(194, 456, 112, 25);
		contentPanel.add(btnMdfile);

	}
}
