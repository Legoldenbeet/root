package com.gerenhua.tool.panel;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.gerenhua.tool.logic.apdu.CommonAPDU;
import com.gerenhua.tool.logic.apdu.pcsc.PcscChannel;
import com.gerenhua.tool.utils.Config;
import com.gerenhua.tool.utils.FileUtil;
import com.gerenhua.tool.utils.PropertiesManager;
public class CardReaderPanel extends JPanel {

	private static final long serialVersionUID = -6360462745055001746L;
	public static JComboBox comboBox;
	private List<String> cardReaderList;
	private DefaultComboBoxModel comboBoxModel;
	private PropertiesManager pm = new PropertiesManager();
	public PcscChannel apduChannel = new PcscChannel();
	public static CommonAPDU commonAPDU;
	private JTextField resetTextField;
	public JButton btnNewButton_2;
	/**
	 * Create the panel
	 */
	public CardReaderPanel() {
		super();
		setLayout(null);

		final JLabel label_1 = new JLabel();
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setText(pm.getString("mc.cardreaderpanel.cardReader"));
		label_1.setBounds(-30, 11, 97, 20);
		add(label_1);

		comboBox = new JComboBox();
		comboBoxModel = new DefaultComboBoxModel();
		cardReaderList = apduChannel.getReaderList();
		// cardReaderList.add("10.0.97.248:5000");

		if (cardReaderList != null && cardReaderList.size() > 0) {
			comboBoxModel = new DefaultComboBoxModel(cardReaderList.toArray());
			comboBox.setModel(comboBoxModel);
			String reader = Config.getValue("Terminal_Data", "reader");
			if (cardReaderList.contains(reader)) {
				comboBox.setSelectedItem(reader);
			}
		}
		comboBox.setBounds(70, 11, 365, 20);
		comboBox.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				cardReaderList = apduChannel.getReaderList();
				if (cardReaderList != null && cardReaderList.size() > 0) {
					comboBoxModel = new DefaultComboBoxModel(cardReaderList.toArray());
					comboBox.setModel(comboBoxModel);
					comboBox.repaint();
				} else {
					comboBoxModel = new DefaultComboBoxModel();
					comboBox.setModel(comboBoxModel);
					comboBox.repaint();
				}
			}
		});
		add(comboBox);

		final JButton button = new JButton();
		button.setText("复位");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				if (comboBox.getSelectedItem() != null) {
					String reader = comboBox.getSelectedItem().toString();
					HashMap<String, String> res = commonAPDU.reset(reader);
					String atrS = "";
					if ("9000".equals(res.get("sw"))) {
						atrS = res.get("atr");
						resetTextField.setText(res.get("atr"));
						btnNewButton_2.setEnabled(true);
					} else {
						atrS = "";
					}
					JOptionPane.showMessageDialog(null, atrS + res.get("sw"));
					Config.setValue("Terminal_Data", "reader", reader);
				}
			}
		});
		button.setBounds(569, 11, 120, 21);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		add(button);

		final JButton btnNewButton = new JButton("打开");
		final JButton btnNewButton_1 = new JButton("关闭");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ExecutorService executorService = Executors.newSingleThreadExecutor();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							String reader = comboBox.getSelectedItem().toString();
							if (reader.indexOf(':') > 0) {
								String[] board = reader.split(":");
								FileUtil.updateBoradFile(board[0], board[1]);
							}
							commonAPDU = new CommonAPDU();
							boolean flag = commonAPDU.init(reader);
							if (flag) {
								btnNewButton.setEnabled(false);
								btnNewButton_1.setEnabled(true);
							}
						}
					});
					Future future = executorService.submit(thread);
					future.get(1, TimeUnit.SECONDS);
					executorService.shutdownNow();
				} catch (Exception e2) {
					if (!(e2 instanceof TimeoutException)) {
						e2.printStackTrace();
					}
				}

			}
		});
		btnNewButton.setBounds(447, 11, 120, 21);
		btnNewButton.setFocusPainted(false);
		btnNewButton.setBorderPainted(false);
		add(btnNewButton);

		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						commonAPDU.close();
						btnNewButton_1.setEnabled(false);
						btnNewButton.setEnabled(true);
					}
				});
			}
		});
		btnNewButton_1.setBounds(691, 11, 120, 21);
		btnNewButton_1.setFocusPainted(false);
		btnNewButton_1.setBorderPainted(false);
		add(btnNewButton_1);
		
		resetTextField = new JTextField();
		resetTextField.setBounds(70, 41, 365, 20);
		add(resetTextField);
		resetTextField.setColumns(10);
		
		btnNewButton_2 = new JButton("剪贴板");
		btnNewButton_2.setBorderPainted(false);
		btnNewButton_2.setFocusPainted(false);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection atr = new StringSelection(resetTextField.getText().trim());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(atr, null);
				((JButton)e.getSource()).setEnabled(false);
			}
		});
		btnNewButton_2.setBounds(447, 41, 120, 21);
		add(btnNewButton_2);
	}
}
