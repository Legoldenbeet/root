package com.echeloneditor.os;

import javax.swing.Action;
import javax.swing.Icon;

public class JButton extends javax.swing.JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButton() {
		repaintButton();
	}

	public JButton(Icon icon) {
		super(icon);
		repaintButton();
	}

	public JButton(String text) {
		super(text);
		repaintButton();
	}

	public JButton(Action a) {
		super(a);
		repaintButton();
	}

	public JButton(String text, Icon icon) {
		super(text, icon);
		repaintButton();
	}

	public void repaintButton(){
		if (OsConstants.isWindows()) {
			this.setFocusPainted(false);
			this.setBorderPainted(false);
		}
	}
}
