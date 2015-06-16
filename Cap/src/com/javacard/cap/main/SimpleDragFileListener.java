package com.javacard.cap.main;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import javax.swing.JTree;

public class SimpleDragFileListener implements DropTargetListener {
	private JTree tree;

	public SimpleDragFileListener(JTree jTree) {
		this.tree = jTree;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		// TODO Auto-generated method stub
		DataFlavor[] dataFlavors = dtde.getCurrentDataFlavors();
		if (dataFlavors[0].match(DataFlavor.javaFileListFlavor)) {
			try {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				Transferable tr = dtde.getTransferable();
				Object obj = tr.getTransferData(DataFlavor.javaFileListFlavor);
				if (obj instanceof List<?>) {
					List<File> files = (List<File>) obj;
					File[] fileList = (File[]) files.toArray();
					CapInsight.addCapFile(fileList);
				}
				dtde.dropComplete(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
