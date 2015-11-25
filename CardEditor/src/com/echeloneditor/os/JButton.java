package com.echeloneditor.os;

import javax.swing.Action;
import javax.swing.Icon;

public class JButton extends javax.swing.JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButton() {
		if (OsConstants.isWindows()) {
			this.setFocusPainted(false);
			this.setBorderPainted(false);
		}
	}

	public JButton(Icon icon) {
		super(icon);
		// TODO Auto-generated constructor stub
	}

	public JButton(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}

	public JButton(Action a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	public JButton(String text, Icon icon) {
		super(text, icon);
		// TODO Auto-generated constructor stub
	}

}
