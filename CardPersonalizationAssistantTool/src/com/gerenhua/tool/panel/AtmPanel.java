package com.gerenhua.tool.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.logic.impl.TradeThread;
import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.FileUtil;
import com.gerenhua.tool.utils.PropertiesManager;

public class AtmPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JTextField moneyTextField;
	public static String proPath = System.getProperty("user.dir");
	public static String reportDir = proPath + "/report/";
	public static JButton qPBOCButton;
	public static JButton lendButton;
	public static JButton earmarkButton;
	public static JButton ecashButton;
	private static JLabel enterMoneyLabel;
	private PropertiesManager pm = new PropertiesManager();
	private String money = new String("");
	public static String tradeType = "";
	public CommonAPDU apduHandler;
	private static JTable table;
	private static DefaultTableModel testDataTableModel = null;
	private static Object[][] tableData = null;
	private JPopupMenu popupMenu = new JPopupMenu();

	// 终端性能列表，与配置界面上的配置型一致，从第一个字节开始
	public enum TerminalSupportType {
		// 依次为：接触式IC、磁条、手工键盘输入、证件验证、无需CVM、签名、联机PIN、
		// IC卡明文PIN校验、支持CDA、吞卡、支持DDA、支持SDA
		TOUCHIC, TRACK, KEYBOARD, CERTIFICATECHECK, NOCVM, SIGN, LINKPIN, ICPINCHECK, SUPPORTCDA, EATCARD, SUPPORTDDA, SUPPORTSDA;
	}

	/**
	 * Create the panel
	 * 
	 * @throws IOException
	 * 
	 * @throws Exception
	 */
	public AtmPanel(final JTextPane textPane, final JTextPane textPane1) throws IOException {
		// super(ImageIO.read(AtmPanel.class.getResource("/com/gerenhua/tool/resources/images/trade.png")));
		setLayout(null);

		enterMoneyLabel = new JLabel();
		enterMoneyLabel.setText("金额");
		enterMoneyLabel.setBounds(6, 154, 40, 23);
		add(enterMoneyLabel);

		moneyTextField = new JTextField();
		moneyTextField.setText("10");
		moneyTextField.setBounds(46, 154, 60, 23);
		add(moneyTextField);

		qPBOCButton = new JButton();
		qPBOCButton.setText("QPBOC");
		qPBOCButton.setBounds(6, 121, 100, 23);
		qPBOCButton.setFocusPainted(false);
		qPBOCButton.setBorderPainted(false);
		add(qPBOCButton);
		qPBOCButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tradeType = pm.getString("mv.tradepanel.qPBOC");
				// 更新交易状态
				Config.setValue("Terminal_Data", "currentTradeType", tradeType);
				// 设置检测报告按钮不可用
				// reportButton.setEnabled(false);
				money = moneyTextField.getText().trim();

				TradeThread tradeThread = new TradeThread(money, tradeType, textPane);
				Thread thread = new Thread(tradeThread);
				thread.start();
			}
		});

		lendButton = new JButton();
		lendButton.setText("借贷记");
		lendButton.setFocusPainted(false);
		lendButton.setBorderPainted(false);
		lendButton.setBounds(6, 22, 100, 23);
		add(lendButton);
		lendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tradeType = pm.getString("mv.tradepanel.lend");
				// 更新交易状态
				Config.setValue("Terminal_Data", "currentTradeType", tradeType);
				// 设置检测报告按钮不可用
				// reportButton.setEnabled(false);
				money = moneyTextField.getText().trim();
				TradeThread tradeThread = new TradeThread(money, tradeType, textPane);
				Thread thread = new Thread(tradeThread);
				thread.start();
			}
		});

		ecashButton = new JButton();
		ecashButton.setText("电子现金");
		ecashButton.setFocusPainted(false);
		ecashButton.setBorderPainted(false);
		ecashButton.setBounds(6, 88, 100, 23);
		add(ecashButton);
		ecashButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tradeType = pm.getString("mv.tradepanel.ecash");
				// 更新交易状态
				Config.setValue("Terminal_Data", "currentTradeType", tradeType);
				// 设置检测报告按钮不可用
				// reportButton.setEnabled(false);
				money = moneyTextField.getText().trim();
				TradeThread tradeThread = new TradeThread(money, tradeType, textPane);
				Thread thread = new Thread(tradeThread);
				thread.start();
			}
		});

		earmarkButton = new JButton();
		earmarkButton.setText("圈存");
		earmarkButton.setFocusPainted(false);
		earmarkButton.setBorderPainted(false);
		earmarkButton.setBounds(6, 55, 100, 23);
		add(earmarkButton);
		earmarkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tradeType = pm.getString("mv.tradepanel.earmark");
				// 更新交易状态
				Config.setValue("Terminal_Data", "currentTradeType", tradeType);
				// 设置检测报告按钮不可用
				// reportButton.setEnabled(false);
				money = moneyTextField.getText().trim();
				TradeThread tradeThread = new TradeThread(money, tradeType, textPane);
				Thread thread = new Thread(tradeThread);
				thread.start();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 187, 100, 300);
		add(scrollPane);

		table = new JTable();
		tableDataDisp();
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {
					popupMenu.show(table, e.getX(), e.getY());
				}
				if (e.getClickCount()==2&&SwingUtilities.isLeftMouseButton(e)) {
					int row=table.getSelectedRow();
					openDoc(reportDir+table.getValueAt(row, 0).toString());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int colum = table.columnAtPoint(e.getPoint());
				Object ob = table.getValueAt(row, colum);
				table.setToolTipText(reportDir + ob.toString());
				table.repaint();
			}
		});
		scrollPane.setViewportView(table);

		JMenuItem menuItem = new JMenuItem("打开");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row=table.getSelectedRow();
				String fileName=table.getValueAt(row, 0).toString();
				String filePath = reportDir + fileName;
				
				File file = new File(filePath);
				if (file.exists()) {
					Object[] options = { "打开", "保存" };
					int ret = JOptionPane.showOptionDialog(null, "交易检测报告", "提示框", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					if (ret == JOptionPane.YES_OPTION) {
						openDoc(filePath);
					} else if (ret == JOptionPane.NO_OPTION) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setCurrentDirectory(new File("."));
						fileChooser.setFileFilter(new FileFilter() {

							@Override
							public String getDescription() {
								// TODO Auto-generated method stub
								return "mcrosoft office word 文档";
							}

							@Override
							public boolean accept(File f) {
								// TODO Auto-generated method stub
								if (f.getName().endsWith(".doc") ||f.getName().endsWith(".docx")|| f.isDirectory()) {
									return true;
								} else {
									return false;
								}
							}
						});
						fileChooser.setSelectedFile(new File(filePath));
						ret = fileChooser.showSaveDialog(null);
						if (ret == JFileChooser.APPROVE_OPTION) {
							FileUtil.copyFile(filePath, fileChooser.getSelectedFile().getAbsolutePath());
							JOptionPane.showMessageDialog(null, "保存成功！");
						} else {
							return;
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "检测报告不存在！", "提示框", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		popupMenu.add(menuItem);
		JMenuItem menuItemDel = new JMenuItem("删除");
		menuItemDel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] row=table.getSelectedRows();
				for (int i : row) {
					String fileName=table.getValueAt(i, 0).toString();
					String filePath = reportDir + fileName;
					
					File file = new File(filePath);
					if (file.exists()) {
						file.delete();
					}
				}
				tableDataDisp();
			}
		});
		popupMenu.add(menuItemDel);
	}

	public static void tableDataDisp() {
		File reportDirectory = new File(reportDir);
		File[] reports = null;
		if (reportDirectory.exists() && reportDirectory.isDirectory()) {
			reports = reportDirectory.listFiles();
		}

		int rowNum = reports.length;
		tableData = new Object[rowNum][1];
		for (int i = 0; i < rowNum; i++) {
			tableData[i][0] = reports[i].getName();
		}
		testDataTableModel = new DefaultTableModel(tableData, new String[] { "交易记录" }) {
			private static final long serialVersionUID = -9082031840487910439L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(testDataTableModel);
	}

	private void openDoc(String filePath){
		// word
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" \"" + filePath + "\"");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "打开文件失败，位置：" + filePath + "请手动操作！");
			return;
		}
	}
	public static boolean decimalDigitsLimit(String moneyStr) {
		String eg = "^[0-9]{1,3}([.]{1}[0-9]{0,2})?$";
		Matcher m = Pattern.compile(eg, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(moneyStr);
		return m.find() ? true : false;

	}
}
