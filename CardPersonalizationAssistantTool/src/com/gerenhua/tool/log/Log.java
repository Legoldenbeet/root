package com.gerenhua.tool.log;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

public class Log {
	private static Logger logger = Logger.getLogger(Logger.class);
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	public static JTextPane jTextPane;

	public static final int LOG_COLOR_BLACK=0;
	public static final int LOG_COLOR_GREEN=1;
	public static final int LOG_COLOR_RED=2;
	public static final int LOG_COLOR_BLUE=3;
	
	public Log() {
	}

	public void setLogArea(JTextPane textPane) {
		textPane.setText("");//cleal 
		this.jTextPane = textPane;
	}

	public void debug(String info) {
		logger.debug(info);
		out(outStr("DEBUG", info), 4);
	}

	public void debug(String info, int startFlag) {
		//logger.debug(info);
		if (startFlag == 0) {
			out(outStr("DEBUG", info), 0);
		}else {
			out(outStr("DEBUG", info), 4);
		}
	}

	public void info(String info) {
		logger.info(info);
		out(outStrNoDate(info),3);
	}

	public void warn(String info) {
		logger.warn(info);
		out(outStr("WARN", info), 1);
	}

	public void error(String info) {
		logger.error(info);
		out(outStr("ERROR", info), 2);
	}

	public void error(String info, Exception e) {
		logger.error(info, e);
		out(outStr("ERROR", info), 2);
	}

	private String outStr(String infoType, String info) {
		return sf.format(new Date()) + " " + infoType + " " + info + "\n";
	}

	private String outStrNoDate(String info) {
		return " \t\t\t" + info + "\n";
	}

	public void out(String info,int command) {
		if (jTextPane != null) {
			SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();

			if (command == LOG_COLOR_BLACK) {
//				jTextPane.setText("");
				StyleConstants.setForeground(simpleAttributeSet, Color.BLACK);
			}else if (command == LOG_COLOR_GREEN) {
				StyleConstants.setForeground(simpleAttributeSet, Color.GREEN);
			} else if (command == LOG_COLOR_RED) {
				StyleConstants.setForeground(simpleAttributeSet, Color.RED);
			} else if (command == LOG_COLOR_BLUE) {
				StyleConstants.setForeground(simpleAttributeSet, Color.BLUE);
				StyleConstants.setBold(simpleAttributeSet, true);
			} else {
				StyleConstants.setForeground(simpleAttributeSet, Color.BLACK);
			}
			addLog(info,simpleAttributeSet);
		}
	}

	public void addLog(String info,SimpleAttributeSet attr) {
		StyledDocument doc = jTextPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), info, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		jTextPane.setCaretPosition(doc.getLength());
	}
}
