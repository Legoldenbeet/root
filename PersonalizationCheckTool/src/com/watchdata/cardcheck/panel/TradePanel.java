package com.watchdata.cardcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

public class TradePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JTextPane textPane;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public TradePanel() throws IOException {
		setLayout(null);
		
		textPane =new JTextPane(){
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
		
		AtmPanel atmPanel = new AtmPanel(textPane);
		atmPanel.setBounds(0, 0, 838, 556);
		add(atmPanel);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 555, 838, 132);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane);
		
		
		scrollPane.setViewportView(textPane);

	}
}
