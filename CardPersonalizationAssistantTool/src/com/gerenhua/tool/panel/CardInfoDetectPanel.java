package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.gerenhua.tool.app.RightPanel;
import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.logic.apdu.CommonHelper;
import com.gerenhua.tool.logic.impl.CardInfoThread;
import com.gerenhua.tool.logic.impl.DeleteObjThread;
import com.gerenhua.tool.logic.impl.LoadCapThead;
import com.gerenhua.tool.logic.impl.RunPrgThread;
import com.gerenhua.tool.utils.Config;
import com.watchdata.commons.crypto.WD3DesCryptoUtil;
import com.watchdata.commons.jce.JceBase.Padding;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDStringUtil;
import com.watchdata.kms.kmsi.IKms;

public class CardInfoDetectPanel extends JPanel implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JTextField textField;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private JTextField textField_3;
	private static JTree tree;
	private static JTextField textField_4;
	private static JTextField textField_5;
	public static CommonAPDU commonAPDU;
	public static JTextPane textPane;
	public static JTextPane textPane_1;
	public static JComboBox comboBox;
	private static Log log = new Log();
	private static ConfigIpDialog dialog = null;

	private static JMenuItem mntmCardinfo;
	private static JMenuItem mntmChangeStatus;
	private static JMenuItem mntmLoad;
	private static JMenuItem mntmdeleteObj;
	// private static JMenuItem mntmCardStatus;
	private static JMenuItem mntmBuildScripts;
	private static JMenuItem mntmInstallApplet;

	private static Thread runPrgThread = null;
	public static RunPrgThread rpt = null;
	public static UpdateStatusDialog updateStatusDialog = null;

	public CardInfoDetectPanel() {
		log.setLogArea(textPane_1);
		setName("卡片信息");
		DefaultMutableTreeNode RootNode = new DefaultMutableTreeNode("CardInfo");
		DefaultTreeModel TreeModel = new DefaultTreeModel(RootNode);

		mntmCardinfo = new JMenuItem("CARD INFO");
		mntmCardinfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				refreshTree();
				if (RunPrgThread.mapBean != null) {
					RunPrgThread.mapBean.clear();
				}
			}
		});

		mntmChangeStatus = new JMenuItem("CHANGE STATUS");
		mntmChangeStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				setStatusDialog(true, true);
			}
		});

		// mntmCardStatus = new JMenuItem("CARD STATUS");
		// mntmCardStatus.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// log.setLogArea(textPane_1);
		// RightPanel.configPanel.setVisible(true);
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// RightPanel.configPanel.initPanel();
		// }
		// });
		//
		// Application.rightPanel.add(RightPanel.configPanel, BorderLayout.CENTER);
		// RightPanel.cardInfoDetectPanel.setVisible(false);
		// RightPanel.facePanel.setVisible(false);
		// RightPanel.testDataConfigPanel.setVisible(false);
		// RightPanel.terminalTypeConfigPanel.setVisible(false);
		// RightPanel.terminalPerformanceConfigPanel.setVisible(false);
		// RightPanel.terminalLimitConfigPanel.setVisible(false);
		// RightPanel.issuerKeyConfigPanel.setVisible(false);
		// RightPanel.caPublicKeyConfigPanel.setVisible(false);
		// RightPanel.aidConfigPanel.setVisible(false);
		// RightPanel.logoPanel.setVisible(false);
		// RightPanel.tradePanel.setVisible(false);
		// RightPanel.cardReaderPanel.setVisible(false);
		// }
		// });

		mntmLoad = new JMenuItem("LOAD CAP");
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				JFileChooser jFileChooser = null;
				String capFile = Config.getValue("CardInfo", "currentCap");
				if (WDAssert.isNotEmpty(capFile)) {
					jFileChooser = new JFileChooser(capFile);
				} else {
					jFileChooser = new JFileChooser("./resources/cap");
				}
				FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("cap package", "cap");
				jFileChooser.setFileFilter(fileNameExtensionFilter);
				jFileChooser.setMultiSelectionEnabled(true);

				int i = jFileChooser.showOpenDialog(null);
				if (i == JFileChooser.APPROVE_OPTION) {
					File[] file = jFileChooser.getSelectedFiles();
					LoadCapThead loadCapThead = new LoadCapThead(file, commonAPDU, textPane_1);
					loadCapThead.setRealCard(true);
					loadCapThead.start();
				}
			}
		});

		mntmBuildScripts = new JMenuItem("BUILD SCRIPTS");
		mntmBuildScripts.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane);
				JFileChooser jFileChooser = null;
				String capFile = Config.getValue("CardInfo", "currentCap");
				if (WDAssert.isNotEmpty(capFile)) {
					jFileChooser = new JFileChooser(capFile);
				} else {
					jFileChooser = new JFileChooser("./resources/cap");
				}

				FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("cap package", "cap");
				jFileChooser.setFileFilter(fileNameExtensionFilter);
				jFileChooser.setMultiSelectionEnabled(true);

				int i = jFileChooser.showOpenDialog(null);
				if (i == JFileChooser.APPROVE_OPTION) {
					File[] file = jFileChooser.getSelectedFiles();
					LoadCapThead loadCapThead = new LoadCapThead(file, commonAPDU, textPane);
					loadCapThead.setRealCard(false);
					loadCapThead.start();
				}
				log.setLogArea(textPane_1);
			}
		});
		mntmInstallApplet = new JMenuItem("Install");
		mntmInstallApplet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.setLogArea(textPane_1);
				Component component = SwingUtilities.getRoot(tree);
				JFrame root = (JFrame) component;
				InstallDialog installDialog = null;
				installDialog = new InstallDialog(root, tree, commonAPDU);
				int x = (int) (root.getLocation().getX() + root.getSize().width - 480);
				int y = (int) (root.getLocation().getY() + 40);
				installDialog.setLocation(x, y);
				installDialog.setVisible(true);
			}
		});

		mntmdeleteObj = new JMenuItem("Remove");
		mntmdeleteObj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				DeleteObjThread deleteObjThread = new DeleteObjThread(tree, commonAPDU);
				deleteObjThread.start();
			}
		});
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "CARD_INFO", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		tree = new JTree();
		tree.setVisibleRowCount(0);
		tree.setShowsRootHandles(true);
		tree.setModel(TreeModel);
		scrollPane.setViewportView(tree);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(tree, popupMenu);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "KEY_INFO", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		panel_3.setLayout(null);

		comboBox = new JComboBox();
		comboBox.setBounds(74, 142, 54, 21);
		panel_3.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "00", "01", "03" }));

		JLabel lblId = new JLabel("id:");
		lblId.setBounds(138, 146, 39, 15);
		panel_3.add(lblId);
		lblId.setHorizontalAlignment(SwingConstants.RIGHT);

		textField_5 = new JTextField();
		textField_5.setBounds(182, 143, 66, 21);
		panel_3.add(textField_5);
		textField_5.setText("00");
		textField_5.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("version:");
		lblNewLabel_1.setBounds(248, 146, 64, 15);
		panel_3.add(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);

		textField_4 = new JTextField();
		textField_4.setBounds(322, 144, 66, 21);
		panel_3.add(textField_4);
		textField_4.setText("00");
		textField_4.setColumns(10);

		JLabel lblNewLabel = new JLabel("Kenc:");
		lblNewLabel.setBounds(10, 57, 54, 15);
		panel_3.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		textField = new JTextField();
		textField.setBounds(74, 54, 314, 21);
		panel_3.add(textField);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				Config.setValue("CardInfo", "Kenc", textField.getText().trim());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		textField.setText(Config.getValue("CardInfo", "Kenc"));

		JLabel lblKmac = new JLabel("Kmac:");
		lblKmac.setBounds(10, 88, 54, 15);
		panel_3.add(lblKmac);
		lblKmac.setHorizontalAlignment(SwingConstants.RIGHT);

		textField_1 = new JTextField();
		textField_1.setBounds(74, 85, 314, 21);
		panel_3.add(textField_1);
		textField_1.setColumns(10);
		textField_1.setText(Config.getValue("CardInfo", "Kmac"));

		JLabel lblKdek = new JLabel("Kdek:");
		lblKdek.setBounds(10, 114, 54, 15);
		panel_3.add(lblKdek);
		lblKdek.setHorizontalAlignment(SwingConstants.RIGHT);

		textField_2 = new JTextField();
		textField_2.setBounds(74, 111, 314, 21);
		panel_3.add(textField_2);
		textField_2.setColumns(10);
		textField_2.setText(Config.getValue("CardInfo", "Kdek"));

		textField_3 = new JTextField();
		textField_3.setBounds(73, 23, 315, 21);
		panel_3.add(textField_3);
		textField_3.setColumns(10);
		textField_3.setText(Config.getValue("CardInfo", "KMC"));

		JLabel lblKmc = new JLabel("KMC:");
		lblKmc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (dialog == null) {
					dialog = new ConfigIpDialog(null);
				}
				if (dialog.isVisible()) {
					dialog.toFront();
				} else {
					dialog.setVisible(true);
				}
			}
		});
		lblKmc.setBounds(16, 28, 48, 15);
		panel_3.add(lblKmc);
		lblKmc.setHorizontalAlignment(SwingConstants.RIGHT);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.5);
		splitPane.setLeftComponent(panel_3);
		splitPane.setRightComponent(panel);
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "GP\u6307\u4EE4", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel_1.add(scrollPane_1);

		textPane = new JTextPane() {
			/**
							 * 
							 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return false;
			}

			@Override
			public void setSize(Dimension d) {
				if (d.width < getParent().getSize().width) {
					d.width = getParent().getSize().width;
				}
				super.setSize(d);
			}
		};
		scrollPane_1.setViewportView(textPane);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "LOG", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_2 = new JScrollPane();
		panel_2.add(scrollPane_2, BorderLayout.CENTER);

		textPane_1 = new JTextPane() {
			/**
													 * 
													 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return false;
			}

			@Override
			public void setSize(Dimension d) {
				if (d.width < getParent().getSize().width) {
					d.width = getParent().getSize().width;
				}
				super.setSize(d);
			}
		};
		scrollPane_2.setViewportView(textPane_1);

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);

		JButton btnNewButton_1 = new JButton("脚本");
		btnNewButton_1.setBounds(0, 10, 120, 23);
		panel_4.add(btnNewButton_1);
		btnNewButton_1.setFocusPainted(false);
		btnNewButton_1.setBorderPainted(false);

		JButton button = new JButton("执行");
		button.setBounds(0, 43, 120, 23);
		panel_4.add(button);
		button.setFocusPainted(false);
		button.setBorderPainted(false);

		JButton btnPutkey = new JButton("PutKey");
		btnPutkey.setBounds(0, 76, 120, 23);
		panel_4.add(btnPutkey);
		btnPutkey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String keyVersion = textField_4.getText().trim();
				String keyId = textField_5.getText().trim();

				String keyInfo = textPane.getText().trim();
				String[] keys = keyInfo.split("\r\n");

				String newEnc = null;
				String newMac = null;
				String newDek = null;
				String newKeyVersion = null;

				if (keys.length == 4) {
					newEnc = keys[0];
					newMac = keys[1];
					newDek = keys[2];
					newKeyVersion = keys[3];
				} else if (keys.length == 2) {
					String newKey = keys[0];
					newKeyVersion = keys[1];

					String initResp = commonAPDU.getInitResp();
					initResp = initResp.substring(8, 20);

					String deriveData = initResp + "F001" + initResp + "0F01";
					newEnc = WD3DesCryptoUtil.ecb_encrypt(newKey, deriveData, Padding.NoPadding);

					deriveData = initResp + "F002" + initResp + "0F02";
					newMac = WD3DesCryptoUtil.ecb_encrypt(newKey, deriveData, Padding.NoPadding);

					deriveData = initResp + "F003" + initResp + "0F03";
					newDek = WD3DesCryptoUtil.ecb_encrypt(newKey, deriveData, Padding.NoPadding);
				} else {
					JOptionPane.showMessageDialog(null, "参数个数错误！");
					return;
				}
				try {
					commonAPDU.putKey(keyVersion, keyId, newKeyVersion, newEnc, newMac, newDek);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnPutkey.setFocusPainted(false);
		btnPutkey.setBorderPainted(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				if (RunPrgThread.mapBean != null) {
					RunPrgThread.mapBean.clear();
				}
				runPrgInThread();
			}
		});
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jFileChooser = new JFileChooser("./resources/scripts");
				FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("prg files", "prg");
				jFileChooser.setFileFilter(fileNameExtensionFilter);

				int i = jFileChooser.showOpenDialog(null);
				if (i == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();

					FileInputStream fis;
					try {
						fis = new FileInputStream(file);
						byte[] fileContent = new byte[fis.available()];
						fis.read(fileContent);

						textPane.setText(new String(fileContent));
						// textPane.setCaretPosition(0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e.getMessage());
					}

				}
			}
		});
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setEnabled(false);
		splitPane_2.setResizeWeight(0.85);
		splitPane_2.setLeftComponent(panel_1);
		splitPane_2.setRightComponent(panel_4);

		JButton btnStop = new JButton("STOP");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				RunPrgThread.mapBean.clear();
				RunPrgThread.mapBean.put("debug", "stop");
			}
		});
		btnStop.setFocusPainted(false);
		btnStop.setBorderPainted(false);
		btnStop.setBounds(0, 109, 120, 23);
		panel_4.add(btnStop);

		JButton btnContinue = new JButton("CONTINUE");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				log.setLogArea(textPane_1);
				RunPrgThread.mapBean.clear();
				RunPrgThread.mapBean.put("debug", "continue");
				synchronized (RunPrgThread.mapBean) {
					RunPrgThread.mapBean.notifyAll();
				}
			}
		});
		btnContinue.setFocusPainted(false);
		btnContinue.setBorderPainted(false);
		btnContinue.setBounds(0, 174, 120, 23);
		panel_4.add(btnContinue);

		JButton btnStep = new JButton("STEP");
		btnStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				if (RunPrgThread.mapBean.size() > 0 && (RunPrgThread.mapBean.get("debug").equalsIgnoreCase("pause") || RunPrgThread.mapBean.get("debug").equalsIgnoreCase("step") || RunPrgThread.mapBean.get("debug").equalsIgnoreCase("continue"))) {
					RunPrgThread.mapBean.clear();
					RunPrgThread.mapBean.put("debug", "step");
					synchronized (RunPrgThread.mapBean) {
						RunPrgThread.mapBean.notifyAll();
					}
				} else {
					RunPrgThread.mapBean.clear();
					RunPrgThread.mapBean.put("debug", "step");
					runPrgInThread();
				}
			}
		});
		btnStep.setFocusPainted(false);
		btnStep.setBorderPainted(false);
		btnStep.setBounds(0, 207, 120, 23);
		panel_4.add(btnStep);

		JButton btnPause = new JButton("PAUSE");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setLogArea(textPane_1);
				RunPrgThread.mapBean.clear();
				RunPrgThread.mapBean.put("debug", "pause");
			}
		});
		btnPause.setFocusPainted(false);
		btnPause.setBorderPainted(false);
		btnPause.setBounds(0, 141, 120, 23);
		panel_4.add(btnPause);

		JSplitPane splitPane_4 = new JSplitPane();
		splitPane_4.setEnabled(false);
		splitPane_4.setResizeWeight(0.6);
		splitPane_4.setTopComponent(splitPane_2);
		splitPane_4.setBottomComponent(panel_2);
		splitPane_4.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setEnabled(false);
		splitPane_1.setResizeWeight(0.3);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setTopComponent(splitPane);
		splitPane_1.setBottomComponent(splitPane_4);

		add(splitPane_1, BorderLayout.CENTER);
		textField_3.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				String kmc = textField_3.getText().trim();
				commonAPDU = new CommonAPDU();
				HashMap<String, String> res = commonAPDU.reset();
				try {
					commonAPDU.send("00A4040000");
					String hostRandom = WDStringUtil.getRandomHexString(16);
					String keyVersion = textField_4.getText().trim();
					String keyId = textField_5.getText().trim();
					// initializeUpdate
					String strResp = commonAPDU.send("8050" + keyVersion + keyId + "08" + hostRandom);
					String initResp = strResp.substring(8, 20);

					String deriveDataKeyEnc = initResp + "F001" + initResp + "0F01";
					String deriveDataKeyMac = initResp + "F002" + initResp + "0F02";
					String deriveDataKeyDek = initResp + "F003" + initResp + "0F03";
					String keyEnc = null;
					String keyMac = null;
					String keyDek = null;
					if (kmc.length() == 16) {
						IKms iKms = IKms.getInstance();
						keyEnc = iKms.encrypt(kmc, IKms.DES_ECB, deriveDataKeyEnc, "pct");
						keyMac = iKms.encrypt(kmc, IKms.DES_ECB, deriveDataKeyMac, "pct");
						keyDek = iKms.encrypt(kmc, IKms.DES_ECB, deriveDataKeyDek, "pct");
					} else {
						keyEnc = WD3DesCryptoUtil.ecb_encrypt(kmc, deriveDataKeyEnc, Padding.NoPadding);
						keyMac = WD3DesCryptoUtil.ecb_encrypt(kmc, deriveDataKeyMac, Padding.NoPadding);
						keyDek = WD3DesCryptoUtil.ecb_encrypt(kmc, deriveDataKeyDek, Padding.NoPadding);
					}
					CommonHelper.updateUI(textField, keyEnc);
					CommonHelper.updateUI(textField_1, keyMac);
					CommonHelper.updateUI(textField_2, keyDek);

					Config.setValue("CardInfo", "KMC", kmc);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void removeUpdate(DocumentEvent e) {
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
		textField_2.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				Config.setValue("CardInfo", "Kdek", textField_2.getText().trim());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		textField_1.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				Config.setValue("CardInfo", "Kmac", textField_1.getText().trim());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Component com = e.getComponent();
					if (com instanceof JTree) {
						JTree tree = (JTree) com;
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

						String nodeName = (node != null) ? node.toString() : null;
						if (WDAssert.isEmpty(nodeName)) {
							return;
						}
						if (node.isLeaf() && !nodeName.equalsIgnoreCase("CardInfo")) {
							String parentNodeName = node.getParent().toString().trim();
							String grandFather = null;
							if (node.getParent().getParent() != null) {
								grandFather = node.getParent().getParent().toString().trim();
							}
							if (parentNodeName.equalsIgnoreCase("Load Files") || parentNodeName.equalsIgnoreCase("Application Instances")) {
								popup.removeAll();
								addMenu(mntmdeleteObj, e);
								if (parentNodeName.equalsIgnoreCase("Application Instances")) {
									addMenu(mntmChangeStatus, e);
									setStatusDialog(false, false);
									updateStatusDialog.isISD = false;
								}
								showMenu(e);
							} else if (grandFather != null && grandFather.equalsIgnoreCase("Load Files and Modules")) {
								popup.removeAll();
								addMenu(mntmInstallApplet, e);
								showMenu(e);
							}
						}
						if (nodeName.equalsIgnoreCase("CardInfo")) {
							popup.removeAll();
							addMenu(mntmCardinfo, e);
							// addMenu(mntmCardStatus, e);
							addMenu(mntmChangeStatus, e);
							setStatusDialog(false, false);
							updateStatusDialog.isISD = true;
							showMenu(e);
						}else if (nodeName.equalsIgnoreCase("Application Instances")) {
							popup.removeAll();
							Collection<String> templates = Config.getItems("Personalization_Template");
							for (String template : templates) {
								final JMenuItem item_tmp = new JMenuItem(template);
								item_tmp.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										JOptionPane.showMessageDialog(null, "nook");
									}
								});
								addMenu(item_tmp, e);
							}
							showMenu(e);
						} else if (nodeName.equalsIgnoreCase("Load Files")) {
							popup.removeAll();
							addMenu(mntmLoad, e);
							addMenu(mntmBuildScripts, e);
							showMenu(e);
						} 

					}
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}

			private void addMenu(JMenuItem menuItem, MouseEvent e) {
				JSeparator jSeparator = new JSeparator(JSeparator.HORIZONTAL);
				popup.add(jSeparator);
				popup.add(menuItem);
				// showMenu(e);
			}
		});

	}

	/**
	 * 更新tree变化
	 */
	public static void refreshTree() {
		if (commonAPDU == null) {
			commonAPDU = new CommonAPDU();
		}

		CardInfoThread cardInfoThread = new CardInfoThread(tree, commonAPDU, comboBox.getSelectedItem().toString().trim(), textField_4.getText().trim(), textField_5.getText().trim(), textField.getText().trim(), textField_1.getText().trim(), textField_2.getText().trim(), textPane_1);
		cardInfoThread.start();
	}

	/**
	 * runPrgInThread
	 */
	public void runPrgInThread() {
		if (WDAssert.isEmpty(textPane.getText())) {
			JOptionPane.showMessageDialog(null, "请先加载脚本！", "信息框", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (rpt == null) {
			rpt = new RunPrgThread(textPane, commonAPDU);
			runPrgThread = new Thread(rpt);
			rpt.addObserver(RightPanel.cardInfoDetectPanel);
			runPrgThread.start();
		}
	}

	/**
	 * setStatusDialog
	 * 
	 * @param visible
	 */
	public void setStatusDialog(boolean visible, boolean isSend) {
		Component component = SwingUtilities.getRoot(tree);
		JFrame root = (JFrame) component;
		updateStatusDialog = new UpdateStatusDialog(root, tree, commonAPDU, isSend);
		int x = (int) (root.getLocation().getX() + root.getSize().width - 480);
		int y = (int) (root.getLocation().getY() + 40);
		updateStatusDialog.setLocation(x, y);
		updateStatusDialog.setVisible(visible);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		String temp = arg1.toString();
		String[] tmp = temp.split("\\|");
		int pos1 = Integer.parseInt(tmp[0]);
		int pos2 = Integer.parseInt(tmp[1]);
		// textPane.setCaretPosition(pos2);

		// SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
		// StyleConstants.setBackground(simpleAttributeSet, Color.RED);
		// StyledDocument doc = textPane.getStyledDocument();
		// doc.setCharacterAttributes(pos1, pos2, simpleAttributeSet, false);
	}
}
