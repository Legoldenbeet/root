package com.echeloneditor.main;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.vo.StatusObject;

public class CloseableTabComponent extends JPanel {
	private static final long serialVersionUID = 1L;

	public String filePath = "";
	public String fileEncode = FileAction.DEFAULT_FILE_ENCODE;
	public String fileNameExt = ".txt";

	public long fileSzie = 0;
	public long lastModifyTime = -1;

	public boolean modify = false;
	public JLabel titleLabel = null;

	public CloseableTabComponent(JTabbedPane aTabbedPane, final StatusObject statusObject) {
		super(new BorderLayout());
		setOpaque(false);
		titleLabel = new JLabel("New File  ");
		titleLabel.setFont(new Font("宋体", Font.PLAIN, 12));
		titleLabel.setOpaque(false);
		add(titleLabel, BorderLayout.CENTER);
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileEncode() {
		return fileEncode;
	}

	public void setFileEncode(String fileEncode) {
		this.fileEncode = fileEncode;
	}

	public long getFileSzie() {
		return fileSzie;
	}

	public void setFileSzie(long fileSzie) {
		this.fileSzie = fileSzie;
	}

	public long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getFileNameExt() {
		return fileNameExt;
	}

	public void setFileNameExt(String fileNameExt) {
		this.fileNameExt = fileNameExt;
	}
}
