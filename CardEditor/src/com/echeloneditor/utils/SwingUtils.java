package com.echeloneditor.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.echeloneditor.main.CardEditor;
import com.echeloneditor.main.CloseableTabComponent;
import com.watchdata.commons.lang.WDAssert;

public class SwingUtils {
	private static final Logger log = Logger.getLogger(SwingUtils.class);

	public static void push2Clipboard(String content) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
	}

	public static void push2Clipboard(final File file) {
		if (file.exists() && file.canRead()) {
			Transferable content = new Transferable() {
				DataFlavor[] dataFlavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					for (int i = 0; i < dataFlavors.length; i++) {
						if (dataFlavors[i].equals(flavor)) {
							return true;
						}
					}

					return false;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					// TODO Auto-generated method stub
					return dataFlavors;
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					List<File> fileList = new ArrayList<File>();
					fileList.add(file);
					return fileList;
				}
			};
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(content, null);
		} else {
			JOptionPane.showMessageDialog(null, "权限不足，无法操作！");
		}
	}

	public static void showTitleFilePath(JTabbedPane tabbedPane) {
		int tabCount = tabbedPane.getTabCount();
		if (tabCount > 0) {
			CloseableTabComponent closeableTabComponent = getCloseableTabComponent(tabbedPane);
			if (closeableTabComponent == null) {
				return;
			}
			String filePath = closeableTabComponent.getFilePath();
			((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle(filePath);
			CardEditor.xFileSystemTree.setSelectedFile(new File(filePath));
		}
	}

	public static RTextScrollPane getExistComponent(JTabbedPane tabbedPane, String filePath) {
		RTextScrollPane com = null;
		int tabCount = tabbedPane.getTabCount();
		if (tabCount > 0) {
			for (int i = 0; i < tabCount; i++) {
				Component component = tabbedPane.getTabComponentAt(i);
				if (component instanceof CloseableTabComponent) {
					if (((CloseableTabComponent) component).getFilePath().equals(filePath)) {
						com = (RTextScrollPane) tabbedPane.getComponentAt(i);
					}
				}
			}
		}
		return com;
	}

	/**
	 * get the select closeableTabComponent use the indicate by the tabbedpane
	 * 
	 * @param tabbedPane
	 * @return
	 */
	public static CloseableTabComponent getCloseableTabComponent(JTabbedPane tabbedPane) {
		Component component = tabbedPane.getTabComponentAt(tabbedPane.getSelectedIndex());
		return (CloseableTabComponent) component;
	}

	/**
	 * get columnHeader with the textcomponent
	 * 
	 * @param editorComponent
	 * @return
	 */
	public static Component getColumnHeader(Component editorComponent) {
		Component target = null;
		if (editorComponent instanceof RSyntaxTextArea) {
			JScrollPane scrollPane = getScrollPane((RSyntaxTextArea) editorComponent);
			JViewport jViewport = scrollPane.getColumnHeader();
			target = jViewport.getView();
		}
		return target;
	}

	public static Component getRowHeader(Component editorComponent) {
		Component target = null;
		if (editorComponent instanceof RSyntaxTextArea) {
			JScrollPane scrollPane = getScrollPane((RSyntaxTextArea) editorComponent);
			JViewport jViewport = scrollPane.getRowHeader();
			target = jViewport.getView();
		}
		return target;
	}

	/**
	 * 获取选项卡选中的RSyntaxTextArea
	 * 
	 * @param tabbedPane
	 * @return
	 */
	public static RSyntaxTextArea getRSyntaxTextArea(JTabbedPane tabbedPane) {
		RSyntaxTextArea rSyntaxTextArea = null;
		if (tabbedPane.getSelectedIndex() == -1) {
			return null;
		}
		// System.out.println(tabbedPane.getSelectedIndex());
		Component com = tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
		if (com instanceof JScrollPane) {
			Component[] component = ((JScrollPane) com).getComponents();
			if (component[0] instanceof RSyntaxTextArea) {
				rSyntaxTextArea = (RSyntaxTextArea) component[0];
			} else if (component[0] instanceof JViewport) {
				Component[] component2 = ((JViewport) component[0]).getComponents();
				if (component2[0] instanceof RSyntaxTextArea) {
					rSyntaxTextArea = (RSyntaxTextArea) component2[0];
				}
			}
		}
		return rSyntaxTextArea;
	}

	/**
	 * Get the JscrollPane that contains this EditorPane, or null if no JScrollPane is the parent of this editor
	 * 
	 * @param editorPane
	 * @return
	 */
	public static JScrollPane getScrollPane(JTextComponent editorPane) {
		JScrollPane jScrollPane = null;
		Container p = editorPane.getParent();
		if (p != null) {
			jScrollPane = (p instanceof JScrollPane) ? (JScrollPane) p : (JScrollPane) p.getParent();
		}
		return jScrollPane;
	}

	/**
	 * new a RSyntaxTextArea editor
	 * 
	 * @return
	 */
	public static RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(25, 70);
		textArea.setCaretPosition(0);
		textArea.setWrapStyleWord(true);
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setClearWhitespaceLinesEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		return textArea;
	}

	/**
	 * 设置焦点选项卡的标题
	 */
	public static void setTabbedPaneTitle(JTabbedPane tabbedPane, String fileName) {
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fileName);
		Component componentl = tabbedPane.getTabComponentAt(tabbedPane.getSelectedIndex());
		((CloseableTabComponent) componentl).titleLabel.setText(fileName + "  ");
	}

	/**
	 * 获取select编辑区内容
	 * 
	 * @param tabbedPane
	 * @return
	 */
	public static String getContent(JTabbedPane tabbedPane) {
		String text = "";
		RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
		if (rSyntaxTextArea != null) {
			text = rSyntaxTextArea.getText();
		}
		return text;
	}

	/**
	 * 设置编辑区内容
	 * 
	 * @param tabbedPane
	 * @param text
	 */
	public static void setContent(JTabbedPane tabbedPane, String text) {
		SwingUtils.getRSyntaxTextArea(tabbedPane).setText(text);
	}

	/**
	 * 根据文件扩展名获取文件的内容类型
	 * 
	 * @param file
	 */
	public static String getFileContentType(String fileName) {
		String result = "";

		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			String fileExt = fileName.substring(pos + 1, fileName.length());
			result = Config.getValue("FILE_TYPE", fileExt);
		}

		if (WDAssert.isEmpty(result)) {
			result = "text/plain";
		}

		log.debug("syntaxstyle:" + result);
		return result;
	}

	public static void updateUI() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Window windows[] = Window.getWindows();
				for (int i = 0; i < windows.length; i++) {
					if (windows[i].isDisplayable()) {
						SwingUtilities.updateComponentTreeUI(windows[i]);
					}
				}
			}
		});
	}

	public static void restart(String appName) throws IOException {
		// 用一条指定的命令去构造一个进程生成器
		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "java", "-jar", appName + ".jar");
		// 让这个进程的工作区空间改为F:\dist
		// 这样的话,它就会去F:\dist目录下找Test.jar这个文件
		// pb.directory(new File("F:\\dist"));
		// 得到进程生成器的环境 变量,这个变量我们可以改,
		// 改了以后也会反应到新起的进程里面去
		// Map<String, String> map = pb.environment();
		Process p = pb.start();
		// 然后就可以对p做自己想做的事情了
		// 自己这个时候就可以退出了
		System.exit(0);
	}

	public static void setLookAndFeelFont(Font commonFont) {
		// Font commonFont = new Font("微软雅黑", Font.PLAIN, 12);
		// Font titleFont = new Font("微软雅黑", Font.PLAIN, 14);
		UIManager.getDefaults().put("CheckBox.font", commonFont);
		UIManager.getDefaults().put("Tree.font", commonFont);
		UIManager.getDefaults().put("Viewport.font", commonFont);
		UIManager.getDefaults().put("ProgressBar.font", commonFont);
		UIManager.getDefaults().put("RadioButtonMenuItem.font", commonFont);
		UIManager.getDefaults().put("FormattedTextField.font", commonFont);
		UIManager.getDefaults().put("ToolBar.font", commonFont);
		UIManager.getDefaults().put("ColorChooser.font", commonFont);
		UIManager.getDefaults().put("ToggleButton.font", commonFont);
		UIManager.getDefaults().put("Panel.font", commonFont);
		UIManager.getDefaults().put("TextArea.font", commonFont);
		UIManager.getDefaults().put("Menu.font", commonFont);
		UIManager.getDefaults().put("RadioButtonMenuItem.acceleratorFont", commonFont);
		UIManager.getDefaults().put("Spinner.font", commonFont);
		UIManager.getDefaults().put("Menu.acceleratorFont", commonFont);
		UIManager.getDefaults().put("CheckBoxMenuItem.acceleratorFont", commonFont);
		UIManager.getDefaults().put("TableHeader.font", commonFont);
		UIManager.getDefaults().put("TextField.font", commonFont);
		UIManager.getDefaults().put("OptionPane.font", commonFont);
		UIManager.getDefaults().put("MenuBar.font", commonFont);
		UIManager.getDefaults().put("Button.font", commonFont);
		UIManager.getDefaults().put("Label.font", commonFont);
		UIManager.getDefaults().put("PasswordField.font", commonFont);
		UIManager.getDefaults().put("InternalFrame.titleFont", commonFont);
		UIManager.getDefaults().put("OptionPane.buttonFont", commonFont);
		UIManager.getDefaults().put("ScrollPane.font", commonFont);
		UIManager.getDefaults().put("MenuItem.font", commonFont);
		UIManager.getDefaults().put("ToolTip.font", commonFont);
		UIManager.getDefaults().put("List.font", commonFont);
		UIManager.getDefaults().put("OptionPane.messageFont", commonFont);
		UIManager.getDefaults().put("EditorPane.font", commonFont);
		UIManager.getDefaults().put("Table.font", commonFont);
		UIManager.getDefaults().put("TabbedPane.font", commonFont);
		UIManager.getDefaults().put("RadioButton.font", commonFont);
		UIManager.getDefaults().put("CheckBoxMenuItem.font", commonFont);
		UIManager.getDefaults().put("TextPane.font", commonFont);
		UIManager.getDefaults().put("PopupMenu.font", commonFont);
		UIManager.getDefaults().put("TitledBorder.font", commonFont);
		UIManager.getDefaults().put("ComboBox.font", commonFont);
	}

	/**
	 * setTheme
	 * 
	 * @param lafIndex
	 * @param theme
	 */
	public static void setTheme(int lafIndex, String theme) {
		try {
			switch (lafIndex) {
			case 0:
				com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme(theme);
				break;
			case 1:
				com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme(theme);
				break;
			case 2:
				com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme(theme);
				break;
			case 3:
				com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme(theme);
				break;
			case 4:
				com.jtattoo.plaf.fast.FastLookAndFeel.setTheme(theme);
				break;
			case 5:
				com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme(theme);
				break;
			case 6:
				com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme(theme);
				break;
			case 7:
				com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme(theme);
				break;
			case 8:
				com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme(theme);
				break;
			case 9:
				com.jtattoo.plaf.mint.MintLookAndFeel.setTheme(theme);
				break;
			case 10:
				com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme(theme);
				break;
			case 11:
				com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme(theme);
				break;
			case 12:
				com.jtattoo.plaf.texture.TextureLookAndFeel.setTheme(theme);
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
