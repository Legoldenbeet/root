package com.javacard.cap.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
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
	private Map<String, String> mapBean;
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
				if (node.isLeaf()) {
					try {
						textArea.setText("");
						textArea.setText(format(nodeName));
					} catch (Exception e2) {
						// TODO: handle exception
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

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);
		
		JPanel panel_1 = new JPanel();
		scrollPane_1.setViewportView(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		panel_1.add(textArea);
		JMenuBar menuBar = new JMenuBar();
		frmCapinsight.setJMenuBar(menuBar);

		JMenu menu = new JMenu("文件");
		menuBar.add(menu);

		JMenuItem menuItem = new JMenuItem("打开");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser(".");
				FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("cap package", "cap");
				jFileChooser.setFileFilter(fileNameExtensionFilter);
				// jFileChooser.setMultiSelectionEnabled(true);

				int i = jFileChooser.showOpenDialog(null);
				if (i == JFileChooser.APPROVE_OPTION) {
					// /File[] file = jFileChooser.getSelectedFiles();
					File file = jFileChooser.getSelectedFile();
					DefaultTreeModel dtm = (DefaultTreeModel) tree.getModel();
					DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();
					DefaultMutableTreeNode capNode = new DefaultMutableTreeNode(file.getName());
					root.removeAllChildren();
					root.add(capNode);
					try {
						mapBean = Cap.readCap(file.getPath());
						for (String key : mapBean.keySet()) {
							DefaultMutableTreeNode componentNode = new DefaultMutableTreeNode(key);
							capNode.add(componentNode);
						}
					} catch (Exception e2) {
						// TODO: handle exception
					}
					expandTree(tree, true);
					tree.updateUI();
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
	public String format(String componentName) throws IOException {
		String componentInfo = mapBean.get(componentName);
		componentName = componentName.substring(0, componentName.lastIndexOf('.'));
		String headerFormat = read(componentName + "Component");
		if (WDAssert.isNotEmpty(headerFormat)) {
			return paddingExt(headerFormat, new StringReader(componentInfo));
		}

		return null;
	}
}
