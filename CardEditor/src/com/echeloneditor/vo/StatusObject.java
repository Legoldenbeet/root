package com.echeloneditor.vo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class StatusObject {
	private JLabel charNum;
	private JLabel fileSize;
	private JComboBox fileEncode;
	private JButton saveBtn;
	
	public void addItemAndSelected(String itemName,boolean removeOthers){
		if (removeOthers) {
			getFileEncode().removeAllItems();
		}
		getFileEncode().addItem(itemName);
		getFileEncode().setSelectedItem(itemName);
	}
	
	public void showFileSize(long size){
		getFileSize().setText("文件大小：" + size);
	}
	
	public void showSaveButton(boolean showBtn){
		getSaveBtn().setVisible(showBtn);
	}
	
	public void showCharNum(long num){
		getCharNum().setText("字符数："+num);
	}
	
	private JComboBox getFileEncode() {
		return fileEncode;
	}

	public void setFileEncode(JComboBox fileEncode) {
		this.fileEncode = fileEncode;
	}

	private JButton getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(JButton saveBtn) {
		this.saveBtn = saveBtn;
	}

	private JLabel getCharNum() {
		return charNum;
	}

	public void setCharNum(JLabel charNum) {
		this.charNum = charNum;
	}


	private JLabel getFileSize() {
		return fileSize;
	}

	public void setFileSize(JLabel fileSize) {
		this.fileSize = fileSize;
	}
}
