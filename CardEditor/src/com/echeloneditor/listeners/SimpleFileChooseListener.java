package com.echeloneditor.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.main.CloseableTabComponent;
import com.echeloneditor.os.OsConstants;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.SwingUtils;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.vo.StatusObject;

public class SimpleFileChooseListener implements ActionListener {
	// 选项卡
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public FileHander fileHander;

	public SimpleFileChooseListener(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
	}

	public void actionPerformed(ActionEvent e) {
		fileHander = new FileHander(tabbedPane, statusObject);
		String command = e.getActionCommand();

		JFileChooser fileChooser = new JFileChooser();
		if (command.equals("open")) {
			int ret = fileChooser.showOpenDialog(null);

			if (ret == JFileChooser.APPROVE_OPTION) {
				// 获得选择的文件
				File file = fileChooser.getSelectedFile();
				if (file.isFile()) {
					fileHander.openFileWithFilePath(file.getPath(), OsConstants.DEFAULT_FILE_ENCODE);
				}
			}
		} else if (command.equals("openext")) {
			int ret = fileChooser.showOpenDialog(null);

			if (ret == JFileChooser.APPROVE_OPTION) {
				// 获得选择的文件
				File file = fileChooser.getSelectedFile();
				String targetPath = OsConstants.DEFAULT_USER_DIR + "/" + Config.getValue("CONFIG", "debugPath") + "/" + file.getName() + ".txt";
				try {
					WindowsExcuter.excute(file.getParentFile(), "cmd.exe /c type " + file.getName() + " >\"" + targetPath + "\"",true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				File targetFile = new File(targetPath);

				if (targetFile.isFile()) {
					fileHander.openFileWithFilePath(targetPath, OsConstants.DEFAULT_FILE_ENCODE);
				}
			}
		} else if (command.equals("save")) {
			CloseableTabComponent closeableTabComponent = SwingUtils.getCloseableTabComponent(tabbedPane);
			String filePath = closeableTabComponent.getFilePath();
			String fileEncode = closeableTabComponent.getFileEncode();
			String fileNameExt = closeableTabComponent.getFileNameExt();
			String fileNameExtShort=fileNameExt.substring(fileNameExt.lastIndexOf(".")+1);
			int tabCount = tabbedPane.getTabCount();
			if (tabCount > 0) {

				if (filePath == null || filePath.equals("")) {
					FileNameExtensionFilter filter = new FileNameExtensionFilter(Config.getValue("FILE_TYPE", fileNameExtShort),fileNameExtShort);
					fileChooser.setFileFilter(filter);
					int ret = fileChooser.showSaveDialog(null);

					if (ret == JFileChooser.APPROVE_OPTION) {
						// 获得选择的文件
						File file = fileChooser.getSelectedFile();
						file=new File(file.getPath()+fileNameExt);
						if (file.exists()) {
							if (file.canWrite()) {
								Object[] options = { "<html>是&nbsp;(<u>Y</u>)</html>", "<html>否&nbsp;(<u>N</u>)</html>" };
								ret = JOptionPane.showOptionDialog(null, "文件已经存在，是否覆盖？", "信息框", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
								if (ret != JOptionPane.YES_OPTION) {
									return;
								}
							} else {
								JOptionPane.showMessageDialog(null, "文件已经存在，属性为只读，保存失败！");
								return;
							}
						}
						fileHander.saveFile(file.getPath(), fileEncode);
						tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
						fileHander.openFileWithFilePath(file.getPath(), "utf-8");
					}
				} else {
					File file = new File(filePath);
					if (file.canWrite()) {
						fileHander.saveFile(filePath, fileEncode);
						closeableTabComponent.setFileSzie(new File(filePath).length());
						closeableTabComponent.setLastModifyTime(file.lastModified());
						closeableTabComponent.setModify(false);
					} else {
						JOptionPane.showMessageDialog(null, "文件属性为只读，保存失败！");
						return;
					}

				}
				statusObject.showFileSize(closeableTabComponent.getFileSzie());
				statusObject.SelectEncodeItem(closeableTabComponent.getFileEncode());
			}

		} else if (command.equalsIgnoreCase("saveas")) {
			CloseableTabComponent closeableTabComponent = SwingUtils.getCloseableTabComponent(tabbedPane);
			String fileEncode = closeableTabComponent.getFileEncode();
			int ret = fileChooser.showSaveDialog(null);

			if (ret == JFileChooser.APPROVE_OPTION) {
				// 获得选择的文件
				File file = fileChooser.getSelectedFile();
				if (file.exists()) {
					Object[] options = { "<html>是&nbsp;(<u>Y</u>)</html>", "<html>否&nbsp;(<u>N</u>)</html>" };
					ret = JOptionPane.showOptionDialog(null, "文件已经存在，是否覆盖？", "信息框", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (ret != JOptionPane.YES_OPTION) {
						return;
					}
				}
				fileHander.saveFile(file.getPath(), fileEncode);
			}
		}
		statusObject.showSaveButton(false);
	}

}
