package com.echeloneditor.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import com.echeloneditor.actions.FileHander;
import com.echeloneditor.vo.StatusObject;

public class SimpleJmenuItemListener implements ActionListener {
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public FileHander fileHander;

	public SimpleJmenuItemListener(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		fileHander = new FileHander(tabbedPane, statusObject);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command=e.getActionCommand();
		if (command.equalsIgnoreCase("new")) {
			String fileExt=((JMenuItem)e.getSource()).getText().trim();
			fileHander.newFile(fileExt);
		}
	}

}
