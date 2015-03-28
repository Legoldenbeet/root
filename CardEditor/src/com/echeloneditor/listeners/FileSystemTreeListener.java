package com.echeloneditor.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.fife.ui.rtextfilechooser.FileSystemTree;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.vo.StatusObject;

public class FileSystemTreeListener implements MouseListener,KeyListener {
	public static FileHander fileHander;
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;

	public FileSystemTreeListener(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;

		fileHander = new FileHander(tabbedPane, statusObject);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 2) {
				open((FileSystemTree)e.getComponent());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			open((FileSystemTree)e.getComponent());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void open(FileSystemTree fileSystemTree){
		File selectedFile = fileSystemTree.getSelectedFile();
		if (selectedFile.exists() && selectedFile.isFile() && selectedFile.canRead()) {
			fileHander.openFileWithFilePath(selectedFile.getPath(), FileAction.DEFAULT_FILE_ENCODE);
		}
	}
}
