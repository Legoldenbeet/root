package com.echeloneditor.actions;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.universalchardet.UniversalDetector;

import com.echeloneditor.listeners.EditorPaneListener;
import com.echeloneditor.listeners.SimpleDragFileListener;
import com.echeloneditor.main.CloseableTabComponent;
import com.echeloneditor.main.FontWidthRuler;
import com.echeloneditor.utils.Config;
import com.echeloneditor.utils.Debug;
import com.echeloneditor.utils.FontUtil;
import com.echeloneditor.utils.ImageHelper;
import com.echeloneditor.utils.SwingUtils;
import com.echeloneditor.vo.StatusObject;

public class FileHander {
	public static ConcurrentHashMap<String, Long> fileDescMapBean = new ConcurrentHashMap<String, Long>();
	public static long currentCharPos = 0;
	public JTabbedPane tabbedPane;
	public StatusObject statusObject;
	public FontWidthRuler ruler;
	public CloseableTabComponent closeableTabComponent;
	public RSyntaxTextArea textArea;
	private static FileInputStream fis;
	private static BufferedInputStream bis;
	private static String tmp;
	private static byte[] bytes;
	public static String currentEncode;
	public UniversalDetector detector;

	public FileHander(JTabbedPane tabbedPane, StatusObject statusObject) {
		this.tabbedPane = tabbedPane;
		this.statusObject = statusObject;
		bytes = new byte[FileAction.BIG_FILE_READ_UNIT_SIZE];// 缓冲区

		detector = new UniversalDetector(null);
	}

	public void openFileWithFilePath(String filePath, String fileEncode) {
		// 打开文件
		try {
			currentEncode = fileEncode;
			boolean isBigFile = false;
			File file = new File(filePath);
			String fileName = file.getName();
			long fileSize = file.length();

			if (fileSize > (FileAction.BIG_FILE_SIZE << 20)) {
				isBigFile = true;
			}
			// 更新状态栏文件编码信息
			statusObject.showFileSize(fileSize);
			RTextScrollPane rTextScrollPane = SwingUtils.getExistComponent(tabbedPane, filePath);
			if (fileDescMapBean.containsKey(filePath) && rTextScrollPane != null) {
				tabbedPane.setSelectedComponent(rTextScrollPane);

				SwingUtils.showTitleFilePath(tabbedPane);
				if (isBigFile) {
					textArea = SwingUtils.getRSyntaxTextArea(tabbedPane);
					currentCharPos = fileDescMapBean.get(filePath);

					if (currentCharPos >= fileSize) {
						JOptionPane.showMessageDialog(SwingUtilities.getRoot(tabbedPane), "已到最后一页");
						return;
					}
				} else {
					statusObject.showViewBtn(false);
					return;
				}
			} else {
				currentCharPos = 0;
				String fileContentType = SwingUtils.getFileContentType(fileName);
				textArea = SwingUtils.createTextArea();

				LanguageSupportFactory lsf = LanguageSupportFactory.get();
				LanguageSupport support = lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
				JavaLanguageSupport jls = (JavaLanguageSupport) support;
				// TODO: This API will change! It will be easier to do
				// per-editor
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
				closeableTabComponent = new CloseableTabComponent(tabbedPane, statusObject);
				closeableTabComponent.setFilePath(file.getPath());
				closeableTabComponent.setFileEncode(currentEncode);
				closeableTabComponent.setFileSzie(fileSize);
				closeableTabComponent.setFileNameExt(fileName.substring(fileName.lastIndexOf(".")));
				closeableTabComponent.setLastModifyTime(file.lastModified());
				tabbedPane.add("New Panel", sp);
				tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);

				tabbedPane.setSelectedComponent(sp);
				// 设置选项卡title为打开文件的文件名
				SwingUtils.setTabbedPaneTitle(tabbedPane, fileName);
			}
			// 清空编辑区
			textArea.setText("");
			String res = Config.getValue("CURRENT_THEME", "current_font");
			textArea.setFont(FontUtil.getFont(res));

			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis, FileAction.BUFFER_SIZE);
			// BufferedReader br = new BufferedReader(new InputStreamReader(fis, FileAction.DEFAULT_FILE_ENCODE), FileAction.BUFFER_SIZE);
			int count = 0;// 缓存计数器
			try {
				if (currentCharPos < fileSize) {
					bis.skip(currentCharPos);
					count = bis.read(bytes, 0, FileAction.BIG_FILE_READ_UNIT_SIZE);

					if (currentCharPos == 0) {
						detector.handleData(bytes, 0, count);
						detector.dataEnd();
						String encoding = detector.getDetectedCharset();
						if (encoding != null) {
							if (encoding.equalsIgnoreCase("UTF-8") || encoding.startsWith("GB")) {
								currentEncode = encoding;
							}
							statusObject.SelectEncodeItem(currentEncode);
							System.out.println("Detected encoding = " + encoding);
						} else {
							currentEncode = FileAction.DEFAULT_FILE_ENCODE;
							System.out.println("No encoding detected. use default charset：" + currentEncode);
						}
					}
					tmp = new String(bytes, 0, count, currentEncode);
					textArea.append(tmp);
					currentCharPos += count;
				}
			} catch (IOException e) {
				currentEncode = FileAction.DEFAULT_FILE_ENCODE;
				e.printStackTrace();
				Debug.log.debug(e.getMessage());
			} finally {
				fileDescMapBean.put(filePath, currentCharPos);
				Debug.log.debug(fileDescMapBean);
				if (fis != null) {
					fis.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (tmp != null) {
					tmp = null;
				}
				detector.reset();
			}
			statusObject.showSaveButton(false);
			if (isBigFile && currentCharPos != 0) {
				SwingUtils.getCloseableTabComponent(tabbedPane).setModify(false);
			} else {
				closeableTabComponent.setModify(false);
			}
			statusObject.showViewBtn(isBigFile);
			statusObject.showSepp(true);
			SwingUtils.showTitleFilePath(tabbedPane);
			// textArea.setCaretPosition(0);
			textArea.requestFocusInWindow();
		} catch (IOException e1) {
			e1.printStackTrace();
			Debug.log.debug(e1.getMessage());
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

	public void newFile(String fileNameExt) {
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
		closeableTabComponent.setFileEncode(FileAction.DEFAULT_FILE_ENCODE);
		closeableTabComponent.setFileSzie(0);
		closeableTabComponent.setFileNameExt(fileNameExt);
		closeableTabComponent.setModify(false);
		tabbedPane.add("New Panel", sp);
		tabbedPane.setTabComponentAt(tabCount, closeableTabComponent);

		tabbedPane.setSelectedComponent(sp);
		// 设置选项卡title为打开文件的文件名
		SwingUtils.setTabbedPaneTitle(tabbedPane, "New File");
		((JFrame) SwingUtilities.getRoot(tabbedPane)).setTitle("New File");
		String res = Config.getValue("CURRENT_THEME", "current_font");

		textArea.setFont(FontUtil.getFont(res));
		statusObject.showSaveButton(false);

		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
	}

	public static void main(String[] args) throws IOException {
		byte[] buf = new byte[4096];
		java.io.FileInputStream fis = new java.io.FileInputStream("G:\\1000_hotel_csv.csv");
		UniversalDetector detector = new UniversalDetector(null);
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}
		detector.reset();

	}
}
