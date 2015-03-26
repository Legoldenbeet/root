package com.echeloneditor.main;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.vo.StatusObject;

public class CloseableTabComponent extends JPanel {
	private static final long serialVersionUID = 1L;

	public String filePath = "";
	public String fileEncode = FileAction.DEFAULT_FILE_ENCODE;
	public String fileNameExt=".txt";

	public long fileSzie = 0;
	public long lastModifyTime=-1;
	
	public boolean modify = false;

//	private static ImageIcon closerImage = ImageHelper.loadImage("closer.gif");
//	private static ImageIcon closerRolloverImage = ImageHelper.loadImage("closer_rollover.gif");
//	private static ImageIcon closerPressedImage = ImageHelper.loadImage("closer_pressed.gif");
	public JLabel titleLabel = null;
//	private JButton closeButton = null;
//	private JTabbedPane tabbedPane = null;

	public CloseableTabComponent(JTabbedPane aTabbedPane, final StatusObject statusObject) {
		super(new BorderLayout());
//		tabbedPane = aTabbedPane;

		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

		titleLabel = new JLabel("New File  ");
		titleLabel.setOpaque(false);
//		Dimension closerD = new Dimension(closerImage.getIconWidth(), closerImage.getIconHeight());
//
//		closeButton = new JButton(closerImage);
//		closeButton.setRolloverIcon(closerRolloverImage);
//		closeButton.setPressedIcon(closerPressedImage);
//		closeButton.setBorderPainted(false);
//		closeButton.setBorder(BorderFactory.createEmptyBorder());
//		closeButton.setFocusPainted(false);
//		closeButton.setRolloverEnabled(true);
//		closeButton.setOpaque(false);
//		closeButton.setContentAreaFilled(false);
//		closeButton.setPreferredSize(closerD);
//		closeButton.setSize(closerD);
//		closeButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				FileHander.fileDescMapBean.remove(getFilePath());
//				FileHander.currentCharPos = 0;
//				FileHander.currentEncode = FileAction.DEFAULT_FILE_ENCODE;
//
//				Debug.log.debug(FileHander.fileDescMapBean);
//
//				tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
//				if (tabbedPane.getTabCount() <= 0) {
//					statusObject.showSaveButton(false);
//					statusObject.reDefault();
//					
//					((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle(Config.getValue("CONFIG", "appName"));
//				}
//				SwingUtils.showTitleFilePath(tabbedPane);
//			}
//		});

		add(titleLabel, BorderLayout.CENTER);
//		add(closeButton, BorderLayout.EAST);
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
