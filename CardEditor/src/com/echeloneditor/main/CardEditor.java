package com.echeloneditor.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.ui.RScrollPane;
import org.fife.ui.dockablewindows.DockableWindowScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.echeloneditor.actions.DecryptTemplateAction;
import com.echeloneditor.actions.EmvFormatAction;
import com.echeloneditor.actions.FileAction;
import com.echeloneditor.actions.FileHander;
import com.echeloneditor.actions.FindAndReplaceAction;
import com.echeloneditor.actions.XmlPreettifyAction;
import com.echeloneditor.listeners.EditorPaneListener;
import com.echeloneditor.listeners.FileSystemTreeListener;
import com.echeloneditor.listeners.FindDialogListener;
import com.echeloneditor.listeners.SimpleDragFileListener;
import com.echeloneditor.listeners.SimpleFileChooseListener;
import com.echeloneditor.listeners.SimpleJmenuItemListener;
import com.echeloneditor.listeners.TabbedPaneChangeListener;
import com.echeloneditor.os.JButton;
import com.echeloneditor.os.OsConstants;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.FileUtil;
import com.echeloneditor.utils.FontUtil;
import com.echeloneditor.utils.ImageHelper;
import com.echeloneditor.utils.SwingUtils;
import com.echeloneditor.utils.WindowsExcuter;
import com.echeloneditor.utils.ZipUtil;
import com.echeloneditor.vo.StatusObject;
import com.sepp.service.Sepp;
import com.sepp.service.SeppImpl;
import com.watchdata.Generater;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDByteUtil;

public class CardEditor {
	private static Logger log = Logger.getLogger(CardEditor.class);
	public static JFrame frmEcheloneditor;
	public static JTabbedPane tabbedPane;
	public static StatusObject statusObject;
	public static XFileSystemTree xFileSystemTree;
	public FontWidthRuler ruler;

	FindDialog findDialog = null;
	ReplaceDialog replaceDialog = null;
	public static AssistantToolDialog dialog = null;

	public JSplitPane centerSplitPaneH;
	public JSplitPane centerSplitPaneV;
	public static FileHander fileHander;
	public FileInputStream fis;

	public static Thread sysThread;

	/**
	 * Create the application.
	 */
	public CardEditor() {
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		startApp(args);
	}

	public static void startApp(final String[] args) {
		try {
			// 设置皮肤
			String currentLaf = Config.getValue("CURRENT_THEME", "current_laf");
			String currentTheme = Config.getValue("CURRENT_THEME", "current_theme");
			String lafIndex = Config.getValue("CURRENT_THEME", "current_lafIndex");

			if (!currentTheme.equals(OsConstants.OS)) {
				SwingUtils.setTheme(Integer.parseInt(lafIndex), currentTheme);
			}

			UIManager.setLookAndFeel(currentLaf);
			JFrame.setDefaultLookAndFeelDecorated(true);

//			Font commonFont = new Font("微软雅黑", Font.PLAIN, 14);
//			SwingUtils.setLookAndFeelFont(commonFont);
			SwingUtils.updateUI();
		} catch (Exception e) {
			log.error("LookAndFeel unsupported.");
		}
		// 初始化窗体
		CardEditor cardEditor = new CardEditor();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(d);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(frmEcheloneditor.getGraphicsConfiguration());

		bounds.x = insets.left;
		bounds.y = insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		frmEcheloneditor.setBounds(bounds);

		// 框体屏幕居中显示
		frmEcheloneditor.setLocationRelativeTo(null);
		// GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getDefaultScreenDevice();
		// gd.setFullScreenWindow(frmEcheloneditor);
		// frmEcheloneditor.setUndecorated(true);;
		// 显示窗体
		frmEcheloneditor.setVisible(true);
		cardEditor.centerSplitPaneH.setDividerLocation(0.2);
		cardEditor.centerSplitPaneV.setDividerLocation(0.95);

		new DropTarget(frmEcheloneditor, DnDConstants.ACTION_COPY_OR_MOVE, new SimpleDragFileListener(tabbedPane, statusObject), true);

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				fileHander.openFileWithFilePath(args[i], OsConstants.DEFAULT_FILE_ENCODE);
			}
		}
		// 启动同步接收和发送服务
		new SeppImpl(tabbedPane, statusObject).start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// 主窗体声明及初始化
		frmEcheloneditor = new JFrame();
		frmEcheloneditor.setIconImage(ImageHelper.loadImage("logo.png").getImage());
		frmEcheloneditor.setTitle(Config.getValue("CONFIG", "appName"));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = d.getWidth();
		double screenHeight = d.getHeight();

		if (screenWidth > 1024) {
			screenWidth = 1024;
		} else {
			screenWidth = screenWidth - 50;
		}
		if (screenHeight > 768) {
			screenHeight = 768;
		} else {
			screenHeight = screenHeight - 50;
		}
		frmEcheloneditor.setSize((int) screenWidth, (int) screenHeight);

		frmEcheloneditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 窗口容器
		Container container = frmEcheloneditor.getContentPane();

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		FlowLayout fl_statusPanel = (FlowLayout) statusPanel.getLayout();
		fl_statusPanel.setAlignment(FlowLayout.RIGHT);
		// frmEcheloneditor.getContentPane().add(statusPanel, BorderLayout.SOUTH);

		JButton button_send = new JButton("Sepp：");
		button_send.setVisible(false);
		button_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				String filePath = SwingUtils.getCloseableTabComponent(tabbedPane).getFilePath();
				try {
					Sepp sepp=new SeppImpl(tabbedPane, statusObject);
					sepp.sendFile(Sepp.INS_TRANSFER_OPEN,new File(filePath), Config.getValue("FREND_LIST", statusObject.getSelectedSeppTartgetItem().trim()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		statusPanel.add(button_send);

		JComboBox comboBox_friend = new JComboBox();
		comboBox_friend.setVisible(false);
		comboBox_friend.setModel(new DefaultComboBoxModel(Config.getItems("FREND_LIST").toArray()));
		statusPanel.add(comboBox_friend);

		JButton button_5 = new JButton("第一页");
		button_5.setVisible(false);
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				open(tabbedPane, -1, false, true);
			}
		});
		statusPanel.add(button_5);

		JButton button_4 = new JButton("上一页");
		button_4.setVisible(false);
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				open(tabbedPane, OsConstants.BIG_FILE_READ_UNIT_SIZE, false, false);
			}
		});
		statusPanel.add(button_4);

		JButton button_2 = new JButton("下一页");
		button_2.setVisible(false);
		statusPanel.add(button_2);

		JButton button_6 = new JButton("最后一页");
		button_6.setVisible(false);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				open(tabbedPane, -1, true, true);
			}
		});
		statusPanel.add(button_6);

		JLabel charNmLabel = new JLabel("字符数：");
		charNmLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		statusPanel.add(charNmLabel);

		JLabel fileSizeLabel = new JLabel("文件大小：");
		fileSizeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		statusPanel.add(fileSizeLabel);

		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(Charset.availableCharsets().keySet().toArray()));
		statusPanel.add(comboBox);

		statusObject = new StatusObject();
		statusObject.setCharNum(charNmLabel);
		statusObject.setFileSize(fileSizeLabel);
		statusObject.setFileEncode(comboBox);

		statusObject.setNextBtn(button_2);
		statusObject.setPrevBtn(button_4);
		statusObject.setLastBtn(button_6);
		statusObject.setFirstBtn(button_5);
		statusObject.setBtn_send(button_send);
		statusObject.setJcb_friend(comboBox_friend);

		JButton btnNewButton_2 = new JButton("reload");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() > 0) {
					String newEncode = statusObject.getSelectedEncodeItem();
					if (newEncode == null) {
						return;
					}
					String filePath = SwingUtils.getCloseableTabComponent(tabbedPane).getFilePath();
					tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
					fileHander.openFileWithFilePath(filePath, newEncode);
					statusObject.SelectEncodeItem(newEncode);
				}
			}
		});
		statusPanel.add(btnNewButton_2);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addMouseListener(new TabbedPaneChangeListener(tabbedPane, statusObject));
		fileHander = new FileHander(tabbedPane, statusObject);

		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				open(tabbedPane, 0, true, false);
			}
		});
		JPanel panel_1 = new JPanel();
		frmEcheloneditor.getContentPane().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		//toolBar.setFloatable(false);
		panel_1.add(toolBar, BorderLayout.NORTH);

		JButton button = new JButton();
		button.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112619422_easyicon_net_24.png")));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileHander.newFile(".txt");
			}
		});
		toolBar.add(button);

		JButton button_1 = new JButton();
		button_1.setActionCommand("open");
		button_1.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));
		button_1.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112148516_easyicon_net_24.png")));
		toolBar.add(button_1);

		JButton btnNewButton = new JButton();
		btnNewButton.setActionCommand("save");
		btnNewButton.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504114455808_easyicon_net_24.png")));
		toolBar.add(btnNewButton);
		statusObject.setSaveBtn(btnNewButton);
		btnNewButton.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));
		btnNewButton.setEnabled(false);

		JButton button_3 = new JButton("");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String filepath = SwingUtils.getCloseableTabComponent(tabbedPane).getFilePath();
				try {
					File file = new File(filepath);
					fis = new FileInputStream(file);

					byte[] bytes = new byte[fis.available()];

					fis.read(bytes);

					RSyntaxTextArea textArea = SwingUtils.createTextArea();

					textArea.setSyntaxEditingStyle("text/plain");

					EditorPaneListener editlistener = new EditorPaneListener(tabbedPane, statusObject);
					textArea.addMouseListener(editlistener);
					textArea.addMouseMotionListener(editlistener);
					textArea.addKeyListener(editlistener);
					textArea.getDocument().addDocumentListener(editlistener);

					RTextScrollPane sp = new RTextScrollPane(textArea);
					sp.setFoldIndicatorEnabled(true);

					Gutter gutter = sp.getGutter();
					gutter.setBookmarkingEnabled(true);
					ImageIcon ii = ImageHelper.loadImage("bookmark.png");
					gutter.setBookmarkIcon(ii);

					// 加入标尺
					FontWidthRuler ruler = new FontWidthRuler(FontWidthRuler.HORIZONTAL, 10, textArea);
					ruler.addSpin(3);
					ruler.NeedPaint = true;
					sp.setColumnHeaderView(ruler);

					int tabCount = tabbedPane.getTabCount();
					CloseableTabComponent closeableTabComponent = new CloseableTabComponent(tabbedPane, statusObject);
					tabbedPane.add("New File", sp);
					tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);

					tabbedPane.setSelectedComponent(sp);
					// 设置选项卡title为打开文件的文件名
					SwingUtils.setTabbedPaneTitle(tabbedPane, file.getName() + "_hex");
					String sb = WDByteUtil.bytes2HEX(bytes);

					int pos = 0;
					int len = sb.length();

					while (pos < len) {
						if (len - pos < 1024) {
							textArea.append(sb.substring(pos, pos + len - pos));
							pos += len - pos;
						} else {
							textArea.append(sb.substring(pos, pos + 1024));
							pos += 1024;
						}
						textArea.append("\n");
					}

					String res = Config.getValue("CURRENT_THEME", "current_font");

					textArea.setFont(FontUtil.getFont(res));
					statusObject.showSaveButton(false);

					closeableTabComponent.setModify(false);
					fis.close();
					// textArea.setCaretPosition(0);
					textArea.requestFocusInWindow();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		button_3.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504111819570_easyicon_net_24.png")));
		toolBar.add(button_3);

		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = SwingUtils.getCloseableTabComponent(tabbedPane).getFilePath();
				try {
					File file = new File(filePath);
					fis = new FileInputStream(file);

					byte[] bytes = new byte[fis.available()];

					fis.read(bytes);

					String plainText = DecryptTemplateAction.doAction(new String(bytes));
					if (WDAssert.isEmpty(plainText)) {
						return;
					}
					String debugPath = OsConstants.DEFAULT_USER_DIR + "/" + Config.getValue("CONFIG", "debugPath");
					File fileDir = new File(debugPath);
					if (!fileDir.exists()) {
						if (!fileDir.canWrite()) {
							// JOptionPane.showMessageDialog(null, "debug缓存目录不可写!");
							// return;
							fileDir.setReadable(true);
							fileDir.setWritable(true);
							fileDir.setExecutable(true);
						}
						if (!fileDir.mkdir()) {
							JOptionPane.showMessageDialog(null, "创建debug缓存目录失败！");
							return;
						}
					}

					String newFilePath = debugPath + "/" + file.getName();
					FileAction fileAction = new FileAction();
					fileAction.save(newFilePath, plainText, "GBK");
					fileHander.openFileWithFilePath(newFilePath, "GBK");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

			}
		});
		btnNewButton_1.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130721025254649_easyicon_net_24.png")));
		toolBar.add(btnNewButton_1);

		JMenuBar menuBar = new JMenuBar();
		frmEcheloneditor.setJMenuBar(menuBar);

		JMenu menu = new JMenu("文件");
		menu.setMnemonic('F');
		menuBar.add(menu);

		JMenu newFileMenu = new JMenu("新建");
		Collection<String> collection = Config.getItems("FILE_TYPE");
		newFileMenu.removeAll();
		for (String fileExt : collection) {
			JMenuItem menuItem = new JMenuItem("." + fileExt);
			menuItem.addActionListener(new SimpleJmenuItemListener(tabbedPane, statusObject));
			menuItem.setActionCommand("new");

			newFileMenu.add(menuItem);
			// newFileMenu.add(new JSeparator());
		}
		newFileMenu.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112619422_easyicon_net_24.png")));
		menu.add(newFileMenu);

		JMenuItem menuItem_1 = new JMenuItem("打开");
		menuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuItem_1.setActionCommand("open");
		menuItem_1.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));
		menuItem_1.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112148516_easyicon_net_24.png")));
		menu.add(menuItem_1);

		JMenuItem mntmex = new JMenuItem("打开(EX)");
		mntmex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mntmex.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112148516_easyicon_net_24.png")));
		mntmex.setActionCommand("openext");
		mntmex.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));
		menu.add(mntmex);

		JSeparator separator_1 = new JSeparator();
		menu.add(separator_1);

		JMenuItem menuItem_2 = new JMenuItem("保存");
		menuItem_2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuItem_2.setActionCommand("save");
		menuItem_2.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));
		menuItem_2.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504114455808_easyicon_net_24.png")));
		menu.add(menuItem_2);

		JMenuItem menuItem_3 = new JMenuItem("另存为");
		menuItem_3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		menuItem_3.setActionCommand("saveas");
		menuItem_3.addActionListener(new SimpleFileChooseListener(tabbedPane, statusObject));

		menuItem_3.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504112319418_easyicon_net_24.png")));
		menu.add(menuItem_3);

		JSeparator separator_2 = new JSeparator();
		menu.add(separator_2);

		JMenuItem menuItem_4 = new JMenuItem("退出");
		menuItem_4.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504114639245_easyicon_net_24.png")));
		menuItem_4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		menuItem_4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem_4);

		JMenu menu_4 = new JMenu("编辑");
		menuBar.add(menu_4);

		JMenuItem menuItem_6 = new JMenuItem("查找");
		menuItem_6.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508110036317_easyicon_net_24.png")));
		menuItem_6.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		menuItem_6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() <= 0) {
					return;
				}
				RSyntaxTextArea textArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				FindDialogListener findDialogListener = new FindDialogListener(textArea);
				findDialog = new FindDialog(frmEcheloneditor, findDialogListener);
				if (replaceDialog != null && replaceDialog.isVisible()) {
					replaceDialog.setVisible(false);
				}
				findDialog.setVisible(true);
			}
		});

		JMenuItem menuItem_13 = new JMenuItem("撤销");
		menuItem_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.undoLastAction();
			}
		});
		menuItem_13.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		menuItem_13.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508104933496_easyicon_net_24.png")));
		menu_4.add(menuItem_13);

		JMenuItem menuItem_14 = new JMenuItem("重做");
		menuItem_14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.redoLastAction();
			}
		});
		menuItem_14.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508104655987_easyicon_net_24.png")));
		menuItem_14.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		menu_4.add(menuItem_14);

		JSeparator separator_5 = new JSeparator();
		menu_4.add(separator_5);

		JMenuItem menuItem_8 = new JMenuItem("剪切");
		menuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.cut();
			}
		});
		menuItem_8.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		menuItem_8.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508105503688_easyicon_net_24.png")));
		menu_4.add(menuItem_8);

		JMenuItem menuItem_9 = new JMenuItem("复制");
		menuItem_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.copy();
			}
		});
		menuItem_9.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508112701510_easyicon_net_24.png")));
		menuItem_9.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		menu_4.add(menuItem_9);

		JMenuItem menuItem_10 = new JMenuItem("粘贴");
		menuItem_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.paste();
			}
		});
		menuItem_10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		menuItem_10.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508105319839_easyicon_net_24.png")));
		menu_4.add(menuItem_10);

		JMenuItem menuItem_11 = new JMenuItem("删除");
		menuItem_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				if (rSyntaxTextArea == null) {
					return;
				}
				int selectStart = rSyntaxTextArea.getSelectionStart();
				if (selectStart < 0) {
					return;
				}
				if (rSyntaxTextArea.getSelectedText() == null) {
					return;
				}
				int len = rSyntaxTextArea.getSelectedText().length();
				try {
					rSyntaxTextArea.getDocument().remove(selectStart, len);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		menuItem_11.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508105713413_easyicon_net_24.png")));
		menuItem_11.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menu_4.add(menuItem_11);

		JSeparator separator_4 = new JSeparator();
		menu_4.add(separator_4);

		JMenuItem menuItem_12 = new JMenuItem("全选");
		menuItem_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				rSyntaxTextArea.selectAll();
			}
		});
		menuItem_12.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508105939375_easyicon_net_24.png")));
		menuItem_12.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		menu_4.add(menuItem_12);
		JSeparator separator_3 = new JSeparator();
		menu_4.add(separator_3);
		menu_4.add(menuItem_6);

		JMenuItem mntmNewMenuItem = new JMenuItem("快速查找");
		mntmNewMenuItem.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508112417300_easyicon_net_24.png")));
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextComponent com = SwingUtils.getRSyntaxTextArea(tabbedPane);
				String targetStr = com.getSelectedText();
				FindAndReplaceAction.find(com, targetStr, true, true, false);
			}
		});

		JMenuItem menuItem_15 = new JMenuItem("替换");
		menuItem_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() <= 0) {
					return;
				}
				RSyntaxTextArea textArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				FindDialogListener findDialogListener = new FindDialogListener(textArea);
				replaceDialog = new ReplaceDialog(frmEcheloneditor, findDialogListener);
				if (findDialog != null && findDialog.isVisible()) {
					// replaceDialog.setSearchContext(findDialog.getSearchContext());
					findDialog.setVisible(false);
				}
				replaceDialog.setVisible(true);
			}
		});
		menuItem_15.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508110228832_easyicon_net_24.png")));
		menuItem_15.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		menu_4.add(menuItem_15);
		menu_4.add(mntmNewMenuItem);

		JMenu menu_3 = new JMenu("格式");
		menuBar.add(menu_3);

		JMenuItem menuItem_5 = new JMenuItem("字体设置");
		menuItem_5.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504114104911_easyicon_net_24.png")));
		menuItem_5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
		menuItem_5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() <= 0) {
					return;
				}
				RSyntaxTextArea textArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				Font font = textArea.getFont();

				FontChooserDialog fontset = new FontChooserDialog(frmEcheloneditor, font, textArea);
				fontset.setLocationRelativeTo(frmEcheloneditor);
				fontset.setVisible(true);
			}
		});
		menu_3.add(menuItem_5);

		JMenuItem menuItem_18 = new JMenuItem("转为大写");
		menuItem_18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				String oldText = rSyntaxTextArea.getSelectedText();
				rSyntaxTextArea.replaceSelection(oldText.toUpperCase());
			}
		});
		menuItem_18.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20140926105354538_easyicon_net_24.png")));
		menuItem_18.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK));
		menu_3.add(menuItem_18);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("转为小写");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				String oldText = rSyntaxTextArea.getSelectedText();
				rSyntaxTextArea.replaceSelection(oldText.toLowerCase());
			}
		});
		mntmNewMenuItem_4.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20140926105435448_easyicon_net_24.png")));
		mntmNewMenuItem_4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK));
		menu_3.add(mntmNewMenuItem_4);

		JSeparator separator_6 = new JSeparator();
		menu_3.add(separator_6);

		JMenuItem mntmTlv = new JMenuItem("TLV");
		menu_3.add(mntmTlv);
		mntmTlv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
				try {
					EmvFormatAction.format(rSyntaxTextArea);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mntmTlv.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130509034342785_easyicon_net_24.png")));
		mntmTlv.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK));

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("格式化");
		menu_3.add(mntmNewMenuItem_1);
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				RSyntaxTextArea rSyntaxTextArea = SwingUtils.getRSyntaxTextArea(tabbedPane);

				CloseableTabComponent ctc = SwingUtils.getCloseableTabComponent(tabbedPane);
				int pos = ctc.getFilePath().lastIndexOf(".");
				String fileExt = ctc.getFilePath().substring(pos + 1);
				if (fileExt.equals("xml")) {
					boolean Success = XmlPreettifyAction.format(rSyntaxTextArea);
					if (!Success) {
						JOptionPane.showMessageDialog(null, "格式化失败");
						return;
					}
				}

			}
		});
		mntmNewMenuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmNewMenuItem_1.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130508011341649_easyicon_net_24.png")));

		JMenu menu_1 = new JMenu("工具");
		menuBar.add(menu_1);

		JMenuItem menuItem_16 = new JMenuItem("辅助工具");
		menuItem_16.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504114154800_easyicon_net_24.png")));
		menuItem_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AssistantToolDialog assistantToolDialog = new AssistantToolDialog(statusObject);
				// JScrollPane jScrollPane = new JScrollPane();
				// jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				// jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				// jScrollPane.setViewportView(assistantToolDialog);

				int tabCount = tabbedPane.getTabCount();
				CloseableTabComponent closeableTabComponent = new CloseableTabComponent(tabbedPane, statusObject);
				closeableTabComponent.setFileEncode(OsConstants.DEFAULT_FILE_ENCODE);
				closeableTabComponent.setFileSzie(0);
				closeableTabComponent.setFileNameExt(".tool");
				closeableTabComponent.setModify(false);
				tabbedPane.add("AssistantTool", assistantToolDialog);
				tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);
				tabbedPane.setSelectedComponent(assistantToolDialog);

				SwingUtils.setTabbedPaneTitle(tabbedPane, "小工具");

				tabbedPane.setSelectedComponent(assistantToolDialog);
			}
		});
		menu_1.add(menuItem_16);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("密钥成分生成工具");
		mntmNewMenuItem_2.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130729071051684_easyicon_net_24.png")));
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generater window = new Generater();
				window.frame.setVisible(true);
			}
		});
		menu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("ZIP打包");
		mntmNewMenuItem_3.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130926111642678_easyicon_net_24.png")));
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = fileChooser.showOpenDialog(frmEcheloneditor);

				if (ret == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String filePath = selectedFile.getPath();
					filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
					File zipFile = new File(filePath + "/" + selectedFile.getName() + ".zip");
					try {
						ZipUtil.zip(fileChooser.getSelectedFile(), zipFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(frmEcheloneditor, e.getMessage());
					}
					JOptionPane.showMessageDialog(frmEcheloneditor, "操作完成!");
				}
			}
		});
		menu_1.add(mntmNewMenuItem_3);

		JMenuItem mntmSnapshot = new JMenuItem("SnapShot");
		mntmSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
		mntmSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardEditor.frmEcheloneditor.setExtendedState(JFrame.ICONIFIED);
				try {
					WindowsExcuter.excute(new File(OsConstants.DEFAULT_USER_DIR), "java -jar ScreenShot.jar", false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		mntmSnapshot.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20150103103702829_easyicon_net_24.png")));
		menu_1.add(mntmSnapshot);
		
		JMenuItem mntmCodecounter = new JMenuItem("CodeCounter");
		mntmCodecounter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = fileChooser.showOpenDialog(frmEcheloneditor);

				if (ret == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					long num=0;
					try {
						num=FileUtil.countLines(selectedFile);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(frmEcheloneditor, e1.getMessage());
						return;
					}
					FileUtil.lines=0;
					JOptionPane.showMessageDialog(frmEcheloneditor, "操作完成!"+num);
				}
			}
		});
		menu_1.add(mntmCodecounter);

		JMenu menu_5 = new JMenu("皮肤");
		menuBar.add(menu_5);

		JMenuItem menuItem_7 = new JMenuItem("皮肤");
		menuItem_7.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/20130504113002740_easyicon_net_24.png")));
		menuItem_7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				// TODO Auto-generated method stub
				new FaceDialog(frmEcheloneditor);
			}
		});
		menu_5.add(menuItem_7);

		JMenu menu_2 = new JMenu("帮助");
		menuBar.add(menu_2);

		JMenuItem menuItem_17 = new JMenuItem("帮助");
		menuItem_17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String appName = Config.getValue("CONFIG", "appName");
				String version = Config.getValue("CONFIG", "version");
				String versionName = Config.getValue("CONFIG", "versionName");
				JOptionPane.showMessageDialog(frmEcheloneditor, appName + "_" + version + "_" + versionName, "帮助", JOptionPane.WARNING_MESSAGE);
			}
		});

		JMenuItem mntmReadme = new JMenuItem("版本历史");
		mntmReadme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					// WindowsExcuter.excute(new File(FileAction.USER_DIR), "cmd.exe /c start readme.txt");
					fileHander.openFileWithFilePath(new File(OsConstants.DEFAULT_USER_DIR + "/history.txt").getPath(), OsConstants.DEFAULT_FILE_ENCODE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mntmReadme.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/2015032508484550_easyicon_net_24.png")));
		menu_2.add(mntmReadme);
		menuItem_17.setIcon(new ImageIcon(CardEditor.class.getResource("/com/echeloneditor/resources/images/2013050411485151_easyicon_net_24.png")));
		menu_2.add(menuItem_17);

		// JTabbedPane bottomTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);
		// bottomTabbedPane.addTab("状态信息", statusPanel);
		// systemShell = new SystemShell();
		// RScrollPane shellScrollPane = new DockableWindowScrollPane(systemShell);
		// bottomTabbedPane.addTab("控制台", FileAction.fsv.getSystemIcon(new File("C:/Windows/System32/cmd.exe")), shellScrollPane);

		centerSplitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerSplitPaneV.setContinuousLayout(true);
		centerSplitPaneV.setOneTouchExpandable(true);
		centerSplitPaneV.setTopComponent(tabbedPane);
		centerSplitPaneV.setBottomComponent(statusPanel);
		// centerSplitPaneV.setDividerLocation(0.95);//
		// frmEcheloneditor.getContentPane().add(statusPanel, BorderLayout.SOUTH);

		xFileSystemTree = new XFileSystemTree(tabbedPane, statusObject);
		xFileSystemTree.setBorder(null);
		xFileSystemTree.addMouseListener(new FileSystemTreeListener(tabbedPane, statusObject));
		xFileSystemTree.addKeyListener(new FileSystemTreeListener(tabbedPane, statusObject));

		RScrollPane scrollPane = new DockableWindowScrollPane(xFileSystemTree);
		scrollPane.setViewportBorder(null);
		centerSplitPaneH = new JSplitPane();

		JTabbedPane leftTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);
		leftTabbedPane.addTab("资源管理器", OsConstants.fsv.getSystemIcon(OsConstants.fsv.getHomeDirectory()), scrollPane);

		// centerSplitPaneH.setDividerLocation(0.2);
		centerSplitPaneH.setLeftComponent(leftTabbedPane);
		centerSplitPaneH.setRightComponent(centerSplitPaneV);
		frmEcheloneditor.getContentPane().add(centerSplitPaneH, BorderLayout.CENTER);

		container.doLayout();
	}

	@SuppressWarnings("static-access")
	private void open(JTabbedPane tabbedPane, long offset, boolean isForward, boolean isAbsolute) {
		CloseableTabComponent closeableTabComponent = SwingUtils.getCloseableTabComponent(tabbedPane);
		String filePath = closeableTabComponent.getFilePath();
		long fileSize = closeableTabComponent.getFileSzie();

		if (isAbsolute) {
			if (isForward) {
				offset = fileSize - OsConstants.BIG_FILE_READ_UNIT_SIZE;
			} else {
				offset = 0;
			}
		} else {
			if (isForward) {
				offset = fileHander.fileDescMapBean.get(filePath);
			} else {
				offset = fileHander.fileDescMapBean.get(filePath) - 2 * offset;
			}
		}
		fileHander.fileDescMapBean.put(filePath, offset);

		if (fileSize > (OsConstants.BIG_FILE_SIZE << 20)) {
			fileHander.openFileWithFilePath(filePath, fileHander.currentEncode);
		} else {
			JOptionPane.showMessageDialog(null, "too small!");
			return;
		}
	}
}
