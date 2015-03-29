package com.echeloneditor.main;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.fife.ui.RScrollPane;
import org.fife.ui.dockablewindows.DockableWindowScrollPane;
import org.fife.ui.rtextfilechooser.FileSystemTree;

import com.echeloneditor.actions.OpenAction;
import com.echeloneditor.vo.StatusObject;

public class XFileSystemTree extends FileSystemTree {
	private static final long serialVersionUID = 1L;
	private OpenAction openAction;
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	
	public XFileSystemTree(){
		
	}

	public XFileSystemTree(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		openAction = new OpenAction(tabbedPane, statusObject, this);
	}

	@Override
	protected JPopupMenu createPopupMenu() {

		JPopupMenu popup = super.createPopupMenu();

		popup.insert(new JMenuItem(openAction), 0);
		popup.insert(new JPopupMenu.Separator(), 1);

		// Re-do this to set orientation for new menu items.
		popup.applyComponentOrientation(getComponentOrientation());
		return popup;
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