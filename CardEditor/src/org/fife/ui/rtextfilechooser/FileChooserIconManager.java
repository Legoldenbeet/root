package org.fife.ui.rtextfilechooser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

class FileChooserIconManager {
	protected Icon folderIcon;
	protected Icon hardDriveIcon;
	protected Icon floppyDriveIcon;
	protected Icon computerIcon;
	protected Icon fileIcon;
	protected Icon errorIcon;
	protected Icon validIcon;
	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private Map<File, Icon> iconCache;

	public FileChooserIconManager() {
		this.iconCache = new HashMap(50);
		createDefaultIcons();
	}

	public void clearIconCache() {
		this.iconCache.clear();
	}

	protected void createDefaultIcons() {
		this.folderIcon = createFolderIcon();

		this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
		this.floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");
		this.fileIcon = UIManager.getIcon("FileView.fileIcon");
		String path = "org/fife/ui/rtextfilechooser/images/";
		String pathTest = "com/echeloneditor/resources/images/";
		ClassLoader cl = getClass().getClassLoader();
		if (this.hardDriveIcon == null)
			this.hardDriveIcon = loadIcon(cl, path + "harddrive.gif");
		if (this.floppyDriveIcon == null)
			this.floppyDriveIcon = loadIcon(cl, path + "floppydrive.gif");
		if (this.fileIcon == null)
			this.fileIcon = loadIcon(cl, path + "file.gif");
		if (this.errorIcon == null) {
			this.errorIcon = loadIcon(cl, pathTest + "error_24.png");
		}
		if (this.validIcon == null) {
			this.validIcon = loadIcon(cl, pathTest + "valid_24.png");
		}
	}

	public static Icon createFolderIcon() {
		Icon folderIcon = null;
		try {
			File temp = File.createTempFile("FileSystemTree", ".tmp");
			temp.delete();
			temp.mkdir();
			if (temp.isDirectory()) {
				folderIcon = FileSystemView.getFileSystemView().getSystemIcon(temp);

				temp.delete();
			}
		} catch (Exception e) {
		}
		if (folderIcon == null) {
			folderIcon = UIManager.getIcon("FileView.directoryIcon");

			if (folderIcon == null) {
				String path = "org/fife/ui/rtextfilechooser/images/";
				ClassLoader cl = FileChooserIconManager.class.getClassLoader();

				folderIcon = loadIcon(cl, path + "directory.gif");
			}

		}

		return folderIcon;
	}

	public Icon getFolderIcon() {
		return this.folderIcon;
	}

	public Icon getIcon(File f) {
		Icon icon = null;

		if (f != null) {
			icon = (Icon) this.iconCache.get(f);
			if (icon != null) {
				return icon;
			}

			if ((f.exists()) || (RootManager.getInstance().isRoot(f))) {
				try {
					icon = fileSystemView.getSystemIcon(f);
				} catch (Exception fnfe) {
				}

			}

			if (icon == null) {
				if (fileSystemView.isFloppyDrive(f)) {
					icon = this.floppyDriveIcon;
				} else if (fileSystemView.isDrive(f)) {
					icon = this.hardDriveIcon;
				} else if (fileSystemView.isComputerNode(f)) {
					icon = this.computerIcon;
				} else if (f.isDirectory()) {
					icon = this.folderIcon;
				} else {
					icon = this.fileIcon;
				}

			}
			if (f.isFile() && f.getName().endsWith("Error.txt")) {
				if (f.length() > 0) {
					icon = this.errorIcon;
				} else {
					icon = this.validIcon;
				}
			}

			this.iconCache.put(f, icon);
		}

		return icon;
	}

	private static Icon loadIcon(ClassLoader cl, String file) {
		return new ImageIcon(cl.getResource(file));
	}

	public Icon removeIconFor(File file) {
		return (Icon) this.iconCache.remove(file);
	}
}