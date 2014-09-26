package com.echeloneditor.actions;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.echeloneditor.listeners.EditorPaneListener;
import com.echeloneditor.listeners.SimpleDragFileListener;
import com.echeloneditor.main.CloseableTabComponent;
import com.echeloneditor.main.FontWidthRuler;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.FontUtil;
import com.echeloneditor.utils.ImageHelper;
import com.echeloneditor.utils.SwingUtils;
import com.echeloneditor.vo.StatusObject;

public class FileHander {
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public FontWidthRuler ruler;
	public static String lineSeparator = System.getProperty("line.separator");
	public static String SystemFileEncode = System.getProperty("file.encoding");

	public RSyntaxTextArea textArea;

	public FileHander(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
	}

	public void openFileWithFilePath(String filePath) {
		// 打开文件
		try {
			File file = new File(filePath);

			long fileSize = file.length();
			long bigFileSzie = Integer.parseInt(Config.getValue("CONFIG", "bigFileSize"));
			// 更新状态栏文件编码信息

			statusObject.showFileSize(fileSize);
			statusObject.addItemAndSelected(SystemFileEncode, true);

			String fileContentType = SwingUtils.getFileContentType(file.getName());
			textArea = SwingUtils.createTextArea();

			LanguageSupportFactory lsf = LanguageSupportFactory.get();
			LanguageSupport support = lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
			JavaLanguageSupport jls = (JavaLanguageSupport) support;
			// TODO: This API will change! It will be easier to do per-editor
			// changes to the build path.
			try {
				jls.getJarManager().addCurrentJreClassFileSource();
				// jsls.getJarManager().addClassFileSource(ji);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			jls.setShowDescWindow(true);
			jls.setParameterAssistanceEnabled(true);
			jls.setAutoActivationEnabled(true);

			lsf.register(textArea);

			textArea.setSyntaxEditingStyle(fileContentType);

			EditorPaneListener editlistener = new EditorPaneListener(tabbedPane, statusObject);
			new DropTarget(textArea, DnDConstants.ACTION_COPY_OR_MOVE, new SimpleDragFileListener(tabbedPane, statusObject), true);
			textArea.addMouseListener(editlistener);
			textArea.addMouseMotionListener(editlistener);
			textArea.addKeyListener(editlistener);
			textArea.getDocument().addDocumentListener(editlistener);

			RTextScrollPane sp = new RTextScrollPane(textArea);
			sp.setFoldIndicatorEnabled(true);

			Gutter gutter = sp.getGutter();
			gutter.setBookmarkingEnabled(true);
			ImageIcon ii = ImageHelper.loadImage("bookmark.png");
			gutter.setBookmarkIcon(ii);

			InputStream in = getClass().getResourceAsStream("/com/echeloneditor/resources/templates/eclipse.xml");
			try {
				Theme theme = Theme.load(in);
				theme.apply(textArea);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			// 加入标尺
			ruler = new FontWidthRuler(FontWidthRuler.HORIZONTAL, 10, textArea);
			ruler.addSpin(3);
			ruler.NeedPaint = true;
			sp.setColumnHeaderView(ruler);

			int tabCount = tabbedPane.getTabCount();
			CloseableTabComponent closeableTabComponent = new CloseableTabComponent(tabbedPane, statusObject);
			closeableTabComponent.setFilePath(file.getPath());
			closeableTabComponent.setFileEncode(SystemFileEncode);
			closeableTabComponent.setFileSzie(fileSize);

			tabbedPane.add("New Panel", sp);
			tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);

			tabbedPane.setSelectedComponent(sp);
			// 设置选项卡title为打开文件的文件名
			SwingUtils.setTabbedPaneTitle(tabbedPane, file.getName());

			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, SystemFileEncode), 10 << 20);

			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					textArea.append(line + lineSeparator);
				}
				fis.close();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			String res = Config.getValue("CURRENT_THEME", "current_font");
			textArea.setFont(FontUtil.getFont(res));
			statusObject.showSaveButton(false);

			textArea.setCaretPosition(0);
			textArea.requestFocusInWindow();
			closeableTabComponent.setModify(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void saveFile(String filePath, String fileEncode) {
		// 打开文件
		FileAction fileAction = new FileAction();
		try {
			fileAction.save(filePath, SwingUtils.getContent(tabbedPane), fileEncode);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void newFile() {
		RSyntaxTextArea textArea = SwingUtils.createTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);

		EditorPaneListener editlistener = new EditorPaneListener(tabbedPane, statusObject);
		textArea.addMouseListener(editlistener);
		textArea.addMouseMotionListener(editlistener);
		textArea.addKeyListener(editlistener);
		textArea.getDocument().addDocumentListener(editlistener);

		RTextScrollPane sp = new RTextScrollPane(textArea);
		sp.setFoldIndicatorEnabled(true);

		Gutter gutter = sp.getGutter();
		gutter.setBookmarkingEnabled(true);
		ImageIcon ii = ImageHelper.loadImage("bookmark.png");
		gutter.setBookmarkIcon(ii);

		InputStream in = getClass().getResourceAsStream("/com/echeloneditor/resources/templates/eclipse.xml");
		try {
			Theme theme = Theme.load(in);
			theme.apply(textArea);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// 加入标尺
		ruler = new FontWidthRuler(FontWidthRuler.HORIZONTAL, 10, textArea);
		ruler.addSpin(3);
		ruler.NeedPaint = true;
		sp.setColumnHeaderView(ruler);

		int tabCount = tabbedPane.getTabCount();
		CloseableTabComponent closeableTabComponent = new CloseableTabComponent(tabbedPane, statusObject);
		closeableTabComponent.setFileEncode("UTF-8");
		closeableTabComponent.setFileSzie(0);
		closeableTabComponent.setModify(false);
		tabbedPane.add("New Panel", sp);
		tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);

		tabbedPane.setSelectedComponent(sp);
		// 设置选项卡title为打开文件的文件名
		SwingUtils.setTabbedPaneTitle(tabbedPane, "New Panel");

		String res = Config.getValue("CURRENT_THEME", "current_font");

		textArea.setFont(FontUtil.getFont(res));
		statusObject.showSaveButton(false);

		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
	}
}
