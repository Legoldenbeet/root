package com.echeloneditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.echeloneditor.main.XFileSystemTree;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.vo.StatusObject;

/**
 * Action that opens the currently selected file.
 */
public class OpenExtAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	// private boolean newWindow;
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public XFileSystemTree fileSystemTree;
	public static FileHander fileHander;

	public OpenExtAction(JTabbedPane tabbedPane, StatusObject statusObject,XFileSystemTree fileSystemTree) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		this.fileSystemTree=fileSystemTree;
		fileHander = new FileHander(tabbedPane, statusObject);
		
		putValue(NAME, "打开(EX)");
		/*KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		putValue(ACCELERATOR_KEY, ks);*/
		
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		File file = fileSystemTree.getSelectedFile();
		String targetPath = FileAction.USER_DIR + "/" + Config.getValue("CONFIG", "debugPath") + "/" + file.getName() + ".txt";
		try {
			WindowsExcuter.excute(file.getParentFile(), "cmd.exe /c type " + file.getName() + " >\"" + targetPath + "\"",true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File targetFile = new File(targetPath);

		if (targetFile.isFile()&&targetFile.exists()) {
			fileHander.openFileWithFilePath(targetPath, FileAction.DEFAULT_FILE_ENCODE);
		}
	}

}