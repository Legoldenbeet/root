package com.echeloneditor.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.echeloneditor.actions.SystemShellExcuter;

public class SystemShell extends RSyntaxTextArea {
	public static byte STATUS_ERROR = (byte) -1;
	public static byte STATUS_OK = (byte) 0;
	public SystemShellExcuter systemShellExcuter;

	private static final long serialVersionUID = 5739259834646704913L;

	public SystemShell() {
		systemShellExcuter = new SystemShellExcuter(this);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					SystemShell systemShell = (SystemShell) e.getComponent();
					String res = systemShell.getText();
					res = res.substring(res.lastIndexOf('\n') + 1, res.length());
					systemShell.append("\n");
					if (res.equalsIgnoreCase("cls")) {
						systemShell.setText("");
					}else {
						try {
							systemShellExcuter.excute(new File("."), "cmd /c " + res);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
}
