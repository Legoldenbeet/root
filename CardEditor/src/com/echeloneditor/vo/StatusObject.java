package com.echeloneditor.vo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class StatusObject {
	JLabel charNum;
	JComboBox fileEncode;
	public JComboBox getFileEncode() {
		return fileEncode;
	}

	public void setFileEncode(JComboBox fileEncode) {
		this.fileEncode = fileEncode;
	}

	JLabel fileSize;
	JButton saveBtn;

	public JButton getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(JButton saveBtn) {
		this.saveBtn = saveBtn;
	}

	public JLabel getCharNum() {
		return charNum;
	}

	public void setCharNum(JLabel charNum) {
		this.charNum = charNum;
	}


	public JLabel getFileSize() {
		return fileSize;
	}

	public void setFileSize(JLabel fileSize) {
		this.fileSize = fileSize;
	}
}
