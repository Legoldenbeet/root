package com.echeloneditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.echeloneditor.main.XFileSystemTree;
import com.echeloneditor.vo.StatusObject;

/**
 * Action that opens the currently selected file.
 */
public class OpenAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	// private boolean newWindow;
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public XFileSystemTree fileSystemTree;
	public static FileHander fileHander;

	public OpenAction(JTabbedPane tabbedPane, StatusObject statusObject,XFileSystemTree fileSystemTree) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		this.fileSystemTree=fileSystemTree;
		fileHander = new FileHander(tabbedPane, statusObject);
		
		putValue(NAME, "打开");
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		putValue(ACCELERATOR_KEY, ks);
	}

	public void actionPerformed(ActionEvent e) {
		fileHander.openFileWithFilePath(fileSystemTree.getSelectedFileName(), FileAction.DEFAULT_FILE_ENCODE);
	}

}