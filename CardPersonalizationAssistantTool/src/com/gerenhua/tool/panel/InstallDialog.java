package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.logic.impl.InstallAppletThread;

public class InstallDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	public JTree tree;
	public CommonAPDU commonAPDU;
	public static Log logger = new Log();
	private JTextField textField_3;

	/**
	 * Create the dialog.
	 */
	public InstallDialog(JFrame p, final JTree tree, final CommonAPDU commonAPDU) {
		super(p, "Install", true);
		this.tree = tree;
		this.commonAPDU = commonAPDU;

		setBounds(100, 100, 470, 199);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel AID = new JLabel("AID:");
		AID.setHorizontalAlignment(SwingConstants.RIGHT);
		AID.setBounds(10, 41, 80, 20);
		contentPanel.add(AID);

		textField = new JTextField();
		textField.setBounds(100, 41, 321, 21);
		contentPanel.add(textField);
		textField.setColumns(10);

		JLabel lblPrivilege = new JLabel("Privilege:");
		lblPrivilege.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrivilege.setBounds(10, 71, 80, 20);
		contentPanel.add(lblPrivilege);

		textField_1 = new JTextField();
		textField_1.setText("00");
		textField_1.setColumns(10);
		textField_1.setBounds(100, 71, 72, 21);
		contentPanel.add(textField_1);

		JLabel lblParam = new JLabel("Param:");
		lblParam.setHorizontalAlignment(SwingConstants.RIGHT);
		lblParam.setBounds(10, 101, 80, 20);
		contentPanel.add(lblParam);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(100, 101, 108, 21);
		contentPanel.add(textField_2);

		textField_3 = new JTextField();
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		String moduleName = (selNode != null) ? selNode.toString() : null;
		textField_3.setEditable(false);
		textField_3.setColumns(10);
		textField_3.setBounds(100, 10, 321, 21);
		textField_3.setText(moduleName);
		contentPanel.add(textField_3);

		JLabel lblModule = new JLabel("Module:");
		lblModule.setHorizontalAlignment(SwingConstants.RIGHT);
		lblModule.setBounds(10, 10, 80, 20);
		contentPanel.add(lblModule);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("Install");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textField.getText() == null) {
					JOptionPane.showMessageDialog(null, "aid 不可以为空");
					return;
				}

				InstallAppletThread.aid = textField.getText().toString().trim().toUpperCase();
				InstallAppletThread.privilege = textField_1.getText().toString().trim().toUpperCase();
				InstallAppletThread.param = textField_2.getText().toString().trim().toUpperCase();

				InstallAppletThread installAppletThread = new InstallAppletThread(tree, commonAPDU);
				installAppletThread.start();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component component = SwingUtilities.getRoot((Component) e.getSource());
				component.setVisible(false);
			}
		});
		buttonPane.add(cancelButton);
	}
}
