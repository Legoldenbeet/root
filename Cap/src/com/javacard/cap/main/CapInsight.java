package com.javacard.cap.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.javacard.cap.Cap;
import com.javacard.cap.Formatter;
import com.watchdata.commons.lang.WDAssert;

public class CapInsight extends Formatter {

	private JFrame frmCapinsight;
	private JTree tree;
	public static Map<String, Map<String, String>> sessionMap = new HashMap<String, Map<String, String>>();
	private JTextPane textPane;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CapInsight window = new CapInsight();
					window.frmCapinsight.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CapInsight() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCapinsight = new JFrame();
		frmCapinsight.setTitle("CAPINSIGHT");
		frmCapinsight.setBounds(100, 100, 876, 542);
		frmCapinsight.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCapinsight.getContentPane().setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.15);
		frmCapinsight.getContentPane().add(splitPane);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				String nodeName = (node != null) ? node.toString() : null;
				if (WDAssert.isEmpty(nodeName)) {
					return;
				}
				String parentNodeName = node.getParent().toString();
				if (WDAssert.isEmpty(parentNodeName)) {
					return;
				}
				if (node.isLeaf()) {
					try {
						String info = sessionMap.get(parentNodeName).get(nodeName);
						textPane.setText("");
						textPane.setText(info);
						textArea.setText("");
						if (nodeName.startsWith("Debug") || nodeName.startsWith("Descriptor")||nodeName.startsWith("WDDebug")) {
							textArea.setText("fail.");
						} else {
							textArea.setText(format(parentNodeName, nodeName));
						}
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}

				}
			}
		});
		tree.setRootVisible(false);
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("CapInsight") {
			{
			}
		}));
		panel.add(tree);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.15);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);

		textPane = new JTextPane();
		textPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		splitPane_1.setLeftComponent(textPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_1);

		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		JMenuBar menuBar = new JMenuBar();
		frmCapinsight.setJMenuBar(menuBar);

		JMenu menu = new JMenu("文件");
		menuBar.add(menu);

		JMenuItem menuItem = new JMenuItem("打开");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser("./debug");
				FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("cap package", "cap");
				jFileChooser.setFileFilter(fileNameExtensionFilter);
				jFileChooser.setMultiSelectionEnabled(true);

				int i = jFileChooser.showOpenDialog(null);
				if (i == JFileChooser.APPROVE_OPTION) {
					File[] fileList = jFileChooser.getSelectedFiles();
					// File file = jFileChooser.getSelectedFile();
					DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel();
					DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
					root.removeAllChildren();

					for (File file : fileList) {
						DefaultMutableTreeNode capNode = new DefaultMutableTreeNode(file.getName());
						root.add(capNode);
						try {
							Map<String, String> mapBean = Cap.readCap(file.getPath());
							for (String key : mapBean.keySet()) {
								if (key.endsWith(".cap")) {
									DefaultMutableTreeNode componentNode = new DefaultMutableTreeNode(key);
									capNode.add(componentNode);
								}
							}
							sessionMap.put(file.getName(), mapBean);
						} catch (Exception e2) {
							// TODO: handle exception
						}
						expandTree(tree, true);
						tree.updateUI();
					}
				}
			}
		});
		menu.add(menuItem);
		frmCapinsight.setLocationRelativeTo(null);
	}

	public static void expandTree(JTree tree, boolean bo) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), bo);
	}

	private static void expandAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	@Override
	public String format(String pName, String componentName) throws IOException {
		Map<String, String> capInfoMap = sessionMap.get(pName);

		int major = Integer.parseInt(capInfoMap.get("MAJOR_VERSION"));
		int minor = Integer.parseInt(capInfoMap.get("MINOR_VERSION"));

		Cap.version = major + "." + minor;

		String componentInfo = capInfoMap.get(componentName);
		StringReader sr = new StringReader(componentInfo);

		componentName = componentName.substring(0, componentName.lastIndexOf('.'));
		String headerFormat = read(componentName + "Component" + "_" + major + "." + minor);

		if (WDAssert.isNotEmpty(headerFormat)) {
			if (componentName.equalsIgnoreCase("Class")) {
				return padingClassComponent(headerFormat, sr);
			} else {
				return paddingExt(headerFormat, sr);
			}
		}

		return null;
	}
}
