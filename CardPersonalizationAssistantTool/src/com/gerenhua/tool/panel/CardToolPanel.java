package com.gerenhua.tool.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.gerenhua.tool.utils.Config;

public class CardToolPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	public JCheckBox chckbxNewCheckBox;
	public JCheckBox chckbxNewCheckBox_1;

	public CardToolPanel() {
		setLayout(null);

		chckbxNewCheckBox = new JCheckBox("include DEBUG and DESCRIPTOR");
		chckbxNewCheckBox.setBounds(65, 44, 293, 23);
		chckbxNewCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int selected = chckbxNewCheckBox.isSelected() == true ? 1 : 0;
				Config.setValue("CardInfo", "includeDebug", Integer.toHexString(selected));
			}
		});
		chckbxNewCheckBox.setSelected(Integer.parseInt(Config.getValue("CardInfo", "includeDebug")) == 0 ? false : true);
		add(chckbxNewCheckBox);

		
		chckbxNewCheckBox_1 = new JCheckBox("拆分组件生成");
		chckbxNewCheckBox_1.setBounds(65, 82, 267, 23);
		chckbxNewCheckBox_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int selected = chckbxNewCheckBox_1.isSelected() == true ? 1 : 0;
				Config.setValue("CardInfo", "separateComponents", Integer.toHexString(selected));
			}
		});
		chckbxNewCheckBox_1.setSelected(Integer.parseInt(Config.getValue("CardInfo", "separateComponents")) == 0 ? false : true);
		add(chckbxNewCheckBox_1);
		
		textField = new JTextField();
		textField.setBounds(186, 126, 66, 22);
		textField.setText(Config.getValue("CardInfo", "cap2prg_commandlen").trim());
		add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("BLOCK SIZE:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(65, 125, 104, 22);
		add(lblNewLabel);
		
		JButton button = new JButton("保存");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.setValue("CardInfo", "cap2prg_commandlen", textField.getText().trim());
				JOptionPane.showMessageDialog(null, "保存成功！");
			}
		});
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setBounds(65, 171, 95, 25);
		add(button);
	}

	public void selectFile(JTextField target) {
		JFileChooser jFileChooser = new JFileChooser(getFile(target.getText().trim()));
		int ret = jFileChooser.showSaveDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
			target.setText(filePath);
		}
	}

	public File getFile(String name) {
		File file = new File(name);
		if (file.exists()) {
			if (file.isFile()) {
				file = file.getParentFile();
			}
		}
		return file;
	}
}
