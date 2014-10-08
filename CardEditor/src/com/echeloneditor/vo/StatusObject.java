package com.echeloneditor.vo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.echeloneditor.actions.FileAction;
import com.watchdata.commons.lang.WDAssert;

public class StatusObject {
	private JLabel charNum;
	private JLabel fileSize;
	private JComboBox fileEncode;
	private JButton saveBtn;

	private JButton nextBtn;
	private JButton prevBtn;
	private JButton lastBtn;
	private JButton firstBtn;

	public void reDefault(){
		SelectEncodeItem(FileAction.DEFAULT_FILE_ENCODE);
		showFileSize(0);
		showCharNum(0);
	}
	public void showViewBtn(boolean visible) {
		getNextBtn().setVisible(visible);
		getPrevBtn().setVisible(visible);
		getLastBtn().setVisible(visible);
		getFirstBtn().setVisible(visible);
	}

	public void SelectEncodeItem(String itemName) {
		getFileEncode().setSelectedItem(itemName);
	}
	public String getSelectedEncodeItem() {
		String item=getFileEncode().getSelectedItem().toString();
		return WDAssert.isEmpty(item)==false?item:null;
	}

	public void showFileSize(long size) {
		getFileSize().setText("文件大小：" + size);
	}

	public void showSaveButton(boolean showBtn) {
		getSaveBtn().setEnabled(showBtn);
	}

	public void showCharNum(long num) {
		getCharNum().setText("字符数：" + num);
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

	private JButton getNextBtn() {
		return nextBtn;
	}

	public void setNextBtn(JButton nextBtn) {
		this.nextBtn = nextBtn;
	}

	private JButton getPrevBtn() {
		return prevBtn;
	}

	public void setPrevBtn(JButton prevBtn) {
		this.prevBtn = prevBtn;
	}

	private JButton getLastBtn() {
		return lastBtn;
	}

	public void setLastBtn(JButton lastBtn) {
		this.lastBtn = lastBtn;
	}

	private JButton getFirstBtn() {
		return firstBtn;
	}

	public void setFirstBtn(JButton firstBtn) {
		this.firstBtn = firstBtn;
	}

}
