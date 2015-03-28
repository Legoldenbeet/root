/*
 * 09/22/2012
 *
 * Tree.java - The extended file system tree used by this plugin.
 * Copyright (C) 2012 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package com.echeloneditor.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.fife.ui.RScrollPane;
import org.fife.ui.dockablewindows.DockableWindowScrollPane;
import org.fife.ui.rtextfilechooser.FileSystemTree;

public class XFileSystemTree extends FileSystemTree {
	private static final long serialVersionUID = 1L;

	public XFileSystemTree() {
	}

	public static void main(String[] args) {
		FileSystemTree fileSystemTree = new FileSystemTree();
		fileSystemTree.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() == 2) {
						FileSystemTree fileSystemTree = (FileSystemTree) e.getComponent();
						JOptionPane.showMessageDialog(null, fileSystemTree.getSelectedFileName());
					}
				}
			}
		});
		RScrollPane scrollPane = new DockableWindowScrollPane(fileSystemTree);
		JFrame frame = new JFrame();
		frame.getContentPane().add(scrollPane);
		frame.setSize(400, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}