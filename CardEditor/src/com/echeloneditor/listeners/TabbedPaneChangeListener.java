package com.echeloneditor.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.main.CloseableTabComponent;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.Debug;
import com.echeloneditor.utils.SwingUtils;
import com.echeloneditor.vo.StatusObject;

public class TabbedPaneChangeListener implements MouseListener {
	public JPopupMenu jPopupMenu;

	private JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public static FileHander fileHander = null;

	public TabbedPaneChangeListener(final JTabbedPane tabbedPane, final StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;

		fileHander = new FileHander(tabbedPane, statusObject);

		jPopupMenu = new JPopupMenu();
		JMenuItem closeCurrent = new JMenuItem("关闭当前");
		closeCurrent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CloseableTabComponent closeableTabComponent = SwingUtils.getCloseableTabComponent(tabbedPane);
				String filePath = closeableTabComponent.getFilePath();
				FileHander.fileDescMapBean.remove(filePath);
				FileHander.currentCharPos = 0;
				FileHander.currentEncode = FileAction.DEFAULT_FILE_ENCODE;

				Debug.log.debug(FileHander.fileDescMapBean);

				tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
				if (tabbedPane.getTabCount() <= 0) {
					statusObject.showSaveButton(false);
					statusObject.reDefault();

					((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle(Config.getValue("CONFIG", "appName"));
				}
				SwingUtils.showTitleFilePath(tabbedPane);
			}
		});
		JMenuItem closeAll = new JMenuItem("关闭所有");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.removeAll();
				statusObject.showSaveButton(false);
				((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle(Config.getValue("CONFIG", "appName"));
			}
		});
		JMenuItem closeOther = new JMenuItem("关闭其他");
		closeOther.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = tabbedPane.getSelectedIndex();
				int count = tabbedPane.getTabCount();
				int left = selectIndex;
				int right = count - (selectIndex + 1);
				for (int i = 0; i < left; i++) {
					tabbedPane.remove(0);
					tabbedPane.repaint();
				}

				for (int j = 0; j < right; j++) {
					tabbedPane.remove(tabbedPane.getTabCount() - 1);
					tabbedPane.repaint();
				}
			}
		});
		jPopupMenu.add(closeCurrent);
		jPopupMenu.addSeparator();
		jPopupMenu.add(closeOther);
		jPopupMenu.addSeparator();
		jPopupMenu.add(closeAll);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// 最大化 功能
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 2) {
				JFrame frame = (JFrame) SwingUtilities.getRoot(e.getComponent());
				int state = frame.getExtendedState();
				if (state == JFrame.MAXIMIZED_BOTH) {
					frame.setExtendedState(JFrame.NORMAL);
				} else {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			}
		}
		// 状态栏
		int tabCount = tabbedPane.getTabCount();
		if (tabCount > 0) {
			CloseableTabComponent closeableTabComponent = SwingUtils.getCloseableTabComponent(tabbedPane);
			String encode = closeableTabComponent.getFileEncode();
			long fileSize = closeableTabComponent.getFileSzie();
			String filePath = closeableTabComponent.getFilePath();
			String fileNameExt = closeableTabComponent.getFileNameExt();
			long recordWhenOpenLastModiyTime = closeableTabComponent.getLastModifyTime();
			boolean modify = closeableTabComponent.isModify();

			if (fileSize >= 0) {
				filePath = filePath.isEmpty() ? "New File" + fileNameExt : filePath;
				((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle(filePath);

				statusObject.showFileSize(fileSize);
				statusObject.SelectEncodeItem(encode);
				statusObject.showCharNum(0);
				statusObject.showSaveButton(modify);

				boolean visible = fileSize > (FileAction.BIG_FILE_SIZE << 20) ? true : false;
				statusObject.showViewBtn(visible);

				if (recordWhenOpenLastModiyTime != -1) {
					if (new File(filePath).lastModified() != recordWhenOpenLastModiyTime) {
						int ret = JOptionPane.showConfirmDialog(null, "本地文档已经被修改，是否重新加载显示文档？", "本地文档被修改", JOptionPane.YES_NO_OPTION);
						if (ret == JOptionPane.YES_OPTION) {
							// 关闭当前文档
							tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
							// 重新打开文档
							fileHander.openFileWithFilePath(filePath, encode);
						}
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Component component = SwingUtilities.getRoot(e.getComponent());
			int frameX = component.getX();
			int frameY = component.getY();
			int eX = e.getXOnScreen();
			int eY = e.getYOnScreen();

			jPopupMenu.show((JFrame) component, eX - frameX, eY - frameY);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		// Debug.log.debug("mouseExited");
	}
}
