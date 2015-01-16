package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import com.gerenhua.tool.log.Log;
import com.gerenhua.tool.logic.Constants;
import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.utils.Config;
import com.watchdata.commons.lang.WDAssert;
import com.watchdata.commons.lang.WDStringUtil;

public class UpdateStatusDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public static boolean isISD = true;
	public JTree tree;
	public CommonAPDU commonAPDU;
	public static Log logger = new Log();

	/**
	 * Create the dialog.
	 */
	public UpdateStatusDialog(JFrame p, final JTree tree, final CommonAPDU commonAPDU, boolean isSend) {
		super(p, "Install", true);
		setBounds(100, 100, 470, 199);

		this.tree = tree;
		this.commonAPDU = commonAPDU;

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("卡片状态:");
		lblNewLabel.setBounds(22, 23, 60, 15);
		contentPanel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("inital");
		lblNewLabel_1.setBounds(86, 23, 100, 15);
		if (isSend) {
			String resp = "";
			try {
				if (isISD) {
					resp = commonAPDU.send("80F28000024F00");
				} else {
					DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					String selNodeName = (selNode != null) ? selNode.toString() : null;
					if (WDAssert.isNotEmpty(selNodeName)) {
						String aid = selNodeName.substring(0, selNodeName.indexOf(";"));
						String aidLen = WDStringUtil.paddingHeadZero(Integer.toHexString(aid.length() / 2), 2);
						String lc = WDStringUtil.paddingHeadZero(Integer.toHexString(aid.length() / 2 + 2), 2);
						resp = commonAPDU.send("80F24000" + lc + "4F" + aidLen + aid);
					}
				}
				if (resp.endsWith(Constants.SW_SUCCESS)) {
					int pos = 0;
					int len = Integer.parseInt(resp.substring(pos, 2), 16);
					pos += 2;
					// String aid = resp.substring(pos, 2 * len + pos);
					pos += 2 * len;
					String lifeStyleCode = resp.substring(pos, pos + 2);
					if (isISD) {
						lblNewLabel_1.setText(Config.getValue("Card_Lifestyle", lifeStyleCode));
					} else {
						lblNewLabel_1.setText(Config.getValue("App_Lifestyle", lifeStyleCode));
					}

				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		contentPanel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("目标状态:");
		lblNewLabel_2.setBounds(196, 23, 63, 15);
		contentPanel.add(lblNewLabel_2);

		final JComboBox comboBox = new JComboBox();
		Collection<String> style = null;
		if (isISD) {
			style = Config.getItems("Card_Lifestyle");
			for (String ls : style) {
				comboBox.addItem(Config.getValue("Card_Lifestyle", ls));
			}
		} else {
			style = Config.getItems("App_Lifestyle");
			for (String ls : style) {
				comboBox.addItem(Config.getValue("App_Lifestyle", ls));
			}
		}

		comboBox.setBounds(269, 20, 149, 21);
		contentPanel.add(comboBox);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("SET");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				try {
					String desc = comboBox.getSelectedItem().toString().trim();
					String item = "";
					if (isISD) {
						item = Config.getItemWithValue("Card_Lifestyle", desc);
					} else {
						item = Config.getItemWithValue("App_Lifestyle", desc);
					}

					if (WDAssert.isNotEmpty(item)) {
						if (isISD) {
							commonAPDU.send("80F080" + item + "00");
						} else {
							DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

							String selNodeName = (selNode != null) ? selNode.toString() : null;
							if (WDAssert.isNotEmpty(selNodeName)) {
								String aid = selNodeName.substring(0, selNodeName.indexOf(";"));
								String aidLen = WDStringUtil.paddingHeadZero(Integer.toHexString(aid.length() / 2), 2);
								// String lc = WDStringUtil.paddingHeadZero(Integer.toHexString(aid.length() / 2+2), 2);
								commonAPDU.send("80F040" + item + /* lc + "4F" + */aidLen + aid);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component component = SwingUtilities.getRoot((Component) e.getSource());
				component.setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
}
