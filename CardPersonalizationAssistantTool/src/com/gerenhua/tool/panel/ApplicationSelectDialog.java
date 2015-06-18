package com.gerenhua.tool.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.watchdata.commons.lang.WDStringUtil;

public class ApplicationSelectDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<HashMap<String, String>> readDirList;
	public DefaultTableModel testDataTableModel;
	private JTable table;
	private final String[] COLUMNS = new String[] { "应用AID", "应用标签", "应用首选名称", "Application Priority Indicator" };

	/**
	 * Create the dialog.
	 */
	public ApplicationSelectDialog(JFrame frame, List<HashMap<String, String>> readDirList) {
		super(frame, "卡片应用列表", true);
		this.readDirList = readDirList;
		setBounds(100, 100, 671, 152);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JScrollPane jScrollPane = new JScrollPane();
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() == 2) {
						(SwingUtilities.getRoot((Component) e.getSource())).setVisible(false);
					}
				}
			}
		});
		tableDataDisp();
		jScrollPane.setViewportView(table);
		getContentPane().add(jScrollPane, BorderLayout.CENTER);
	}

	public String getSelectedAID() {
		int row = table.getSelectedRow();
		return table.getValueAt(row, 0).toString();
	}

	/**
	 * @Title: tableDataDisp
	 * @Description 将从数据库中查出的数据显示在table中
	 * @param
	 * @return
	 * @throws
	 */
	public void tableDataDisp() {
		int rowNum = readDirList.size();
		Object[][] tableData = new Object[rowNum][4];
		for (int i = 0; i < rowNum; i++) {
			tableData[i][0] = readDirList.get(i).get("4F");
			tableData[i][1] = WDStringUtil.hex2asc(readDirList.get(i).get("50"));
			tableData[i][2] = WDStringUtil.hex2asc(readDirList.get(i).get("9F12"));
			tableData[i][3] = readDirList.get(i).get("87");
		}

		testDataTableModel = new DefaultTableModel(tableData, COLUMNS) {
			private static final long serialVersionUID = -9082031840487910439L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.getTableHeader().setReorderingAllowed(true);
		table.setModel(testDataTableModel);
	}
}
