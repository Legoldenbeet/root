/*
 * 09/22/2012
 *
 * Tree.java - The extended file system tree used by this plugin.
 * Copyright (C) 2012 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package com.echeloneditor.filesystemtree;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.fife.ui.RScrollPane;
import org.fife.ui.app.GUIApplication;
import org.fife.ui.app.StandardAction;
import org.fife.ui.dockablewindows.DockableWindowScrollPane;
import org.fife.ui.rtextfilechooser.FileSystemTree;

/**
 * The extended file system tree used by this plugin.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class XFileSystemTree extends FileSystemTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OpenAction openAction;
	private OpenAction openInNewWindowAction;
	private GoIntoAction goIntoAction;

	private static final String MSG = "org.fife.rtext.plugins.filesystemtree.PopupMenu";

	/**
	 * Constructor.
	 *
	 * @param plugin
	 *            The plugin.
	 */
	public XFileSystemTree() {

		Listener listener = new Listener();
		addMouseListener(listener);
		addPropertyChangeListener(listener);

		// Add a needed extra bit of space at the top.
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0), getBorder()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configurePopupMenuActions() {

		super.configurePopupMenuActions();
		File selected = getSelectedFile();

		boolean enable = selected != null && selected.isFile();
		openAction.setEnabled(enable);
		openInNewWindowAction.setEnabled(enable);

		goIntoAction.setEnabled(selected != null && selected.isDirectory());

	}

	/**
	 * Creates actions for the file system tree that this plugin adds on top of the defaults.
	 */
	private void createPluginSpecificActions() {
		ResourceBundle msg = ResourceBundle.getBundle(MSG);
		openAction = new OpenAction(msg.getString("Open"), false);
		openInNewWindowAction = new OpenAction(msg.getString("OpenInNewWindow"), true);
		// goIntoAction = new GoIntoAction(null/*plugin.getRText()*/, msg);
	}

	/**
	 * Creates the popup menu for this file system tree. Subclasses can override this method if they wish to add more menu items to the popup menu.
	 *
	 * @return The popup menu for this file system tree.
	 */
	@Override
	protected JPopupMenu createPopupMenu() {

		JPopupMenu popup = super.createPopupMenu();

		popup.insert(new JMenuItem(openAction), 0);
		popup.insert(new JMenuItem(openInNewWindowAction), 1);

		popup.insert(new JMenuItem(goIntoAction), 4);
		popup.insert(new JPopupMenu.Separator(), 5);

		// Re-do this to set orientation for new menu items.
		popup.applyComponentOrientation(getComponentOrientation());
		return popup;

	}

	/**
	 * If a file is selected in the file system tree, it is opened in RText.
	 */
	private void doOpenFile() {
		File file = getSelectedFile();
		if (file != null) {
			// We'll make sure the file exists and is a regular file
			// (as opposed to a directory) before attempting to open it.
			if (file.isFile()) {
				// AbstractMainView mainView = plugin.getRText().getMainView();
				// null encoding means check for Unicode first, and
				// if it isn't, use system default encoding.
				// mainView.openFile(file.getAbsolutePath(), null, true);
			}
		}
	}

	/**
	 * Overridden to install our extra actions.
	 */
	@Override
	protected void installKeyboardActions() {

		super.installKeyboardActions();

		InputMap im = getInputMap();
		ActionMap am = getActionMap();
		createPluginSpecificActions();

		// Enter => open the file in RText.
		im.put((KeyStroke) openAction.getValue(Action.ACCELERATOR_KEY), "OnEnter");
		am.put("OnEnter", openAction);

	}

	/**
	 * Makes this tree drill down into the selected folder.
	 */
	private class GoIntoAction extends StandardAction {

		/*
		 * public GoIntoAction(RSyntaxTextArea app, ResourceBundle msg) { //super(app, msg, "Action.GoInto"); }
		 */

		public GoIntoAction(GUIApplication app) {
			super(app);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			File file = getSelectedFile();
			if (file != null && file.isDirectory()) {
			} else { // Should never happen
				UIManager.getLookAndFeel().provideErrorFeedback(XFileSystemTree.this);
			}
		}

	}

	/**
	 * Listens for events in this tree.
	 */
	private class Listener extends MouseAdapter implements PropertyChangeListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				doOpenFile();
			}
		}

		public void propertyChange(PropertyChangeEvent e) {
			String name = e.getPropertyName();
			if (name.equals(FileSystemTree.WILL_EXPAND_PROPERTY)) {
//				plugin.getRText().setCursor(Cursor.
//							getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			else if (name.equals(FileSystemTree.EXPANDED_PROPERTY)) {
//				plugin.getRText().setCursor(Cursor.
//							getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	/**
	 * Action that opens the currently selected file.
	 */
	private class OpenAction extends AbstractAction {

		private boolean newWindow;

		public OpenAction(String name, boolean newWindow) {
			putValue(NAME, name);
			this.newWindow = newWindow;
			if (!newWindow) {
				KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
				putValue(ACCELERATOR_KEY, ks);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (newWindow) {
				// Create a new RText window.
//				RText r = new RText(null, new RTextPrefs().populate(plugin.getRText()));
//				StoreKeeper.addRTextInstance(r);
				String file = getSelectedFileName();
				// Open the new RText's file chooser. Do this in an
				// invokeLater() call as RText's constructor leaves some
				// stuff to do via invokeLater() as well, and we must wait
				// for this stuff to complete before we can continue (e.g.
				// RText's "working directory" must be set).
//				SwingUtilities.invokeLater(new OpenInNewWindowRunnable(r, file));
			} else {
				doOpenFile();
			}
		}

	}

//	/**
//	 * Adds an old text file to an RText instance.
//	 */
//	private class OpenInNewWindowRunnable implements Runnable {
//
//		private RText rtext;
//		private String file;
//
//		public OpenInNewWindowRunnable(RText rtext, String file) {
//			this.rtext = rtext;
//			this.file = file;
//		}
//
//		public void run() {
//			AbstractMainView mainView = rtext.getMainView();
//			mainView.openFile(file, null);
//		}
//
//	}
//	private RText owner;
	private String name;
	private static XFileSystemTree tree;
	
public static void main(String[] args) {
	tree = new XFileSystemTree();
	RScrollPane scrollPane = new DockableWindowScrollPane(tree);
	
	JFrame frame=new JFrame();
	frame.getContentPane().add(scrollPane);
	frame.setSize(400, 1000);
	frame.show();
}
}