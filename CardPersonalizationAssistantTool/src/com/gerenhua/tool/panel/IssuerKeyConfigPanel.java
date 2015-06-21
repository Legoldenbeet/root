package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.gerenhua.tool.configdao.IssuerKeyInfo;
import com.gerenhua.tool.logic.apdu.CommonHelper;
import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.FixedSizePlainDocument;
import com.gerenhua.tool.utils.PropertiesManager;
import com.watchdata.commons.lang.WDAssert;

public class IssuerKeyConfigPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField_2;
	private JTextField textField_1;
	private JTextField textField;
	private PropertiesManager pm = new PropertiesManager();

	private IssuerKeyInfo issuerKeyInfo = new IssuerKeyInfo();
	private JTextField textField_3;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTable table;
	private final String[] COLUMNS = new String[] { "密钥名称", "UDKac", "UDKmac", "UDKenc", "Derive" };
	Object[][] tableData = null;
	private DefaultTableModel testDataTableModel = null;
	List<IssuerKeyInfo> sdList = new ArrayList<IssuerKeyInfo>();
	private JTextField textField_4;
	private JCheckBox chckbxDe;
	private JPopupMenu popupMenu = new JPopupMenu();
	public static String currentSelectedKeyName="";
	/**
	 * Create the panel
	 */
	public IssuerKeyConfigPanel() {
		super();
		setLayout(null);
		// setBorder(JTBorderFactory.createTitleBorder(pm.getString("mv.issuerkeyconfig.issuerkey")));
		init();

		final JLabel lblUdkac = new JLabel();
		lblUdkac.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUdkac.setText("UDKac：");
		lblUdkac.setBounds(-20, 53, 97, 20);
		add(lblUdkac);

		IssuerKeyInfo ikInfo = issuerKeyInfo.getIssuerKeyInfo("ApplicationKey");

		textField = new JTextField();
		textField.setBounds(82, 53, 400, 20);
		textField.setDocument(new FixedSizePlainDocument(32));
		textField.setToolTipText(pm.getString("mv.issuerkeyconfig.fullkey"));
		textField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				String c = String.valueOf(e.getKeyChar());
				Pattern pattern = Pattern.compile("[0-9a-fA-F]*");

				Matcher matcher = pattern.matcher(c);
				if (!(matcher.matches()))
					e.consume();
			}
		});
		textField.setText(ikInfo.getAcKey());
		add(textField);

		final JLabel lblUdkmac = new JLabel();
		lblUdkmac.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUdkmac.setText("UDKmac：");
		lblUdkmac.setBounds(-20, 83, 97, 20);
		add(lblUdkmac);

		textField_1 = new JTextField();
		textField_1.setBounds(82, 83, 400, 20);
		textField_1.setDocument(new FixedSizePlainDocument(32));
		textField_1.setToolTipText(pm.getString("mv.issuerkeyconfig.fullkey"));
		textField_1.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				String c = String.valueOf(e.getKeyChar());
				Pattern pattern = Pattern.compile("[0-9a-fA-F]*");

				Matcher matcher = pattern.matcher(c);
				if (!(matcher.matches()))
					e.consume();
			}
		});
		textField_1.setText(ikInfo.getMacKey());
		add(textField_1);

		final JLabel lblUdkenc = new JLabel();
		lblUdkenc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUdkenc.setText("UDKenc：");
		lblUdkenc.setBounds(-30, 113, 107, 20);
		add(lblUdkenc);

		textField_2 = new JTextField();
		textField_2.setBounds(82, 113, 400, 20);
		textField_2.setDocument(new FixedSizePlainDocument(32));
		textField_2.setToolTipText(pm.getString("mv.issuerkeyconfig.fullkey"));
		textField_2.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				String c = String.valueOf(e.getKeyChar());
				Pattern pattern = Pattern.compile("[0-9a-fA-F]*");

				Matcher matcher = pattern.matcher(c);
				if (!(matcher.matches()))
					e.consume();
			}
		});
		textField_2.setText(ikInfo.getEncKey());
		add(textField_2);

		chckbxDe = new JCheckBox("Derive");
		chckbxDe.setBounds(195, 139, 103, 23);
		chckbxDe.setSelected(ikInfo.getDerive() == 0 ? false : true);
		add(chckbxDe);

		textField_4 = new JTextField();
		textField_4.setBounds(82, 140, 107, 20);
		textField_4.setText(ikInfo.getKeyName());
		add(textField_4);
		textField_4.setColumns(10);

		final JButton button_1 = new JButton();
		button_1.setText("保存");
		button_1.setBounds(82, 170, 84, 21);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				issuerKeyInfo.setDerive(chckbxDe.isSelected() == true ? 1 : 0);
				issuerKeyInfo.setKeyName(textField_4.getText().trim());
				issuerKeyInfo.setAcKey(textField.getText().trim());
				issuerKeyInfo.setMacKey(textField_1.getText().trim());
				issuerKeyInfo.setEncKey(textField_2.getText().trim());
				issuerKeyInfo.add(issuerKeyInfo);
				Config.setValue("ApplicationKey", "currentAppKey", issuerKeyInfo.getKeyName());

				JOptionPane.showMessageDialog(null, "保存成功！", "提示框", JOptionPane.INFORMATION_MESSAGE);
				sdList = issuerKeyInfo.getIssuerKeyInfos("ApplicationKey");
				tableDataDisp();
				table.repaint();
			}

		});
		button_1.setFocusPainted(false);
		button_1.setBorderPainted(false);
		add(button_1);

		JLabel label = new JLabel();
		label.setText("8000：");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(-20, 23, 97, 20);
		add(label);

		textField_3 = new JTextField();
		textField_3.setToolTipText("请输入96位的0-F之间数字或字母");
		textField_3.setDocument(new FixedSizePlainDocument(96));
		textField_3.setBounds(82, 23, 400, 20);
		textField_3.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				try {
					String appKeyStr = textField_3.getText();
					String udkAc = appKeyStr.substring(0, 32);
					CommonHelper.updateUI(textField, udkAc);
					CommonHelper.updateUI(textField_5, CommonHelper.getCheckValue(udkAc));

					String udkMac = appKeyStr.substring(32, 64);
					CommonHelper.updateUI(textField_1, udkMac);
					CommonHelper.updateUI(textField_6, CommonHelper.getCheckValue(udkMac));

					String udkEnc = appKeyStr.substring(64, 96);
					CommonHelper.updateUI(textField_2, udkEnc);
					CommonHelper.updateUI(textField_7, CommonHelper.getCheckValue(udkEnc));
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

		add(textField_3);

		JLabel label_1 = new JLabel();
		label_1.setText("CKV：");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(485, 53, 47, 20);
		add(label_1);

		textField_5 = new JTextField();
		textField_5.setToolTipText("");
		textField_5.setEditable(false);
		textField_5.setBounds(538, 53, 111, 20);
		add(textField_5);

		JLabel label_2 = new JLabel();
		label_2.setText("CKV：");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(485, 83, 47, 20);
		add(label_2);

		textField_6 = new JTextField();
		textField_6.setToolTipText("");
		textField_6.setEditable(false);
		textField_6.setBounds(538, 83, 111, 20);
		add(textField_6);

		JLabel label_3 = new JLabel();
		label_3.setText("CKV：");
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		label_3.setBounds(485, 113, 47, 20);
		add(label_3);

		textField_7 = new JTextField();
		textField_7.setToolTipText("");
		textField_7.setEditable(false);
		textField_7.setBounds(538, 113, 111, 20);
		add(textField_7);

		JPanel panel = new JPanel();
		panel.setBounds(10, 211, 770, 358);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.getTableHeader().setReorderingAllowed(true);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() != 2) {
						return;
					}
					int row = table.rowAtPoint(e.getPoint());
					// int colum = table.columnAtPoint(e.getPoint());

					issuerKeyInfo = issuerKeyInfo.getIssuerKeyInfo("ApplicationKey", table.getValueAt(row, 0).toString());

					chckbxDe.setSelected(issuerKeyInfo.getDerive() == 0 ? false : true);
					textField_4.setText(issuerKeyInfo.getKeyName());
					textField.setText(issuerKeyInfo.getAcKey());
					textField_1.setText(issuerKeyInfo.getMacKey());
					textField_2.setText(issuerKeyInfo.getEncKey());
				}else {
					int row=table.rowAtPoint(e.getPoint());
					currentSelectedKeyName=table.getValueAt(row, 0).toString();
					addPopup(table, popupMenu);
				}
				
			}
		});
		sdList = issuerKeyInfo.getIssuerKeyInfos("ApplicationKey");
		tableDataDisp();
		scrollPane.setViewportView(table);

		JMenuItem popmItem=new JMenuItem("删除");
		popmItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (WDAssert.isNotEmpty(currentSelectedKeyName)) {
					issuerKeyInfo.del(currentSelectedKeyName);
					sdList=issuerKeyInfo.getIssuerKeyInfos("ApplicationKey");
					tableDataDisp();
					table.repaint();
				}
			}
		});
		popupMenu.add(popmItem);
	}

	/**
	 * @Title: tableDataDisp
	 * @Description 将从数据库中查出的数据显示在table中
	 * @param
	 * @return
	 * @throws
	 */
	public void tableDataDisp() {
		int rowNum = sdList.size();
		tableData = new Object[rowNum][5];
		for (int i = 0; i < rowNum; i++) {
			tableData[i][0] = sdList.get(i).getKeyName();
			tableData[i][1] = sdList.get(i).getAcKey();
			tableData[i][2] = sdList.get(i).getMacKey();
			tableData[i][3] = sdList.get(i).getEncKey();
			tableData[i][4] = sdList.get(i).getDerive();
		}
		testDataTableModel = new DefaultTableModel(tableData, COLUMNS) {
			private static final long serialVersionUID = -9082031840487910439L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(testDataTableModel);
	}

	private void init() {
		setName(pm.getString("mv.issuerkeyconfig.name"));
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
